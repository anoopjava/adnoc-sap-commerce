import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {
  AuthGuard,
  CmsConfig,
  FeaturesConfigModule,
  I18nModule,
  provideDefaultConfig,
} from '@spartacus/core';
import {
  FormErrorsModule,
  MessageComponentModule,
} from '@spartacus/storefront';
import { AdnocAmendOrderItemsModule } from '../adnoc-amend-order-items/adnoc-amend-order-items.module';
import { AdnocCancelOrderComponent } from './adnoc-cancel-order.component';
import { AdnocOrderCancellationService } from '../adnoc-order-cancellation.service';
import { AdnocOrderAmendService } from '../../adnoc-amend-order.service';
import { AmendOrderActionsModule } from '@spartacus/order/components';

@NgModule({
  imports: [
    CommonModule,
    I18nModule,
    AdnocAmendOrderItemsModule,
    AmendOrderActionsModule,
    FormErrorsModule,
    MessageComponentModule,
    FeaturesConfigModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        CancelOrderComponent: {
          component: AdnocCancelOrderComponent,
          guards: [AuthGuard],
          providers: [
            {
              provide: AdnocOrderAmendService,
              useExisting: AdnocOrderCancellationService,
            },
          ],
        },
      },
    }),
  ],
  declarations: [AdnocCancelOrderComponent],
  exports: [AdnocCancelOrderComponent],
})
export class AdnocCancelOrderModule {}
