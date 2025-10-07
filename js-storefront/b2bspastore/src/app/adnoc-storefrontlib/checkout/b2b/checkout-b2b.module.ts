/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { AdnocCheckoutB2BComponentsModule } from './components/checkout-b2b-components.module';
import { CheckoutB2BCoreModule } from './core/checkout-b2b-core.module';
import { CheckoutB2BOccModule } from './occ/checkout-b2b-occ.module';

@NgModule({
  imports: [
    AdnocCheckoutB2BComponentsModule,
    CheckoutB2BCoreModule,
    CheckoutB2BOccModule,
  ],
})
export class CheckoutB2BModule {}
