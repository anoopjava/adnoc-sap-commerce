/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ActiveCartOrderEntriesContextToken } from '@spartacus/cart/base/root';
import { OutletModule, PAGE_LAYOUT_HANDLER } from '@spartacus/storefront';
import { AdnocCartDetailsModule } from './adnoc-cart-details/adnoc-cart-details.module';
import { AdnocCartSharedModule } from './cart-shared/adnoc-cart-shared.module';
import {
  ActiveCartOrderEntriesContext,
  CartPageLayoutHandler,
  SaveForLaterModule,
} from '@spartacus/cart/base/components';
import { AdnocClearCartModule } from './clear-cart';
import { AdnocCartTotalsModule } from './adnoc-cart-totals/adnoc-cart-totals.module';
import { CartProceedToCheckoutModule } from './adnoc-cart-proceed-to-checkout/adnoc-cart-proceed-to-checkout.module';
import { AdnocAddedToCartDialogModule } from './adnoc-added-to-cart-dialog/adnoc-added-to-cart-dialog.module';

@NgModule({
  imports: [
    CommonModule,
    AdnocCartDetailsModule,
    CartProceedToCheckoutModule,
    AdnocCartTotalsModule,
    AdnocCartSharedModule,
    SaveForLaterModule,
    AdnocClearCartModule,
    OutletModule.forChild(),
  ],
  exports: [
    AdnocCartDetailsModule,
    CartProceedToCheckoutModule,
    AdnocCartTotalsModule,
    AdnocCartSharedModule,
    AdnocClearCartModule,
    AdnocAddedToCartDialogModule,
    SaveForLaterModule,
  ],
  providers: [
    {
      provide: PAGE_LAYOUT_HANDLER,
      useExisting: CartPageLayoutHandler,
      multi: true,
    },
    {
      provide: ActiveCartOrderEntriesContextToken,
      useExisting: ActiveCartOrderEntriesContext,
    },
  ],
})
export class AdnocCartBaseComponentsModule {}
