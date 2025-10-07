/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { OrderDetailsService } from '../order-details.service';
import { AdnocCartOutlets } from '../../../../cart/base/root/models/cart-outlets.model';

@Component({
    selector: 'cx-order-details-totals',
    templateUrl: './adnoc-order-detail-totals.component.html',
    host: { class: 'adnoc-order-details-totals' },
    standalone: false
})
export class AdnocOrderDetailTotalsComponent implements OnInit {
  constructor(protected orderDetailsService: OrderDetailsService) {}

  order$!: Observable<any>;

  readonly CartOutlets = AdnocCartOutlets;

  ngOnInit() {
    this.order$ = this.orderDetailsService.getOrderDetails();
  }
}

