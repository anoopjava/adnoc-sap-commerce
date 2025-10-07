package com.adnoc.service.actions.quote;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.email.CMSEmailPageService;
import de.hybris.platform.acceleratorservices.email.EmailGenerationService;
import de.hybris.platform.acceleratorservices.email.EmailService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.email.actions.GenerateEmailAction;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.task.RetryLaterException;
import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocNewQuoteRequestEmailActionTest
{
    @InjectMocks
    private AdnocNewQuoteRequestEmailAction adnocNewQuoteRequestEmailAction = new AdnocNewQuoteRequestEmailAction();
    @Mock
    private EmailService emailService;
    @Mock
    private CatalogVersionModel contentCatalogVersion;
    @Mock
    private ProcessContextResolutionStrategy contextResolutionStrategy;
    @Mock
    private CMSEmailPageService cmsEmailPageService;
    @Mock
    private EmailGenerationService emailGenerationService;
    @Mock
    private ModelService modelService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;
    @Mock
    private EmailPageModel emailPageModel;

    public static final String CSA_ADMIN_TO_ADDRESS = "adnoc.csa.admin.toAddress";
    private final BusinessProcessModel businessProcessModel = new BusinessProcessModel();
    private EmailMessageModel emailMessageModel;
    private EmailAddressModel emailAddressModel;

    @Test
    public void testExecuteActionOk() throws RetryLaterException
    {
        Mockito.when(contextResolutionStrategy.getContentCatalogVersion(businessProcessModel)).thenReturn(contentCatalogVersion);
        Mockito.when(cmsEmailPageService.getEmailPageForFrontendTemplate(nullable(String.class), nullable(contentCatalogVersion.getClass())))
                .thenReturn(emailPageModel);
        emailMessageModel = Mockito.mock(EmailMessageModel.class);
        Mockito.when(emailGenerationService.generate(businessProcessModel, emailPageModel)).thenReturn(emailMessageModel);

        final String email1 = "csadmin1@gmail.com";
        final String email2 = "csadmin2@gmail.com";
        final String configuredEmails = email1 + "," + email2;

        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getString(CSA_ADMIN_TO_ADDRESS)).thenReturn(configuredEmails);
        Mockito.when(emailGenerationService.generate(businessProcessModel, emailPageModel)).thenReturn(emailMessageModel);
        Mockito.when(emailService.getOrCreateEmailAddressForEmail(eq(email1), eq("csadmin1"))).thenReturn(emailAddressModel);
        Mockito.when(emailService.getOrCreateEmailAddressForEmail(eq(email2), eq("csadmin2"))).thenReturn(emailAddressModel);

        final List<EmailMessageModel> emailsModels = new ArrayList<>();
        emailsModels.add(emailMessageModel);
        businessProcessModel.setEmails(emailsModels);

        final GenerateEmailAction.Transition result = adnocNewQuoteRequestEmailAction.executeAction(businessProcessModel);
        Assert.assertEquals(GenerateEmailAction.Transition.OK, result);
        verify(emailService, times(2)).getOrCreateEmailAddressForEmail(anyString(), anyString());
        verify(modelService).save(emailMessageModel);
    }

    @Test
    public void testExecuteActionNOK() throws RetryLaterException
    {
        final var result = adnocNewQuoteRequestEmailAction.executeAction(businessProcessModel);
        assertEquals(GenerateEmailAction.Transition.NOK, result);
        verify(emailService, never()).getOrCreateEmailAddressForEmail(anyString(), anyString());
        verify(modelService, never()).save(any(EmailMessageModel.class));
    }
}
