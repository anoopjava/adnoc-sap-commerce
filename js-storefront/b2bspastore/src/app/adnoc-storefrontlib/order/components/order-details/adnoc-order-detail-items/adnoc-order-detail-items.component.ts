/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Component, inject, OnInit } from '@angular/core';
import {
  AbstractOrderType,
  OrderEntry,
  PromotionLocation,
} from '@spartacus/cart/base/root';
import {
  CmsOrderDetailItemsComponent,
  GlobalMessageType,
} from '@spartacus/core';
import { Consignment, OrderOutlets } from '@spartacus/order/root';
import { CmsComponentData } from '@spartacus/storefront';
import { Observable, of } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { OrderDetailsService } from '../order-details.service';
import { MyAccountV2OrderConsignmentsService } from '@spartacus/order/components';
import { AdnocCartOutlets } from '../../../../cart/base/root/models/cart-outlets.model';
import { AdnocOrder } from '../../../../../core/model/adnoc-users.model';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';
import _ from 'lodash';

@Component({
  selector: 'cx-order-details-items',
  templateUrl: './adnoc-order-detail-items.component.html',
  host: { class: 'adnoc-order-details-items' },
  standalone: false,
})
export class AdnocOrderDetailItemsComponent implements OnInit {
  protected orderConsignmentsService = inject(
    MyAccountV2OrderConsignmentsService
  );
  readonly OrderOutlets = OrderOutlets;
  readonly CartOutlets = AdnocCartOutlets;
  readonly abstractOrderType = AbstractOrderType;
  protected globalMessageService = inject(AdnocGlobalMessageService);
  promotionLocation: PromotionLocation = PromotionLocation.Order;

  pickupConsignments: Consignment[] | undefined;
  deliveryConsignments: Consignment[] | undefined;

  pickupUnconsignedEntries: OrderEntry[] | undefined;
  deliveryUnConsignedEntries: OrderEntry[] | undefined;

  order$: Observable<AdnocOrder>;
  enableAddToCart$: Observable<boolean | undefined>;
  isOrderLoading$: Observable<boolean>;

  groupCartItems$: Observable<boolean | undefined>;
  SortByEntryData!: Consignment[];

  ngOnInit(): void {
    this.globalMessageService.remove(GlobalMessageType.MSG_TYPE_ERROR);
  }

  constructor(
    protected orderDetailsService: OrderDetailsService,
    protected component: CmsComponentData<CmsOrderDetailItemsComponent>
  ) {
    this.order$ = this.orderDetailsService.getOrderDetails().pipe(
      tap((order) => {
       
        this.pickupConsignments = this.getGroupedConsignments(order, true);
        this.deliveryConsignments = this.getGroupedConsignments(order, false);
        this.groupByEntryNumber(this.deliveryConsignments || []);
        this.pickupUnconsignedEntries = this.getUnconsignedEntries(order, true);
        this.deliveryUnConsignedEntries = this.getUnconsignedEntries(
          order,
          false
        );
      })
    );

    this.enableAddToCart$ = this.component.data$.pipe(
      map((data) => data.enableAddToCart)
    );

    this.isOrderLoading$ =
      typeof this.orderDetailsService.isOrderDetailsLoading === 'function'
        ? this.orderDetailsService.isOrderDetailsLoading()
        : of(false);

    this.groupCartItems$ = this.component.data$.pipe(
      map((data) => data.groupCartItems)
    );
  }

  groupByEntryNumber(consignmentData: Consignment[]) {
    this.SortByEntryData = Array.isArray(consignmentData)
      ? consignmentData.map((order) => ({
          ...order,
          entries: _.sortBy(
            order.entries || [],
            (entry) => entry?.orderEntry?.entryNumber
          ),
        }))
      : [];
  }

  protected getGroupedConsignments(
    order: AdnocOrder,
    pickup: boolean
  ): Consignment[] | undefined {
    return this.orderConsignmentsService.getGroupedConsignments(order, pickup);
  }

  protected getUnconsignedEntries(
    order: AdnocOrder,
    pickup: boolean
  ): OrderEntry[] | undefined {
    return this.orderConsignmentsService.getUnconsignedEntries(order, pickup);
  }
}
