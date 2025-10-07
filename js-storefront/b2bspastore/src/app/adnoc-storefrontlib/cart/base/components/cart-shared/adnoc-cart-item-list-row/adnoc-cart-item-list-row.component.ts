/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Component, inject, ViewEncapsulation } from '@angular/core';
import { CartItemContext } from '@spartacus/cart/base/root';
import { AdnocCartItemComponent } from '../cart-item/adnoc-cart-item.component';
import { CartItemContextSource } from '../cart-item/model/cart-item-context-source.model';
import { CartItemListComponentService } from './adnoc-cart-item-list-row.component.service';

@Component({
    selector: '[adnoc-cart-item-list-row], adnoc-cart-item-list-row',
    templateUrl: './adnoc-cart-item-list-row.component.html',
    styleUrls: ['./adnoc-cart-item-list-row.component.scss'],
    encapsulation: ViewEncapsulation.None,
    providers: [
        CartItemContextSource,
        { provide: CartItemContext, useExisting: CartItemContextSource },
    ],
    standalone: false
})
export class CartItemListRowComponent extends AdnocCartItemComponent {
  componentService = inject(CartItemListComponentService);
  isFlagQuote = this.componentService.showBasePriceWithDiscount();
  constructor(cartItemContextSource: CartItemContextSource) {
    super(cartItemContextSource);
  }
}
