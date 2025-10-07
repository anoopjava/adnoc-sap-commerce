/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component, OnDestroy } from '@angular/core';
import { CartOutlets } from '@spartacus/cart/base/root';
import { Order, OrderFacade } from '@spartacus/order/root';
import { Observable } from 'rxjs';

@Component({
  selector: 'adnoc-order-confirmation-totals',
  templateUrl: './adnoc-order-confirmation-totals.component.html',
  styleUrl: './adnoc-order-confirmation-totals.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdnocOrderConfirmationTotalsComponent implements OnDestroy {
  readonly cartOutlets = CartOutlets;
  order$: Observable<Order | undefined> = this.orderFacade.getOrderDetails();

  constructor(protected orderFacade: OrderFacade) {}

  ngOnDestroy() {
    this.orderFacade.clearPlacedOrder();
  }
}
