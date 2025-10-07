/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { AdnocCancelOrderConfirmationModule } from './adnoc-cancel-order-confirmation/adnoc-cancel-order-confirmation.module';
import { AdnocCancelOrderModule } from './adnoc-cancel-order/adnoc-cancel-order.module';

@NgModule({
  imports: [AdnocCancelOrderModule, AdnocCancelOrderConfirmationModule],
})
export class AdnocOrderCancellationModule {}
