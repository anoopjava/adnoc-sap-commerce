/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { AdnocCartValidationWarningsModule } from './adnoc-cart-warnings/adnoc-cart-validation-warnings.module';
import { CartItemValidationWarningModule } from './adnoc-cart-item-warning/adnoc-cart-item-validation-warning.module';

@NgModule({
  imports: [AdnocCartValidationWarningsModule, CartItemValidationWarningModule],
  providers: [],
})
export class CartValidationComponentsModule {}
