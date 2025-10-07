package com.adnoc.controllers;

import com.adnoc.b2bocc.config.data.AdnocConfigListWsDTO;
import com.adnoc.b2bocc.config.data.AdnocConfigWsDTO;
import com.adnoc.facades.config.AdnocConfigFacade;
import com.adnoc.facades.config.data.AdnocConfigData;
import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.annotation.SecurePortalUnauthenticatedAccess;
import de.hybris.platform.webservicescommons.mapping.DataMapper;
import de.hybris.platform.webservicescommons.mapping.FieldSetLevelHelper;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * Adnoc B2B Config Controller
 */
@RestController
@ApiVersion("v2")
@Tag(name = "Adnoc B2B Config")
public class AdnocB2BConfigController
{
    @Resource(name = "adnocConfigFacade")
    private AdnocConfigFacade adnocConfigFacade;

    @Resource(name = "dataMapper")
    private DataMapper dataMapper;

    @SecurePortalUnauthenticatedAccess
    @GetMapping(value = "/{baseSiteId}/adnocConfig")
    @Operation(operationId = "getAdnocConfig", summary = "get adnoc config.")
    @ApiBaseSiteIdParam
    public AdnocConfigWsDTO getAdnocConfig(@Parameter(description = "adnoc config to get.", required = true)
                                               @RequestParam final String configKey)
    {
        final AdnocConfigData adnocConfigData = adnocConfigFacade.getAdnocConfig(configKey);
        final AdnocConfigWsDTO adnocConfigWsDTO = dataMapper.map(adnocConfigData, AdnocConfigWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL);

        return adnocConfigWsDTO;
    }

    @SecurePortalUnauthenticatedAccess
    @GetMapping(value = "/{baseSiteId}/adnocConfigs")
    @Operation(operationId = "getAdnocConfigs", summary = "get adnoc configs.")
    @ApiBaseSiteIdParam
    public AdnocConfigListWsDTO getAdnocConfigs(@Parameter(description = "adnoc configs to get.", required = true)
                                               @RequestParam final String... configKeys)
    {
        final AdnocConfigListWsDTO adnocConfigListWsDTO = new AdnocConfigListWsDTO();
        final List<AdnocConfigData> adnocConfigDataList = adnocConfigFacade.getAdnocConfigs(configKeys);
        adnocConfigListWsDTO.setAdnocConfigs(dataMapper.mapAsList(adnocConfigDataList, AdnocConfigWsDTO.class, FieldSetLevelHelper.DEFAULT_LEVEL));

        return adnocConfigListWsDTO;
    }
}
