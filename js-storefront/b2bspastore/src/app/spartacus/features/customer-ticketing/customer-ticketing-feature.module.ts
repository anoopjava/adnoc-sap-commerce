import { NgModule } from '@angular/core';
import { CmsConfig, I18nConfig, provideConfig } from '@spartacus/core';
import {
  customerTicketingTranslationChunksConfig,
  customerTicketingTranslations,
} from '@spartacus/customer-ticketing/assets';
import { CUSTOMER_TICKETING_FEATURE } from '@spartacus/customer-ticketing/root';
import { CustomerTicketingRootModule } from './root/customer-ticketing-root.module';

@NgModule({
  declarations: [],
  imports: [CustomerTicketingRootModule],
  providers: [
    provideConfig(<CmsConfig>{
      featureModules: {
        [CUSTOMER_TICKETING_FEATURE]: {
          module: () =>
            import(
              '../../../adnoc-storefrontlib/customer-ticketing/adnoc-customer-ticketing.module'
            ).then((m) => m.AdnocCustomerTicketingModule),
        },
      },
    }),
    provideConfig(<I18nConfig>{
      i18n: {
        resources: customerTicketingTranslations,
        chunks: customerTicketingTranslationChunksConfig,
      },
    }),
  ],
})
export class CustomerTicketingFeatureModule {}
