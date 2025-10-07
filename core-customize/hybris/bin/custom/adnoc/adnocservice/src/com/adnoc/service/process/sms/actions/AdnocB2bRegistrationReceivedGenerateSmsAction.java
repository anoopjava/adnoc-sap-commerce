package com.adnoc.service.process.sms.actions;

import com.adnoc.service.model.AdnocSoldToB2BRegistrationModel;
import de.hybris.platform.b2b.model.B2BRegistrationProcessModel;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdnocB2bRegistrationReceivedGenerateSmsAction extends AbstractAdnocB2bGenerateSmsAction
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2bRegistrationReceivedGenerateSmsAction.class);

    private static final String ADNOC_B2B_REGISTRATION_RECEIVED_SMS_BODY = "adnoc.b2b.registration.received.sms.body";

    @Override
    public Transition executeAction(final BusinessProcessModel businessProcessModel)
    {
        LOG.debug("appEvent=AdnocB2bRegistrationReceivedSmsAction, executing SMS action for process: {}", businessProcessModel.getCode());
        executeSmsAction(businessProcessModel, ADNOC_B2B_REGISTRATION_RECEIVED_SMS_BODY);
        LOG.info("appEvent=AdnocB2bRegistrationReceivedSmsAction,SMS action executed successfully for process: {}", businessProcessModel.getCode());
        return Transition.OK;
    }

    protected void executeSmsAction(final BusinessProcessModel businessProcessModel, final String adnocB2bRegistrationSmsBody)
    {
        if (businessProcessModel instanceof B2BRegistrationProcessModel)
        {
            final B2BRegistrationProcessModel b2BRegistrationProcessModel = (B2BRegistrationProcessModel) businessProcessModel;
            if (b2BRegistrationProcessModel.getRegistration() instanceof AdnocSoldToB2BRegistrationModel)
            {
                final AdnocSoldToB2BRegistrationModel abnocB2BRegistrationModel = (AdnocSoldToB2BRegistrationModel) b2BRegistrationProcessModel.getRegistration();
                prepareAndSendSmsAction(adnocB2bRegistrationSmsBody, abnocB2BRegistrationModel.getMobileNumber(), abnocB2BRegistrationModel.getFirstName());
                LOG.debug("appEvent=AdnocB2bRegistrationReceivedSmsAction, SMS prepared and sent successfully to mobile number: {}", abnocB2BRegistrationModel.getMobileNumber());
            }
        }
    }
}
