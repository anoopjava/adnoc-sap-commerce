package com.adnoc.facades.process.email.context;

import com.adnoc.service.model.AdnocCsTicketProcessModel;
import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.urlencoder.UrlEncoderService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class AdnocCsTicketClosedEmailContextTest
{
    @InjectMocks
    private AdnocCsTicketClosedEmailContext adnocCsTicketClosedEmailContext;

    @Mock
    private AdnocCsTicketProcessModel ticketProcessModel;
    @Mock
    private CsTicketModel csTicketModel;
    @Mock
    private BaseSiteModel baseSiteModel;
    @Mock
    private LanguageModel languageModel;
    @Mock
    private EmailPageModel emailPageModel;
    @Mock
    private UrlEncoderService urlEncoderService;


    @Test
    public void testInit()
    {
        Mockito.when(ticketProcessModel.getAdnocCsTicket()).thenReturn(csTicketModel);

        Mockito.when(csTicketModel.getBaseSite()).thenReturn(baseSiteModel);
        Mockito.when(baseSiteModel.getDefaultLanguage()).thenReturn(languageModel);
        Mockito.when(languageModel.getIsocode()).thenReturn("en");
        Mockito.when(urlEncoderService.getUrlEncodingPatternForEmail(any(AdnocCsTicketProcessModel.class)))
                .thenReturn("mockedPattern");

        adnocCsTicketClosedEmailContext.init(ticketProcessModel, emailPageModel);

        assertNotNull("Ticket should be initialized", ticketProcessModel.getAdnocCsTicket());

    }
}
