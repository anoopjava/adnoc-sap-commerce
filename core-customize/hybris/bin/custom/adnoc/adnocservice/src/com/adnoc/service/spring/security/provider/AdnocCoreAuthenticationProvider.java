package com.adnoc.service.spring.security.provider;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.enums.PartnerFunction;
import de.hybris.platform.audit.AuditableActions;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.jalo.JaloConnection;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.JaloSystemException;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.spring.security.CoreAuthenticationProvider;
import de.hybris.platform.spring.security.CoreUserDetails;
import de.hybris.platform.validator.AuthenticationValidator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CharacterPredicate;
import org.apache.commons.text.RandomStringGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;

import java.security.SecureRandom;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class AdnocCoreAuthenticationProvider extends CoreAuthenticationProvider
{
    private static final Logger LOG = LogManager.getLogger(AdnocCoreAuthenticationProvider.class);

    private ModelService modelService;
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private final UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();
    private List<AuthenticationValidator<User>> validators;
    private AuthenticationValidator<User> userAuthenticationValidator;
    private AdnocB2BUnitService adnocB2BUnitService;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException
    {
        LOG.info("Authenticating user: {}", authentication.getName());
        if (Registry.hasCurrentTenant() && JaloConnection.getInstance().isSystemInitialized())
        {
            try
            {
                return authenticateInternal(authentication);
            }
            catch (final JaloSystemException e)
            {
                throw new CoreAuthenticationProvider.CoreAuthenticationException("Error during authentication", e);
            }
        }
        else
        {
            final RandomStringGenerator.Builder var10000 = (new RandomStringGenerator.Builder()).withinRange(48, 122).filteredBy(new CharacterPredicate[]{Character::isLetterOrDigit});
            final SecureRandom secureRandom = new SecureRandom();
            final RandomStringGenerator credentialGenerator = var10000.usingRandom(secureRandom::nextInt).get();
            final String dummyCredential = credentialGenerator.generate(20);
            return createSuccessAuthentication(authentication, new CoreUserDetails(dummyCredential, dummyCredential, true, false, true, true, Collections.emptyList(), (String) null, (PK) null));
        }
    }

    private Authentication authenticateInternal(final Authentication authentication)
    {
        final String username = Objects.isNull(authentication.getPrincipal()) ? "NONE_PROVIDED" : authentication.getName();

        final UserDetails userDetails;
        try
        {
            userDetails = retrieveUser(username);
        }
        catch (final UsernameNotFoundException notFound)
        {
            throw new BadCredentialsException(messages.getMessage("CoreAuthenticationProvider.badCredentials", "Bad credentials"), notFound);
        }

        final User user = createUser(userDetails);
        validateUserAuthentication(user);
        validateUserDetails(userDetails);
        final AuditableActions.ActionBuilder auditActionBuilder = AuditableActions.builder();
        auditActionBuilder.withAttribute("user", user.getPK());
        additionalAuthenticationChecks(userDetails, (AbstractAuthenticationToken) authentication);
        postAuthenticationChecks.check(userDetails);
        updateUserLastLogin(user.getPK());
        JaloSession.getCurrentSession().setUser(user);
        AuditableActions.audit(auditActionBuilder.withName("successful user authentication"));
        return createSuccessAuthentication(authentication, userDetails);
    }

    private void updateUserLastLogin(final PK pk)
    {
        final UserModel userModel = modelService.get(pk);
        if (userModel instanceof final B2BCustomerModel b2BCustomerModel)
        {
            validateAccountActiveness(b2BCustomerModel);
            LOG.info("appEvent=login,authentication successful for user: {}", userModel.getUid());
        }
        userModel.setLastLogin(userModel.getCurrentLoginTime());
        final ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.systemDefault());
        userModel.setCurrentLoginTime(Date.from(zonedDateTime.toInstant()));
        modelService.save(userModel);
        modelService.refresh(userModel);
    }

    private void validateAccountActiveness(final B2BCustomerModel b2BCustomerModel)
    {
        final boolean isAdmin = b2BCustomerModel.getAllGroups().stream().anyMatch(principalGroupModel -> StringUtils.equals(principalGroupModel.getUid(), "b2badmingroup"));
        final String contactPerson = isAdmin ? "ADNOC sales manager" : "your Admin";
        final String errorMessage = String.format("Your account is blocked, please contact %s", contactPerson);

        if (Boolean.FALSE.equals(b2BCustomerModel.getActive()))
        {
            LOG.warn("appEvent=Login, customer '{}' is inactive. Account blocked.", b2BCustomerModel.getUid());
            throw new InvalidGrantException(errorMessage);
        }
        final B2BUnitModel soldToB2BUnitModel = getAdnocB2BUnitService().getSoldToB2BUnit(b2BCustomerModel);
        if (Objects.isNull(soldToB2BUnitModel) || Boolean.FALSE.equals(soldToB2BUnitModel.getActive()))
        {
            LOG.warn("appEvent=Login, soldTo customer '{}' with unit'{}' is inactive. Account blocked.", b2BCustomerModel.getUid(), soldToB2BUnitModel);
            throw new InvalidGrantException(errorMessage);
        }

        final B2BUnitModel payerB2BUnitModel = b2BCustomerModel.getAllGroups().stream()
                .filter(B2BUnitModel.class::isInstance).map(B2BUnitModel.class::cast).filter(b2BUnitModel -> Objects.equals(PartnerFunction.PY, b2BUnitModel.getPartnerFunction()))
                .findAny().orElse(null);
        if (Objects.nonNull(payerB2BUnitModel) && Boolean.FALSE.equals(payerB2BUnitModel.getActive()))
        {
            LOG.warn("appEvent=Login, payer customer '{}' with unit'{}' is inactive. Account blocked.", b2BCustomerModel.getUid(), payerB2BUnitModel.getUid());
            throw new InvalidGrantException(errorMessage);
        }
    }

    private void validateUserAuthentication(final User user)
    {
        if (validators != null)
        {
            validators.forEach((validator) -> validator.validate(user));
        }
        else
        {
            userAuthenticationValidator.validate(user);
        }

    }

    private class DefaultPostAuthenticationChecks implements UserDetailsChecker
    {
        @Override
        public void check(final UserDetails user)
        {
            if (!user.isCredentialsNonExpired())
            {
                throw new CredentialsExpiredException(messages.getMessage("CoreAuthenticationProvider.credentialsExpired", "User credentials have expired"));
            }
        }
    }

    @Override
    public void setValidators(final List<AuthenticationValidator<User>> validators)
    {
        super.setValidators(validators);
        this.validators = validators;
    }

    @Override
    public void setModelService(final ModelService modelService)
    {
        super.setModelService(modelService);
        this.modelService = modelService;
    }

    public void setUserAuthenticationValidator(final AuthenticationValidator<User> userAuthenticationValidator)
    {
        this.userAuthenticationValidator = userAuthenticationValidator;
    }

    protected AdnocB2BUnitService getAdnocB2BUnitService()
    {
        return adnocB2BUnitService;
    }

    public void setAdnocB2BUnitService(final AdnocB2BUnitService adnocB2BUnitService)
    {
        this.adnocB2BUnitService = adnocB2BUnitService;
    }
}
