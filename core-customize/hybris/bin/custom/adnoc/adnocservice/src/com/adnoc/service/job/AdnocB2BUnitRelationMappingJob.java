package com.adnoc.service.job;

import com.adnoc.service.b2bunit.AdnocB2BUnitService;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.cronjob.enums.CronJobResult;
import de.hybris.platform.cronjob.enums.CronJobStatus;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.servicelayer.cronjob.AbstractJobPerformable;
import de.hybris.platform.servicelayer.cronjob.PerformResult;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class AdnocB2BUnitRelationMappingJob extends AbstractJobPerformable<CronJobModel>
{
    private static final Logger LOG = LogManager.getLogger(AdnocB2BUnitRelationMappingJob.class);

    private AdnocB2BUnitService adnocB2BUnitService;

    @Override
    public PerformResult perform(final CronJobModel cronJobModel)
    {
        LOG.info("appEvent=AdnocB2BUnitRelationMappingJob, Started.");
        final Collection<B2BUnitModel> b2BUnitModelForChildMapping = getAdnocB2BUnitService().getB2BUnitsForChildMapping();
        LOG.info("appEvent=AdnocB2BUnitRelationMappingJob, found {} records for b2BUnitModelForChildMapping.", CollectionUtils.size(b2BUnitModelForChildMapping));
        for (final B2BUnitModel parentB2BUnitModel : b2BUnitModelForChildMapping)
        {
            final Set<String> childB2BUnitUids = new HashSet<>(parentB2BUnitModel.getChildB2BUnits());
            if (CollectionUtils.isEmpty(childB2BUnitUids))
            {
                continue;
            }
            final Collection<String> processedChildB2BUnitUids = new HashSet<>();
            processChildB2BUnitForRelation(parentB2BUnitModel, childB2BUnitUids, processedChildB2BUnitUids);
            LOG.info("appEvent=AdnocB2BUnitRelationMappingJob, removing childB2BUnitUids={} for parentB2BUnitUid={} as processed successfully.",
                    processedChildB2BUnitUids, parentB2BUnitModel.getUid());
            childB2BUnitUids.removeAll(processedChildB2BUnitUids);
            parentB2BUnitModel.setChildB2BUnits(childB2BUnitUids);
            modelService.save(parentB2BUnitModel);
        }
        return new PerformResult(CronJobResult.SUCCESS, CronJobStatus.FINISHED);
    }

    private void processChildB2BUnitForRelation(final B2BUnitModel parentB2BUnitModel, final Set<String> childB2BUnitUids,
                                                final Collection<String> processedChildB2BUnitUids)
    {
        for (String childB2BUnitUid : childB2BUnitUids)
        {
            childB2BUnitUid = StringUtils.trim(childB2BUnitUid);
            LOG.info("appEvent=AdnocB2BUnitRelationMappingJob, mapping started for childB2BUnitUid={} with parentB2BUnitUid={}.",
                    childB2BUnitUid, parentB2BUnitModel.getUid());
            final B2BUnitModel childB2BUnitModel = getChildB2BUnit(parentB2BUnitModel, processedChildB2BUnitUids, childB2BUnitUid);
            if (Objects.isNull(childB2BUnitModel))
            {
                continue;
            }

            processB2BUnitRelation(parentB2BUnitModel, childB2BUnitModel);
            try
            {
                modelService.save(childB2BUnitModel);
                processedChildB2BUnitUids.add(childB2BUnitUid);
            }
            catch (final ModelSavingException modelSavingException)
            {
                LOG.error("appEvent=AdnocB2BUnitRelationMappingJob, An exception {} occurred while saving childB2BUnitUid={} mapped with parentB2BUnitUid={}.",
                        ExceptionUtils.getRootCauseMessage(modelSavingException), childB2BUnitModel.getUid(), parentB2BUnitModel.getUid());
            }
        }
    }

    private void processB2BUnitRelation(final B2BUnitModel parentB2BUnitModel, final B2BUnitModel childB2BUnitModel)
    {
        LOG.info("appEvent=AdnocB2BUnitRelationMappingJob, found childB2BUnitUid={} to map with parentB2BUnitUid={}.",
                childB2BUnitModel.getUid(), parentB2BUnitModel.getUid());
        final Set<PrincipalGroupModel> existingParentB2BUnits = CollectionUtils.isNotEmpty(childB2BUnitModel.getGroups()) ?
                childB2BUnitModel.getGroups().stream().filter(B2BUnitModel.class::isInstance).map(B2BUnitModel.class::cast)
                        .collect(Collectors.toSet()) : new HashSet<>();
        existingParentB2BUnits.add(parentB2BUnitModel);
        childB2BUnitModel.setGroups(existingParentB2BUnits);
    }

    private B2BUnitModel getChildB2BUnit(final B2BUnitModel parentB2BUnitModel, final Collection<String> processedChildB2BUnitUids, final String childB2BUnitUid)
    {
        if (StringUtils.equals(childB2BUnitUid, parentB2BUnitModel.getUid()))
        {
            LOG.info("appEvent=AdnocB2BUnitRelationMappingJob, skipping mapping as it is self mapping.");
            processedChildB2BUnitUids.add(childB2BUnitUid);
            return null;
        }
        final B2BUnitModel childB2BUnitModel = getAdnocB2BUnitService().getUnitForUid(childB2BUnitUid);
        if (Objects.isNull(childB2BUnitModel))
        {
            LOG.info("appEvent=AdnocB2BUnitRelationMappingJob, skipping mapping as childB2BUnitUid not found with uid={}.", childB2BUnitUid);
            return null;
        }
        return childB2BUnitModel;
    }

    @Override
    public boolean isAbortable()
    {
        LOG.info("appEvent=AdnocB2BUnitRelationMappingJob, Aborted.");
        return true;
    }

    protected AdnocB2BUnitService getAdnocB2BUnitService()
    {
        return adnocB2BUnitService;
    }

    public void setAdnocB2BUnitService(final AdnocB2BUnitService adnocB2BUnitService)
    {
        this.adnocB2BUnitService = adnocB2BUnitService;
    }
}
