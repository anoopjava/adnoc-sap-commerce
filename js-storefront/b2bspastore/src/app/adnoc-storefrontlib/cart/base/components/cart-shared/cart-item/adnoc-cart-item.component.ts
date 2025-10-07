/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';
import {
  CartItemComponentOptions,
  CartItemContext,
  CartOutlets,
  OrderEntry,
  PromotionLocation,
} from '@spartacus/cart/base/root';
import { ICON_TYPE } from '@spartacus/storefront';
import { CartItemContextSource } from './model/cart-item-context-source.model';
import { useFeatureStyles } from '@spartacus/core';
import { CART_MAXQTY } from '../../../../../shared/constants';
import { AdnocCartOutlets } from '../../../root/models/cart-outlets.model';
import { AdnocOrderEntry } from '../../../../../../core/model/adnoc-cart.model';

@Component({
  selector: 'cx-cart-item',
  templateUrl: './adnoc-cart-item.component.html',
  providers: [
    CartItemContextSource,
    { provide: CartItemContext, useExisting: CartItemContextSource },
  ],
  host: { class: 'adnoc-cart-item' },
  standalone: false,
})
export class AdnocCartItemComponent implements OnChanges {
  @Input() compact = false;

  @Input() item!: AdnocOrderEntry;
  @Input() readonly = false;

  @Input() quantityControl!: UntypedFormControl;

  @Input() promotionLocation: PromotionLocation = PromotionLocation.ActiveCart;

  // TODO: evaluate whether this is generic enough
  @Input() options: CartItemComponentOptions = {
    isSaveForLater: false,
    optionalBtn: null,
    displayAddToCart: false,
  };
  minOrderQuantity!: number;
  maxOrderQuantity!: number;
  stockLevel!: number;
  iconTypes = ICON_TYPE;
  readonly CartOutlets = AdnocCartOutlets;

  constructor(protected cartItemContextSource: CartItemContextSource) {
    useFeatureStyles('a11yCartItemsLinksStyles');
  }
  ngOnChanges(changes?: SimpleChanges) {
    if (changes?.['compact']) {
      this.cartItemContextSource.compact$.next(this.compact);
    }

    if (changes?.['readonly']) {
      this.cartItemContextSource.readonly$.next(this.readonly);
    }

    if (changes?.['item'] && this.item) {
      this.cartItemContextSource.item$.next(this.item);

      const product = this.item.product as any;
      const stock = product.stock;

      this.minOrderQuantity = product?.minOrderQuantity ?? 1;
      this.maxOrderQuantity = product?.maxOrderQuantity ?? CART_MAXQTY;
      this.stockLevel = stock?.stockLevel ?? CART_MAXQTY;

      if (this.maxOrderQuantity > this.stockLevel) {
        this.maxOrderQuantity = this.stockLevel;
      }
    }
    if (changes?.['quantityControl']) {
      this.cartItemContextSource.quantityControl$.next(this.quantityControl);
    }
    if (changes?.['promotionLocation']) {
      this.cartItemContextSource.location$.next(this.promotionLocation);
    }

    if (changes?.['options']) {
      this.cartItemContextSource.options$.next(this.options);
    }
  }

  isProductOutOfStock(product: any): boolean {
    // TODO Move stocklevelstatuses across the app to an enum
    return (
      product &&
      product.stock &&
      product.stock.stockLevelStatus === 'outOfStock'
    );
  }

  removeItem() {
    this.quantityControl.setValue(0);
    this.quantityControl.markAsDirty();
  }
}
