/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { AdnocReturnOrderConfirmationModule } from './adnoc-return-order-confirmation/adnoc-return-order-confirmation.module';
import { AdnocReturnOrderModule } from './adnoc-return-order/adnoc-return-order.module';

@NgModule({
  imports: [AdnocReturnOrderModule, AdnocReturnOrderConfirmationModule],
})
export class OrderReturnModule {}
