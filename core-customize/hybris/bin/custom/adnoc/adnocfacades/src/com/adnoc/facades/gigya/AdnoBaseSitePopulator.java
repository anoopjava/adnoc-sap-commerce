package com.adnoc.facades.gigya;

import com.adnoc.facades.gigya.data.GigyaConfigData;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.basesite.data.BaseSiteData;
import de.hybris.platform.commercefacades.basesites.converters.populator.BaseSitePopulator;
import de.hybris.platform.gigya.gigyaservices.model.GigyaConfigModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;

public class AdnoBaseSitePopulator extends BaseSitePopulator
{
    private Converter<GigyaConfigModel, GigyaConfigData> adnocGigyaConfigConverter;

    @Override
    public void populate(final BaseSiteModel source, final BaseSiteData target)
    {
        super.populate(source, target);
        Optional.ofNullable(source.getGigyaConfig())
                .ifPresent(config -> target.setGigyaConfigData(getAdnocGigyaConfigConverter().convert(config)));
    }

    protected Converter<GigyaConfigModel, GigyaConfigData> getAdnocGigyaConfigConverter()
    {
        return adnocGigyaConfigConverter;
    }

    public void setAdnocGigyaConfigConverter(final Converter<GigyaConfigModel, GigyaConfigData> adnocGigyaConfigConverter)
    {
        this.adnocGigyaConfigConverter = adnocGigyaConfigConverter;
    }

}
