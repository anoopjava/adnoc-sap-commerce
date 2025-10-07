/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Cart } from '@spartacus/cart/base/root';
import { Observable } from 'rxjs';
import { AdnocActiveCartFacade } from '../../root/facade/adnoc-active-cart.facade';

@Component({
    selector: 'adnoc-cart-totals',
    templateUrl: './adnoc-cart-totals.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class AdnocCartTotalsComponent implements OnInit {
  cart$!: Observable<Cart>;

  constructor(protected activeCartService: AdnocActiveCartFacade) {}

  ngOnInit() {
    this.cart$ = this.activeCartService.getActive();
  }
}
