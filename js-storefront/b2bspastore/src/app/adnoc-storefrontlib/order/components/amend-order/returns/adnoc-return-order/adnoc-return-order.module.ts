/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { AuthGuard, CmsConfig, provideDefaultConfig } from '@spartacus/core';
import { FormErrorsModule } from '@spartacus/storefront';
import { AdnocAmendOrderActionsModule } from '../../adnoc-amend-order-actions/adnoc-amend-order-actions.module';
import { AmendOrderItemsModule } from '../../adnoc-amend-order-items/adnoc-amend-order-items.module';
import { AdnocOrderAmendService } from '../../adnoc-amend-order.service';
import { OrderReturnService } from '../order-return.service';
import { AdnocReturnOrderComponent } from './adnoc-return-order.component';

@NgModule({
  imports: [
    CommonModule,
    AmendOrderItemsModule,
    AdnocAmendOrderActionsModule,
    FormErrorsModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        ReturnOrderComponent: {
          component: AdnocReturnOrderComponent,
          guards: [AuthGuard],
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
  declarations: [AdnocReturnOrderComponent],
  exports: [AdnocReturnOrderComponent],
})
export class AdnocReturnOrderModule {}
