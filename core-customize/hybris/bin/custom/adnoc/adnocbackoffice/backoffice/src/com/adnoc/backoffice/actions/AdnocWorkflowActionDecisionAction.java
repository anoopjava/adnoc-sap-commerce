package com.adnoc.backoffice.actions;

import com.adnoc.service.enums.IdentityType;
import com.adnoc.service.enums.PartnerFunction;
import com.adnoc.service.model.*;
import com.hybris.backoffice.workflow.WorkflowActionDecisionAction;
import com.hybris.cockpitng.engine.WidgetInstanceManager;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.workflow.WorkflowAttachmentService;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Resource;
import java.util.*;

public class AdnocWorkflowActionDecisionAction extends WorkflowActionDecisionAction
{
    private static final Logger LOG = LogManager.getLogger(AdnocWorkflowActionDecisionAction.class);
    private static final String JUST_MESSAE = "JustMessage";
    private static final String ADNOC_B2B_UNIT_REGISTRATION = "AdnocB2BUnitRegistration";
    private static final int IBAN_NUMBER_MAX_LENGTH = 35;
    private static final int ACCOUNT_NUMBER_MAX_LENGTH = 10;

    private static final Map<String, ValidationRule> IDENTITY_VALIDATION_RULES = Map.of(
            "FS0001", new ValidationRule("^\\d{3}-\\d{4}-\\d{7}-\\d{1}$",
                    "Invalid Identification Number format for Emirates ID. Expected format: 123-1234-1234567-1"),
            "FS0002", new ValidationRule("^[a-zA-Z0-9]{1,20}$",
                    "Invalid Identification Number format for Passport Number. Must contain only letters and numbers, with a maximum of 20 characters"));

    @Resource(name = "workflowAttachmentService")
    private WorkflowAttachmentService workflowAttachmentService;

    @Resource(name = "notificationService")
    private NotificationService notificationService;

    @Resource(name = "modelService")
    private ModelService modelService;

    @Override
    protected boolean shouldPerform(final WidgetInstanceManager widgetInstanceManager, final WorkflowDecisionModel selectedDecision)
    {
        final boolean shouldPerform = super.shouldPerform(widgetInstanceManager, selectedDecision);
        LOG.debug("appEvent=AdnocB2BRegistrationWorkFlow, Entered shouldPerform with decision: {}, shouldPerform: {}", selectedDecision.getName(), shouldPerform);

        final List<ItemModel> models = workflowAttachmentService.getAttachmentsForAction(selectedDecision.getAction(), AdnocRegistrationModel.class.getName());

        if (CollectionUtils.isEmpty(models))
        {
            LOG.info("appEvent=AdnocB2BRegistrationWorkFlow, No attachments found for the action: " + selectedDecision.getAction().getCode());
            return false;
        }

        final AdnocRegistrationModel adnocRegistrationModel = (AdnocRegistrationModel) models.get(0);
        modelService.refresh(adnocRegistrationModel);

        if (shouldPerform && StringUtils.equalsIgnoreCase("Approve", selectedDecision.getName()))
        {
            LOG.info("appEvent=AdnocRegistrationWorkFlow, Notifying user about workflow for decision:{} ", selectedDecision.getName());
            return handleApproveAction(adnocRegistrationModel, selectedDecision, widgetInstanceManager);
        }
        else if (shouldPerform && StringUtils.equalsIgnoreCase("Reject", selectedDecision.getName()))
        {
            LOG.debug("appEvent=AdnocRegistrationWorkFlow, Notifying user about workflow for decision: {}", adnocRegistrationModel.getName());
            return handleRejectAction(adnocRegistrationModel, selectedDecision, widgetInstanceManager);
        }

        LOG.info("appEvent=AdnocB2BRegistrationWorkFlow, Exiting shouldPerform with shouldPerform: {}", shouldPerform);
        return shouldPerform;
    }

