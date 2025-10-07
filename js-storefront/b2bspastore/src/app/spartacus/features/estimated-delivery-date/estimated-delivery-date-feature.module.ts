import { NgModule } from '@angular/core';
import { I18nConfig, provideConfig } from "@spartacus/core";
import { estimatedDeliveryDateTranslationChunksConfig, estimatedDeliveryDateTranslations } from "@spartacus/estimated-delivery-date/assets";
import { EstimatedDeliveryDateRootModule } from "@spartacus/estimated-delivery-date/root";

@NgModule({
  declarations: [],
  imports: [
    EstimatedDeliveryDateRootModule
  ],
  providers: [provideConfig(<I18nConfig>{
    i18n: {
      resources: estimatedDeliveryDateTranslations,
      chunks: estimatedDeliveryDateTranslationChunksConfig,
    },
  })
  ]
})
export class EstimatedDeliveryDateFeatureModule { }
