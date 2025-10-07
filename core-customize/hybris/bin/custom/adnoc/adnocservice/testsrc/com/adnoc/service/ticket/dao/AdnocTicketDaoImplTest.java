package com.adnoc.service.ticket.dao;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocTicketDaoImplTest
{

    @InjectMocks
    private AdnocTicketDaoImpl adnocTicketDao=new AdnocTicketDaoImpl();

    @Mock
    private FlexibleSearchService flexibleSearchService= Mockito.mock(FlexibleSearchService.class);
    @Mock
    private CsTicketEventModel csTicketEventModel=Mockito.mock(CsTicketEventModel.class);
    @Mock
    private CsTicketModel ticket=Mockito.mock(CsTicketModel.class);

    @Test
    void testFindTicketEventByTicket()
    {

        adnocTicketDao.setFlexibleSearchService(flexibleSearchService);
        SearchResult searchResult= Mockito.mock(SearchResult.class);
        List<CsTicketEventModel> csTicketEventModels= new ArrayList<>();
        csTicketEventModels.add(csTicketEventModel);
        Mockito.when(searchResult.getResult()).thenReturn(csTicketEventModels);
        Mockito.when(flexibleSearchService.search(Mockito.any(FlexibleSearchQuery.class))).thenReturn(searchResult);

        List<CsTicketEventModel> csTicketEventModels1=adnocTicketDao.findTicketEventByTicket(ticket);
        Assertions.assertThat(csTicketEventModels1).isNotNull();

    }
}