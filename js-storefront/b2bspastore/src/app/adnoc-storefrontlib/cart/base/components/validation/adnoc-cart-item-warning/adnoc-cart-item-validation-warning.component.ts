/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CartModification, CartValidationFacade } from '@spartacus/cart/base/root';
import { ICON_TYPE } from '@spartacus/storefront';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
    selector: 'cx-cart-item-validation-warning',
    templateUrl: './adnoc-cart-item-validation-warning.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CartItemValidationWarningComponent {
  @Input()
  code!: string;

  iconTypes = ICON_TYPE;
  isVisible = true;

  cartModification$: Observable<CartModification | undefined>;

  constructor(protected cartValidationFacade: CartValidationFacade) {
    this.cartModification$ = this.cartValidationFacade
    .getValidationResults()
    .pipe(
      //@ts-ignore
      map((modificationList) =>
        modificationList.find(
          //@ts-ignore
          (modification) => modification.entry?.product?.code === this.code
        )
      )
    );
  }
}
