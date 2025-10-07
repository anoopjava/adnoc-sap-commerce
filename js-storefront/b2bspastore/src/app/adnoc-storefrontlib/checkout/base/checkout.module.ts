/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
//import { CheckoutCoreModule } from '@spartacus/checkout/base/core';
import { CheckoutComponentsModule } from './components/checkout-components.module';
import { CheckoutOccModule } from './occ/checkout-occ.module';
import { CheckoutCoreModule } from './core/checkout-core.module';

@NgModule({
  imports: [CheckoutComponentsModule, CheckoutCoreModule, CheckoutOccModule],
})
export class CheckoutModule {}
