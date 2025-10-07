package com.adnoc.service.ticket;

import com.adnoc.service.ticket.dao.AdnocTicketDao;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.mediaweb.assertions.assertj.Assertions;
import de.hybris.platform.ticket.events.model.CsTicketEventModel;
import de.hybris.platform.ticket.model.CsTicketModel;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
class AdnocTicketServiceImplTest
{
    @InjectMocks
    private AdnocTicketServiceImpl adnocTicketService=new AdnocTicketServiceImpl();

    private AdnocTicketDao adnocTicketDao= Mockito.mock(AdnocTicketDao.class);

    private CsTicketModel ticket=Mockito.mock(CsTicketModel.class);


    @Test
    void testGetUpdateEventsForTicket()
    {
        List<CsTicketEventModel> csTicketEventModels=new ArrayList<>();
        csTicketEventModels.add(new CsTicketEventModel());
        adnocTicketService.setAdnocTicketDao(adnocTicketDao);
        Mockito.when(adnocTicketDao.findTicketEventByTicket(ticket)).thenReturn(csTicketEventModels);
        List<CsTicketEventModel> resultList=adnocTicketService.getUpdateEventsForTicket(ticket);
        Assertions.assertThat(resultList).isNotNull();
    }
}