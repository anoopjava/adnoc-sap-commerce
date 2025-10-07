package com.adnoc.service.actions.quote;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import com.adnoc.service.model.AdnocB2BCustomerCreationProcessModel;
import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.email.actions.GenerateEmailAction;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.task.RetryLaterException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AbstractAdnocB2BAdminEmailAction extends GenerateEmailAction
{
    private static final Logger LOG = LogManager.getLogger(AbstractAdnocB2BAdminEmailAction.class);

    private AdnocB2BUnitService adnocB2BUnitService;
    private EmailService emailService;

    @Override
    public Transition executeAction(final BusinessProcessModel businessProcessModel) throws RetryLaterException
    {
        final Transition transition = super.executeAction(businessProcessModel);
        if (transition == Transition.OK && businessProcessModel instanceof final AdnocB2BCustomerCreationProcessModel adnocB2BCustomerCreationProcessModel)
        {
            final B2BCustomerModel b2BCustomerModel = (B2BCustomerModel) adnocB2BCustomerCreationProcessModel.getCustomer();
            final B2BUnitModel soldToB2BUnitModel = getAdnocB2BUnitService().getSoldToB2BUnit(b2BCustomerModel);
            final Set<B2BCustomerModel> soldToB2BCustomerModels = getAdnocB2BUnitService().getB2BCustomers(soldToB2BUnitModel);
            final Set<B2BCustomerModel> soldToB2BAdminCustomers = soldToB2BCustomerModels.stream().filter(soldToB2BCustomerModel -> soldToB2BCustomerModel.getGroups().stream()
                    .anyMatch(principalGroupModel -> StringUtils.equals(principalGroupModel.getUid(), B2BConstants.B2BADMINGROUP))).collect(Collectors.toSet());

            final List<String> emailIds = soldToB2BAdminCustomers.stream()
                    .map(B2BCustomerModel::getEmail).filter(StringUtils::isNotEmpty).toList();

            final List<EmailAddressModel> emailAddressModels = createEmailsAddressForEmail(emailIds);

            final EmailMessageModel emailMessage = businessProcessModel.getEmails().iterator().next();
            emailMessage.setToAddresses(emailAddressModels);

            LOG.info("appEvent=CustomerEmail, setting recipients into toAddresses: {}", emailMessage.getToAddresses());
            modelService.save(emailMessage);
        }
        return transition;
    }

    private List<EmailAddressModel> createEmailsAddressForEmail(final List<String> emails)
    {
        final List<EmailAddressModel> emailAddressModelList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(emails))
        {
            for (final String email : emails)
            {
                LOG.info("appEvent=email, processing email address: {}", email);
                final EmailAddressModel emailAddressModel = getEmailService().getOrCreateEmailAddressForEmail(email, email.split("@")[0]);
                emailAddressModelList.add(emailAddressModel);
            }
        }
        return emailAddressModelList;

    }

    protected AdnocB2BUnitService getAdnocB2BUnitService()
    {
        return adnocB2BUnitService;
    }

    public void setAdnocB2BUnitService(final AdnocB2BUnitService adnocB2BUnitService)
    {
        this.adnocB2BUnitService = adnocB2BUnitService;
    }

    protected EmailService getEmailService()
    {
        return emailService;
    }

    public void setEmailService(final EmailService emailService)
    {
        this.emailService = emailService;
    }
}
