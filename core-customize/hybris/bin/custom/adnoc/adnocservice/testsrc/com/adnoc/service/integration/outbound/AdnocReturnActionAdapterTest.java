package com.adnoc.service.integration.outbound;

import com.adnoc.service.director.AdnocOutboundReplicationDirector;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.returns.model.ReturnProcessModel;
import de.hybris.platform.returns.model.ReturnRequestModel;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AdnocReturnActionAdapterTest {

    @Test
    void testRequestReturnApproval() {
        // Arrange
        BusinessProcessService businessProcessService = mock(BusinessProcessService.class);
        AdnocOutboundReplicationDirector replicationDirector = mock(AdnocOutboundReplicationDirector.class);
        ModelService modelService = mock(ModelService.class);

        AdnocReturnActionAdapter adapter = new AdnocReturnActionAdapter();
        adapter.setBusinessProcessService(businessProcessService);
        adapter.setAdnocOutboundReplicationDirector(replicationDirector);
        adapter.setModelService(modelService);

        ReturnRequestModel returnRequest = mock(ReturnRequestModel.class);
        ReturnProcessModel processModel = mock(ReturnProcessModel.class);

        when(businessProcessService.createProcess(anyString(), eq("adnocReturnOrderOutbound-process")))
                .thenReturn(processModel);

        // Just to avoid NPEs if getCode() is logged
        when(processModel.getCode()).thenReturn("RET_PROC_001");

        // Act
        adapter.requestReturnApproval(returnRequest);

        // Assert
        verify(replicationDirector, times(1)).scheduleOutboundTask(returnRequest);
        verify(businessProcessService, times(1)).createProcess(startsWith("adnocReturnOrderOutbound-process-"), eq("adnocReturnOrderOutbound-process"));
        verify(modelService, times(1)).save(processModel);
        verify(processModel, times(1)).setReturnRequest(returnRequest);
        verify(businessProcessService, times(1)).startProcess(processModel);
    }
}
