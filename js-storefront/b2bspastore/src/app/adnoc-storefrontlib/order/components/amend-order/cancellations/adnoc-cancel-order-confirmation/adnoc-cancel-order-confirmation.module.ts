/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { AuthGuard, CmsConfig, provideDefaultConfig } from '@spartacus/core';
import { AmendOrderActionsModule } from '@spartacus/order/components';
import { AdnocAmendOrderItemsModule } from '../adnoc-amend-order-items/adnoc-amend-order-items.module';
import { AdnocCancelOrderConfirmationComponent } from './adnoc-cancel-order-confirmation.component';
import { AdnocOrderAmendService } from '../../adnoc-amend-order.service';
import { AdnocOrderCancellationService } from '../adnoc-order-cancellation.service';
import { AdnocOrderCancellationGuard } from '../adnoc-order-cancellation.guard';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    AdnocAmendOrderItemsModule,
    AmendOrderActionsModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        CancelOrderConfirmationComponent: {
          component: AdnocCancelOrderConfirmationComponent,
          guards: [AuthGuard, AdnocOrderCancellationGuard],
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
  declarations: [AdnocCancelOrderConfirmationComponent],
  exports: [AdnocCancelOrderConfirmationComponent],
})
export class AdnocCancelOrderConfirmationModule {}
