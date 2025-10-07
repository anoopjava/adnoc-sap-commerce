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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocQuoteRequestResubmissionEmailActionTest
{
    @InjectMocks
    private AdnocQuoteRequestResubmissionEmailAction adnocNewQuoteRequestEmailAction = new AdnocQuoteRequestResubmissionEmailAction();
    @Mock
    private ProcessContextResolutionStrategy contextResolutionStrategy;
    @Mock
    private CMSEmailPageService cmsEmailPageService;
    @Mock
    private CatalogVersionModel contentCatalogVersion;
    @Mock
    private EmailPageModel emailPageModel;
    @Mock
    private EmailGenerationService emailGenerationService;
    @Mock
    private EmailService emailService;
    @Mock
    private ModelService modelService;
    @Mock
    private ConfigurationService configurationService;
    @Mock
    private Configuration configuration;

    private final BusinessProcessModel businessProcessModel = new BusinessProcessModel();
    private EmailMessageModel emailMessageModel;
    private EmailAddressModel emailAddressModel;
    public static final String ADMIN_TOADDRESS = "adnoc.csa.admin.toAddress";

    @Test
    public void testExecuteActionOk() throws RetryLaterException
    {
        Mockito.when(contextResolutionStrategy.getContentCatalogVersion(businessProcessModel)).thenReturn(contentCatalogVersion);
        Mockito.when(cmsEmailPageService.getEmailPageForFrontendTemplate(nullable(String.class), nullable(contentCatalogVersion.getClass())))
                .thenReturn(emailPageModel);
        emailMessageModel = Mockito.mock(EmailMessageModel.class);
        Mockito.when(emailGenerationService.generate(businessProcessModel, emailPageModel)).thenReturn(emailMessageModel);

        final String email1 = "admin1@example.com";
        final String email2 = "admin2@example.com";
        final String configuredEmails = email1 + "," + email2;

        Mockito.when(configurationService.getConfiguration()).thenReturn(configuration);
        Mockito.when(configuration.getString(ADMIN_TOADDRESS)).thenReturn(configuredEmails);
        Mockito.when(emailGenerationService.generate(businessProcessModel, emailPageModel)).thenReturn(emailMessageModel);
        Mockito.when(emailService.getOrCreateEmailAddressForEmail(eq(email1), eq("admin1"))).thenReturn(emailAddressModel);
        Mockito.when(emailService.getOrCreateEmailAddressForEmail(eq(email2), eq("admin2"))).thenReturn(emailAddressModel);

        final List<EmailMessageModel> emailMessageModels = new ArrayList<>();
        emailMessageModels.add(emailMessageModel);
        businessProcessModel.setEmails(emailMessageModels);
        final GenerateEmailAction.Transition result = adnocNewQuoteRequestEmailAction.executeAction(businessProcessModel);
        Assert.assertEquals(GenerateEmailAction.Transition.OK, result);
        verify(emailService, times(2)).getOrCreateEmailAddressForEmail(anyString(), anyString());
        verify(modelService).save(emailMessageModel);
    }

    @Test
    public void testExecuteActionNotOk() throws RetryLaterException
    {
        final List<EmailMessageModel> emails = new ArrayList<>();
        final EmailMessageModel emailMessage = Mockito.mock(EmailMessageModel.class);
        emails.add(emailMessage);
        businessProcessModel.setEmails(emails);
        final GenerateEmailAction.Transition result = adnocNewQuoteRequestEmailAction.executeAction(businessProcessModel);
        Assert.assertEquals(GenerateEmailAction.Transition.NOK, result);
        verify(emailService, never()).getOrCreateEmailAddressForEmail(anyString(), anyString());
        verify(modelService, never()).save(any(EmailMessageModel.class));
    }
}
