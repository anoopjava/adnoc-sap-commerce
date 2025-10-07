package com.adnoc.service.actions.quote;

import com.adnoc.service.model.AdnocB2BCustomerCreationProcessModel;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.acceleratorservices.email.CMSEmailPageService;
import de.hybris.platform.acceleratorservices.email.EmailGenerationService;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.model.email.EmailAddressModel;
import de.hybris.platform.acceleratorservices.model.email.EmailMessageModel;
import de.hybris.platform.acceleratorservices.process.email.actions.GenerateEmailAction;
import de.hybris.platform.acceleratorservices.process.strategies.ProcessContextResolutionStrategy;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.task.RetryLaterException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.nullable;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class AdnocShipToCreatedB2BCustomerEmailActionTest
{
    @InjectMocks
    private AdnocShipToCreatedB2BCustomerEmailAction adnocShipToCreatedB2BCustomerEmailAction;
    @Mock
    private AdnocB2BCustomerCreationProcessModel adnocB2BCustomerCreationProcessModel;
    @Mock
    private B2BCustomerModel b2BCustomerModel;
    @Mock
    private B2BUnitModel b2BUnitModel;
    @Mock
    private EmailAddressModel emailAddressModel;
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
    private EmailMessageModel emailMessageModel;

    @Test
    public void testExecuteActionOk() throws RetryLaterException
    {
        Mockito.when(contextResolutionStrategy.getContentCatalogVersion(adnocB2BCustomerCreationProcessModel)).thenReturn(contentCatalogVersion);
        Mockito.when(cmsEmailPageService.getEmailPageForFrontendTemplate(nullable(String.class), nullable(contentCatalogVersion.getClass())))
                .thenReturn(emailPageModel);
        emailMessageModel = Mockito.mock(EmailMessageModel.class);
        Mockito.when(emailGenerationService.generate(adnocB2BCustomerCreationProcessModel, emailPageModel)).thenReturn(emailMessageModel);

        Mockito.when(adnocB2BCustomerCreationProcessModel.getCustomer()).thenReturn(b2BCustomerModel);
        Mockito.when(b2BCustomerModel.getAllGroups()).thenReturn(Set.of(b2BUnitModel));
        Mockito.when(b2BUnitModel.getPartnerFunction()).thenReturn(com.adnoc.service.enums.PartnerFunction.SP);

        Mockito.when(adnocB2BCustomerCreationProcessModel.getEmails()).thenReturn(Collections.singletonList(emailMessageModel));
        final List<EmailAddressModel> expectedEmailAddresses = Collections.singletonList(emailAddressModel);
        Mockito.when(emailMessageModel.getToAddresses()).thenReturn(expectedEmailAddresses);

        final GenerateEmailAction.Transition result = adnocShipToCreatedB2BCustomerEmailAction.executeAction(adnocB2BCustomerCreationProcessModel);
        Assert.assertEquals(GenerateEmailAction.Transition.OK, result);
    }

    @Test
    public void testExecuteActionNotOk() throws RetryLaterException
    {
        final AdnocB2BCustomerCreationProcessModel businessProcessModel = Mockito.mock(AdnocB2BCustomerCreationProcessModel.class);
        final AbstractAdnocB2BAdminEmailAction.Transition result = adnocShipToCreatedB2BCustomerEmailAction.executeAction(businessProcessModel);
        de.hybris.platform.testframework.Assert.assertEquals(AbstractAdnocB2BAdminEmailAction.Transition.NOK, result);
    }
}
