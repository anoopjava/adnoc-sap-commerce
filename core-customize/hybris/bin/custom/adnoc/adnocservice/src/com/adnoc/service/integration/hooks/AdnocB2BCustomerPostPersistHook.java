/*
 *  * Copyright (c) SCL. All rights reserved.
 */

package com.adnoc.service.integration.hooks;

import com.adnoc.service.enums.PartnerFunction;
import com.adnoc.service.model.AdnocB2BCustomerCreationProcessModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.enums.RetentionState;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.inboundservices.persistence.PersistenceContext;
import de.hybris.platform.inboundservices.persistence.hook.PostPersistHook;
import de.hybris.platform.processengine.BusinessProcessService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.services.BaseStoreService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

public class AdnocB2BCustomerPostPersistHook implements PostPersistHook
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BCustomerPostPersistHook.class);

    private ModelService modelService;
    private BusinessProcessService businessProcessService;
    private BaseSiteService baseSiteService;
    private CommonI18NService commonI18NService;
    private BaseStoreService baseStoreService;
    private UserService userService;

    @Override
    public void execute(final ItemModel item, final PersistenceContext context)
    {
        if (item instanceof final B2BCustomerModel b2BCustomerModel)
        {
            LOG.info("appEvent=AdnocB2BCustomerInbound, B2BCustomerPostPersistHook Started for Customer: {}", b2BCustomerModel.getUid());

            if (Objects.isNull(b2BCustomerModel.getRetentionState()))
            {
                if (isShipToCustomer(b2BCustomerModel))
                {
                    LOG.info("appEvent=AdnocInboundCustomerEmail,shipTo customer:{} doesn't have a retention state:{}", b2BCustomerModel.getUid(), b2BCustomerModel.getRetentionState());
                    b2BCustomerModel.setActive(false);
                    b2BCustomerModel.setLoginDisabled(true);
                    getModelService().save(b2BCustomerModel);

                    triggerCustomerCreationProcess(b2BCustomerModel, "adnocShipToCreatedB2BCustomerProcess");
                }
                else
                {
                    LOG.info("appEvent=AdnocInboundCustomerEmail,customer:{} doesn't have a retention state:{}", b2BCustomerModel.getUid(), b2BCustomerModel.getRetentionState());
                    b2BCustomerModel.setActive(true);
                    b2BCustomerModel.setLoginDisabled(false);
                    getModelService().save(b2BCustomerModel);
                }

                b2BCustomerModel.setRetentionState(RetentionState.PROCESSED);
                getModelService().save(b2BCustomerModel);
            }
            else
            {
                LOG.info("appEvent=AdnocInboundCustomerEmail,customer:{} has a retention state:{}", b2BCustomerModel.getUid(), b2BCustomerModel.getRetentionState());
                if (isShipToCustomer(b2BCustomerModel))
                {
                    b2BCustomerModel.setActive(false);
                    b2BCustomerModel.setLoginDisabled(true);
                }
                else
                {
                    b2BCustomerModel.setActive(true);
                    b2BCustomerModel.setLoginDisabled(false);
                }

                getModelService().save(b2BCustomerModel);
            }
        }
    }

    private boolean isShipToCustomer(final B2BCustomerModel b2BCustomerModel)
    {
        return b2BCustomerModel.getGroups().stream()
                .filter(B2BUnitModel.class::isInstance)
                .map(B2BUnitModel.class::cast)
                .anyMatch(b2BUnitModel -> Objects.equals(PartnerFunction.SH, b2BUnitModel.getPartnerFunction()));
    }

    private void triggerCustomerCreationProcess(final B2BCustomerModel b2BCustomerModel, final String processCode)
    {
        triggerCustomerCreationProcess(b2BCustomerModel, processCode, null);
    }

    private void triggerCustomerCreationProcess(final B2BCustomerModel b2BCustomerModel, final String processCode, final String generatedPassword)
    {
        final String processName = "AdnocB2BCustomerCreation-" + b2BCustomerModel.getUid() + "-" + System.currentTimeMillis();
        final AdnocB2BCustomerCreationProcessModel processModel = (AdnocB2BCustomerCreationProcessModel) getBusinessProcessService()
                .createProcess(processName, processCode);

        processModel.setCustomer(b2BCustomerModel);
        processModel.setSapBusinessPartnerID(b2BCustomerModel.getSapBusinessPartnerID());

        if (StringUtils.isNotBlank(generatedPassword))
        {
            processModel.setPassword(generatedPassword);
        }

        getModelService().save(processModel);
        LOG.info("appEvent=AdnocB2BCustomerInbound, Process Model created: {}", processModel.getCode());

        getBusinessProcessService().startProcess(processModel);
        LOG.debug("appEvent=AdnocB2BCustomerInbound, Process executed for customer: {}", b2BCustomerModel.getUid());
    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    public void setModelService(final ModelService modelService)
    {
        this.modelService = modelService;
    }

    protected BusinessProcessService getBusinessProcessService()
    {
        return businessProcessService;
    }

    public void setBusinessProcessService(final BusinessProcessService businessProcessService)
    {
        this.businessProcessService = businessProcessService;
    }

    protected CommonI18NService getCommonI18NService()
    {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService)
    {
        this.commonI18NService = commonI18NService;
    }

    protected BaseStoreService getBaseStoreService()
    {
        return baseStoreService;
    }

    public void setBaseStoreService(final BaseStoreService baseStoreService)
    {
        this.baseStoreService = baseStoreService;
    }

    protected BaseSiteService getBaseSiteService()
    {
        return baseSiteService;
    }

    public void setBaseSiteService(final BaseSiteService baseSiteService)
    {
        this.baseSiteService = baseSiteService;
    }

    protected UserService getUserService()
    {
        return userService;
    }

    public void setUserService(final UserService userService)
    {
        this.userService = userService;
    }
}
