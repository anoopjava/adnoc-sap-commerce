package com.adnoc.service.sms;

import com.adnoc.service.rest.integration.SmsRequestData;
import com.adnoc.service.rest.integration.SmsResponseData;

/**
 * Service interface for handling SMS operations in the ADNOC system.
 * This interface defines methods for sending SMS messages.
 */
public interface AdnocSmsService
{
    /**
     * Send sms sms response data.
     *
     * @param smsRequestData the sms request data
     * @return the sms response data
     */
    SmsResponseData sendSms(SmsRequestData smsRequestData);
}
