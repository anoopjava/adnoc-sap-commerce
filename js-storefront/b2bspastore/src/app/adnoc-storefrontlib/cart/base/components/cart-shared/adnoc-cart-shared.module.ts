/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { FeaturesConfigModule, I18nModule, UrlModule } from '@spartacus/core';
import {
  AtMessageModule,
  IconModule,
  MediaModule,
  OutletModule,
  PopoverModule,
  PromotionsModule,
  provideOutlet,
} from '@spartacus/storefront';

import { CartItemListRowComponent } from './adnoc-cart-item-list-row/adnoc-cart-item-list-row.component';
import { CartItemListComponent } from './adnoc-cart-item-list/adnoc-cart-item-list.component';
import { AdnocCartItemComponent } from './cart-item/adnoc-cart-item.component';
import { AdnocOrderSummaryComponent } from './adnoc-order-summary/adnoc-order-summary.component';
import { CartItemValidationWarningModule } from '../validation/adnoc-cart-item-warning/adnoc-cart-item-validation-warning.module';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import {MatCheckboxModule} from '@angular/material/checkbox';
import { provideNativeDateAdapter } from '@angular/material/core';
import { AdnocCalendarComponent } from './adnoc-date/adnoc-calendar.component';
import { AdnocCartCouponModule } from '../cart-coupon/adnoc-cart-coupon.module';
import { AdnocAddToCartModule } from '../adnoc-add-to-cart/adnoc-add-to-cart.module';
import { AdnocCartOutlets } from '../../root/models/cart-outlets.model';
import { ItemCounterModule } from '../../../../shared/components/item-counter/item-counter.module';

@NgModule({
  imports: [
    FormsModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatCheckboxModule,
    AtMessageModule,
    AdnocCartCouponModule,
    CartItemValidationWarningModule,
    CommonModule,
    I18nModule,
    IconModule,
    ItemCounterModule,
    MediaModule,
    OutletModule,
    PromotionsModule,
    ReactiveFormsModule,
    RouterModule,
    UrlModule,
    AdnocAddToCartModule,
    FeaturesConfigModule,
    PopoverModule
  ],
  providers: [
    provideNativeDateAdapter(),
    provideOutlet({
      id: AdnocCartOutlets.ORDER_SUMMARY,
      component: AdnocOrderSummaryComponent,
    }),
    provideOutlet({
      id: AdnocCartOutlets.CART_ITEM_LIST,
      component: CartItemListComponent,
    }),
  ],
  declarations: [
    AdnocCartItemComponent,
    AdnocOrderSummaryComponent,
    CartItemListComponent,
    CartItemListRowComponent,
    AdnocCalendarComponent,
  ],
  exports: [
    AdnocCartItemComponent,
    CartItemListRowComponent,
    CartItemListComponent,
    AdnocOrderSummaryComponent,
    AdnocCalendarComponent,
  ],
})
export class AdnocCartSharedModule {}
