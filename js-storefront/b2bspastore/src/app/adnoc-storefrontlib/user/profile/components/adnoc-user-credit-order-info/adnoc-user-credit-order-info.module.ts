import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  CmsConfig,
  ConfigModule,
  FeaturesConfigModule,
  I18nModule,
  provideDefaultConfig,
  UrlModule,
} from '@spartacus/core';
import { SpinnerModule } from '@spartacus/storefront';
import { AdnocUserCreditOrderInfoComponent } from './adnoc-user-credit-order-info.component';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [AdnocUserCreditOrderInfoComponent],
  imports: [
    CommonModule,
    I18nModule,
    UrlModule,
    RouterModule,
    FeaturesConfigModule,
    SpinnerModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        AdnocHomepageUserCreditOrderDetailsComponent: {
          component: AdnocUserCreditOrderInfoComponent,
        },
      },
    }),
  ],
  exports: [AdnocUserCreditOrderInfoComponent],
})
export class AdnocUserCreditOrderInfoModule {}