    private boolean handleApproveAction(final AdnocRegistrationModel adnocRegistrationModel, final WorkflowDecisionModel selectedDecision, final WidgetInstanceManager widgetInstanceManager)
    {
        if (!validateCommonMandatoryAttributes(adnocRegistrationModel, widgetInstanceManager))
        {
            return false;
        }
        if (adnocRegistrationModel instanceof final AdnocSoldToB2BRegistrationModel adnocSoldToB2BRegistrationModel)
        {
            if (!validateSoldToMandatoryAttributes(widgetInstanceManager, adnocSoldToB2BRegistrationModel))
            {
                return false;
            }

            if (!validateAllOptionalAccountFieldsForSoldTo(adnocSoldToB2BRegistrationModel, widgetInstanceManager))
            {
                return false;
            }
        }
        if (adnocRegistrationModel instanceof final AdnocB2BUnitRegistrationModel adnocB2BUnitRegistrationModel)
        {
            final PartnerFunction partnerFunction = adnocRegistrationModel.getPartnerFunction();
            final Map<String, Object> validateB2BUnitRegistrationMap = new HashMap<>();
            if (!validateAttributesForMandatoryValue(ADNOC_B2B_UNIT_REGISTRATION, validateB2BUnitRegistrationMap, widgetInstanceManager))
            {
                return false;
            }
            if (adnocB2BUnitRegistrationModel instanceof final AdnocShipToB2BUnitRegistrationModel adnocShipToB2BUnitRegistrationModel)
            {
                if (!validateShipToMandatoryAttributes(widgetInstanceManager, adnocShipToB2BUnitRegistrationModel))
                {
                    return false;
                }
                if (!validateShipToNotApplicableAttributes(widgetInstanceManager, adnocShipToB2BUnitRegistrationModel, partnerFunction))
                {
                    return false;
                }
            }
            if (adnocB2BUnitRegistrationModel instanceof final AdnocPayerB2BUnitRegistrationModel adnocPayerB2BUnitRegistrationModel)
            {
                if (!validatePayerMandatoryAttributes(widgetInstanceManager, adnocPayerB2BUnitRegistrationModel))
                {
                    return false;
                }
                if (!validatePayerNotApplicableAttributes(widgetInstanceManager, adnocPayerB2BUnitRegistrationModel, partnerFunction))
                {
                    return false;
                }
                if (!validateAllOptionalAccountFieldsForPayer(adnocPayerB2BUnitRegistrationModel, widgetInstanceManager))
                {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean validateSoldToMandatoryAttributes(final WidgetInstanceManager widgetInstanceManager, final AdnocSoldToB2BRegistrationModel adnocSoldToB2BRegistrationModel)
    {
        final Map<String, Object> validateSoldToRegistrationMandatoryData = new HashMap<>();
        validateSoldToRegistrationMandatoryData.put("Reconciliation Account", adnocSoldToB2BRegistrationModel.getReconciliationAccount());
        validateSoldToRegistrationMandatoryData.put("Sort Key", adnocSoldToB2BRegistrationModel.getSortKey());
        validateSoldToRegistrationMandatoryData.put("Planning Group", adnocSoldToB2BRegistrationModel.getPlanningGroup());
        validateSoldToRegistrationMandatoryData.put("Terms of payment", adnocSoldToB2BRegistrationModel.getTermsOfPayment());
        validateSoldToRegistrationMandatoryData.put("Payment Methods", adnocSoldToB2BRegistrationModel.getPaymentMethods());
        if (!validateAttributesForMandatoryValue(ADNOC_B2B_UNIT_REGISTRATION, validateSoldToRegistrationMandatoryData, widgetInstanceManager))
        {
            return false;
        }
        if (!validateAlphanumericField(adnocSoldToB2BRegistrationModel.getVatId(), "VAT ID", 15, widgetInstanceManager))
        {
            return false;
        }
        if (!validateTradeLicenseNumber(adnocSoldToB2BRegistrationModel.getTradeLicenseNumber(), "AdnocB2BRegistration", widgetInstanceManager))
        {
            return false;
        }
        return true;
    }

    private boolean validatePayerNotApplicableAttributes(final WidgetInstanceManager widgetInstanceManager, final AdnocB2BUnitRegistrationModel adnocB2BUnitRegistrationModel, final PartnerFunction partnerFunction)
    {
        final Map<String, Object> PayerNotApplicableAttributes = new HashMap<>();
        if (!validateAttributesForNotApplicableValue(ADNOC_B2B_UNIT_REGISTRATION, partnerFunction, PayerNotApplicableAttributes, widgetInstanceManager))
        {
            return false;
        }
        return true;
    }

    private boolean validatePayerMandatoryAttributes(final WidgetInstanceManager widgetInstanceManager, final AdnocPayerB2BUnitRegistrationModel adnocPayerB2BUnitRegistrationModel)
    {
        final Map<String, Object> pyMandatoryAttributesMap = new HashMap<>();
        pyMandatoryAttributesMap.put("InvoicingDate", adnocPayerB2BUnitRegistrationModel.getInvoicingDate());
        pyMandatoryAttributesMap.put("SalesManager", adnocPayerB2BUnitRegistrationModel.getSalesManager());
        pyMandatoryAttributesMap.put("Collector", adnocPayerB2BUnitRegistrationModel.getCollector());
        pyMandatoryAttributesMap.put("Reconciliation Account", adnocPayerB2BUnitRegistrationModel.getReconciliationAccount());
        pyMandatoryAttributesMap.put("Sort Key", adnocPayerB2BUnitRegistrationModel.getSortKey());
        pyMandatoryAttributesMap.put("Planning Group", adnocPayerB2BUnitRegistrationModel.getPlanningGroup());
        pyMandatoryAttributesMap.put("Payment Methods", adnocPayerB2BUnitRegistrationModel.getPaymentMethods());
        pyMandatoryAttributesMap.put("Dunning Clerk", adnocPayerB2BUnitRegistrationModel.getDunningClerk());
        pyMandatoryAttributesMap.put("Accounting clerk", adnocPayerB2BUnitRegistrationModel.getAccountingClerk());
        pyMandatoryAttributesMap.put("Terms of payment", adnocPayerB2BUnitRegistrationModel.getTermsOfPayment());

        if (!validateAttributesForMandatoryValue(ADNOC_B2B_UNIT_REGISTRATION, pyMandatoryAttributesMap, widgetInstanceManager))
        {
            return false;
        }
        if (!validateAlphanumericField(adnocPayerB2BUnitRegistrationModel.getVatId(), "VAT ID", 15, widgetInstanceManager))
        {
            return false;
        }
        return true;
    }

    private boolean validateShipToNotApplicableAttributes(final WidgetInstanceManager widgetInstanceManager, final AdnocShipToB2BUnitRegistrationModel adnocShipToB2BUnitRegistrationModel, final PartnerFunction partnerFunction)
    {
        final Map<String, Object> validateShipToB2BUnitNAData = new HashMap<>();
        if (!validateAttributesForNotApplicableValue(ADNOC_B2B_UNIT_REGISTRATION, partnerFunction, validateShipToB2BUnitNAData, widgetInstanceManager))
        {
            return false;
        }
        return true;
    }

    private boolean validateShipToMandatoryAttributes(final WidgetInstanceManager widgetInstanceManager, final AdnocShipToB2BUnitRegistrationModel adnocShipToB2BUnitRegistrationModel)
    {
        final Map<String, Object> validateShipToB2BUnitRegistrationMandatoryData = new HashMap<>();
        validateShipToB2BUnitRegistrationMandatoryData.put("Latitude", adnocShipToB2BUnitRegistrationModel.getLatitude());
        validateShipToB2BUnitRegistrationMandatoryData.put("Longitude", adnocShipToB2BUnitRegistrationModel.getLongitude());
        validateShipToB2BUnitRegistrationMandatoryData.put("incoTerms", adnocShipToB2BUnitRegistrationModel.getIncoTerms());
        validateShipToB2BUnitRegistrationMandatoryData.put("Plant", adnocShipToB2BUnitRegistrationModel.getPlant());
        if (!validateAttributesForMandatoryValue(ADNOC_B2B_UNIT_REGISTRATION, validateShipToB2BUnitRegistrationMandatoryData, widgetInstanceManager))
        {
            return false;
        }
        return true;
    }

    private boolean validateCommonMandatoryAttributes(final AdnocRegistrationModel adnocRegistrationModel, final WidgetInstanceManager widgetInstanceManager)
    {
        final Map<String, Object> validateRegistrationMap = new HashMap<>();
        validateRegistrationMap.put("Currency", adnocRegistrationModel.getCurrency());
        validateRegistrationMap.put("TaxClassification", adnocRegistrationModel.getTaxClassification());
        validateRegistrationMap.put("PaymentTerms", adnocRegistrationModel.getPaymentTerms());
        validateRegistrationMap.put("CustomerGroup", adnocRegistrationModel.getCustomerGroup());
        validateRegistrationMap.put("SalesOffice", adnocRegistrationModel.getSalesOffice());
        validateRegistrationMap.put("SalesGroup", adnocRegistrationModel.getSalesGroup());
        validateRegistrationMap.put("Shipping Condition", adnocRegistrationModel.getShippingCondition());
        if (!validateAttributesForMandatoryValue("AdnocB2BRegistration", validateRegistrationMap, widgetInstanceManager))
        {
            return false;
        }
        if (!validateIdentificationNumberFormat((adnocRegistrationModel.getIdentityType()), adnocRegistrationModel.getIdentificationNumber(), "AdnocB2BRegistration", widgetInstanceManager))
        {
            return false;
        }
        return true;
    }

    private boolean validateAttributesForMandatoryValue(final String appEvent, final Map<String, Object> attributeMap, final WidgetInstanceManager widgetManager)
    {
        if (MapUtils.isEmpty(attributeMap))
        {
            return true;
        }
        for (final Map.Entry<String, Object> entry : attributeMap.entrySet())
        {
            final Object entryValue = entry.getValue();
            if (Objects.isNull(entryValue) || (entryValue instanceof final Collection collection && CollectionUtils.isEmpty(collection)))
            {
                final String message = entry.getKey() + " is Mandatory";
                LOG.info("appEvent={}, Notifying user about missing {}", appEvent, entry.getKey());
                notificationService.notifyUser(widgetManager, JUST_MESSAE, NotificationEvent.Level.FAILURE, message);
                return false;
            }
        }
        return true;
    }

    private boolean validateAttributesForNotApplicableValue(final String appEvent, final PartnerFunction partnerFunction,
                                                            final Map<String, Object> attributeMap, final WidgetInstanceManager widgetManager)
    {
        if (MapUtils.isEmpty(attributeMap))
        {
            return true;
        }
        for (final Map.Entry<String, Object> entry : attributeMap.entrySet())
        {
            if (Objects.nonNull(entry.getValue()))
            {
                final String message = String.format("%s is not applicable for PartnerFunction=%s, Please Remove it to Proceed",
                        entry.getKey(), partnerFunction);
                LOG.info("appEvent={}, Notifying user about: {}", appEvent, message);
                notificationService.notifyUser(widgetManager, JUST_MESSAE, NotificationEvent.Level.FAILURE, message);
                return false;
            }
        }
        return true;
    }

    private boolean handleRejectAction(final AdnocRegistrationModel adnocRegistrationModel, final WorkflowDecisionModel selectedDecision, final WidgetInstanceManager widgetInstanceManager)
    {
        if (StringUtils.isBlank(adnocRegistrationModel.getRejectReason()))
        {
            LOG.warn("appEvent=B2BRegistrationWorkflow,No Reject Reason Found. disabling reject option for:{}", adnocRegistrationModel.getEmail());
            notificationService.notifyUser(widgetInstanceManager, JUST_MESSAE, NotificationEvent.Level.FAILURE, "Reject reason is mandatory");
            return false;
        }
        LOG.info("appEvent=B2BRegistrationWorkflow, Reject Reason Available for: {}", adnocRegistrationModel.getEmail());
        return true;
    }

    private boolean validateOptionalNumericField(final String value, final String fieldLabel, final int maxLength,
                                                 final WidgetInstanceManager widgetManager)
    {
        if (StringUtils.isNotBlank(value))
        {
            if (!value.matches("\\d+"))
            {
                notificationService.notifyUser(widgetManager, JUST_MESSAE, NotificationEvent.Level.FAILURE,
                        fieldLabel + " must contain only digits.");
                return false;
            }
            if (value.length() > maxLength)
            {
                notificationService.notifyUser(widgetManager, JUST_MESSAE, NotificationEvent.Level.FAILURE,
                        fieldLabel + " must not exceed " + maxLength + " digits.");
                return false;
            }
        }
        return true;
    }

    private boolean validateOptionalAlphanumericField(final String value, final String fieldLabel, final int maxLength,
                                                      final WidgetInstanceManager widgetManager)
    {
        if (StringUtils.isNotBlank(value))
        {
            final String pattern = "^[a-zA-Z0-9 ]{1," + maxLength + "}$";
            if (!value.matches(pattern))
            {
                notificationService.notifyUser(widgetManager, "JustMessage", NotificationEvent.Level.FAILURE,
                        fieldLabel + " must be alphanumeric (letters, numbers, spaces only) and up to " + maxLength + " characters.");
                return false;
            }
        }

        return true;
    }

    private boolean validateAlphanumericField(final String value, final String fieldLabel,
                                              final int maxLength, final WidgetInstanceManager widgetManager)
    {
        final String pattern = "^[a-zA-Z0-9 ]{1," + maxLength + "}$";
        if (StringUtils.isBlank(value))
        {
            notificationService.notifyUser(widgetManager, JUST_MESSAE, NotificationEvent.Level.FAILURE,
                    fieldLabel + " is required.");
            return false;
        }
        if (!value.matches(pattern))
        {
            notificationService.notifyUser(widgetManager, JUST_MESSAE, NotificationEvent.Level.FAILURE,
                    fieldLabel + " must be alphanumeric (letters, numbers, spaces only) and up to " + maxLength + " characters.");
            return false;
        }
        return true;
    }

    private boolean validateAllOptionalAccountFieldsForSoldTo(final AdnocSoldToB2BRegistrationModel model,
                                                              final WidgetInstanceManager widgetManager)
    {
        return validateOptionalAlphanumericField(model.getVirtualIBANNumber(), "Virtual IBAN Number", IBAN_NUMBER_MAX_LENGTH, widgetManager)
                && validateOptionalNumericField(model.getPreviousAccountNumber(), "Previous Account Number", ACCOUNT_NUMBER_MAX_LENGTH, widgetManager)
                && validateOptionalAlphanumericField(model.getCollectionIBANNumber(), "Collection IBAN Number", IBAN_NUMBER_MAX_LENGTH, widgetManager);
    }

    private boolean validateAllOptionalAccountFieldsForPayer(final AdnocPayerB2BUnitRegistrationModel model,
                                                             final WidgetInstanceManager widgetManager)
    {
        return validateOptionalAlphanumericField(model.getVirtualIBANNumber(), "Virtual IBAN Number", IBAN_NUMBER_MAX_LENGTH, widgetManager)
                && validateOptionalNumericField(model.getPreviousAccountNumber(), "Previous Account Number", ACCOUNT_NUMBER_MAX_LENGTH, widgetManager)
                && validateOptionalAlphanumericField(model.getCollectionIBANNumber(), "Collection IBAN Number", IBAN_NUMBER_MAX_LENGTH, widgetManager);
    }

    private boolean validateIdentificationNumberFormat(final IdentityType identityType, final String identificationNumber,
                                                       final String appEvent, final WidgetInstanceManager widgetManager)
    {
        if (StringUtils.isBlank(identificationNumber))
        {
            LOG.info("appEvent={}, Missing identificationNumber", appEvent);
            notificationService.notifyUser(widgetManager, JUST_MESSAE, NotificationEvent.Level.FAILURE,
                    "identificationNumber is required.");
            return false;
        }

        final String identityCode = identityType.getCode();
        final ValidationRule rule = IDENTITY_VALIDATION_RULES.get(identityCode);

        if (Objects.nonNull(rule))
        {
            if (!identificationNumber.matches(rule.getRegex()))
            {
                LOG.info("appEvent={}, Invalid format for identityType={}", appEvent, identityCode);
                notificationService.notifyUser(widgetManager, JUST_MESSAE, NotificationEvent.Level.FAILURE, rule.getMessage());
                return false;
            }
            LOG.info("appEvent={}, Validation passed for identityType={}", identityCode);
        }
        return true;
    }

    private boolean validateTradeLicenseNumber(final String tradeLicenseNumber, final String appEvent, final WidgetInstanceManager widgetManager)
    {
        if (StringUtils.isBlank(tradeLicenseNumber))
        {
            LOG.info("appEvent={}, Missing Trade License Number", appEvent);
            notificationService.notifyUser(widgetManager, JUST_MESSAE, NotificationEvent.Level.FAILURE,
                    "Trade License Number is required for validation");
            return false;
        }

        final String tradeLicenseRegex = "^[a-zA-Z]{2}-\\d{7}$";
        if (!tradeLicenseNumber.matches(tradeLicenseRegex))
        {
            final String message = "Invalid Trade License Number format. Expected format: AA-1234567";
            LOG.info("appEvent={}, Invalid Trade License Number format", appEvent);
            notificationService.notifyUser(widgetManager, JUST_MESSAE, NotificationEvent.Level.FAILURE, message);
            return false;
        }
        LOG.info("appEvent={}, Validation passed for Trade License Number: {}", tradeLicenseNumber);
        return true;
    }

}
