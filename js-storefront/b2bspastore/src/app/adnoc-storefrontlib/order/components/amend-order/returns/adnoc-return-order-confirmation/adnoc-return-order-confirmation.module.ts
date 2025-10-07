/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import {
  AuthGuard,
  CmsConfig,
  I18nModule,
  provideDefaultConfig,
} from '@spartacus/core';
import { AdnocAmendOrderActionsModule } from '../../adnoc-amend-order-actions/adnoc-amend-order-actions.module';
import { AmendOrderItemsModule } from '../../adnoc-amend-order-items/adnoc-amend-order-items.module';
import { AdnocOrderAmendService } from '../../adnoc-amend-order.service';
import { OrderReturnGuard } from '../order-return.guard';
import { OrderReturnService } from '../order-return.service';
import { AdnocReturnOrderConfirmationComponent } from './adnoc-return-order-confirmation.component';

@NgModule({
  imports: [
    CommonModule,
    AmendOrderItemsModule,
    I18nModule,
    ReactiveFormsModule,
    AdnocAmendOrderActionsModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        ReturnOrderConfirmationComponent: {
          component: AdnocReturnOrderConfirmationComponent,
          guards: [AuthGuard, OrderReturnGuard],
          providers: [
            {
              provide: AdnocOrderAmendService,
              useExisting: OrderReturnService,
            },
          ],
        },
      },
    }),
  ],
  declarations: [AdnocReturnOrderConfirmationComponent],
  exports: [AdnocReturnOrderConfirmationComponent],
})
export class AdnocReturnOrderConfirmationModule {}
