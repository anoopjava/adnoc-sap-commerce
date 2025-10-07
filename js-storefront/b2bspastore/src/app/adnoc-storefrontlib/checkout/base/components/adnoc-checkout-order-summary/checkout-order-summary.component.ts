/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Cart, CartOutlets } from '@spartacus/cart/base/root';
import { Observable } from 'rxjs';
import { AdnocCartOutlets } from '../../../../cart/base/root/models/cart-outlets.model';
import { AdnocActiveCartFacade } from '../../../../cart/base/root/facade/adnoc-active-cart.facade';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
    selector: 'cx-checkout-order-summary',
    templateUrl: './checkout-order-summary.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CheckoutOrderSummaryComponent {
  cart$: Observable<Cart>;
  isReviewPage = false;
  readonly cartOutlets = AdnocCartOutlets;
  private router = inject(Router); 
  private activatedRoute=inject(ActivatedRoute);
  constructor(protected activeCartFacade: AdnocActiveCartFacade) {
    this.cart$ = this.activeCartFacade.getActive();
    this.isReviewPage = this.router.url.includes('payment-type');
    
  }
}
