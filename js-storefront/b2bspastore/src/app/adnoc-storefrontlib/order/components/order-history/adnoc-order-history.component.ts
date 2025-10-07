/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component, OnDestroy, ViewEncapsulation } from '@angular/core';
import { Params } from '@angular/router';
import {
  isNotUndefined,
  RoutingService,
  TranslationService,
} from '@spartacus/core';
import {
  Order,
  OrderHistoryFacade,
  ReplenishmentOrderHistoryFacade,
} from '@spartacus/order/root';
import { combineLatest, Observable } from 'rxjs';
import { filter, map, take, tap } from 'rxjs/operators';
import { OrderHistoryList } from '../../root/model/adnoc-order.model';

@Component({
  selector: 'cx-order-history',
  templateUrl: './adnoc-order-history.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['./adnoc-order-history.component.scss'],
  encapsulation: ViewEncapsulation.None,
  host: { class: 'adnoc-order-history' },
  standalone: false,
})
export class AdnocOrderHistoryComponent implements OnDestroy {
  constructor(
    protected routing: RoutingService,
    protected orderHistoryFacade: OrderHistoryFacade,
    protected translation: TranslationService,
    protected replenishmentOrderHistoryFacade: ReplenishmentOrderHistoryFacade
  ) {
    this.orders$ = this.orderHistoryFacade
      .getOrderHistoryList(this.PAGE_SIZE)
      .pipe(
        tap((orders: OrderHistoryList | undefined) => {
          this.setOrderHistoryParams(orders);
        })
      );

    this.hasReplenishmentOrder$ = this.replenishmentOrderHistoryFacade
      .getReplenishmentOrderDetails()
      .pipe(map((order) => order && Object.keys(order).length !== 0));

    this.isLoaded$ = this.orderHistoryFacade.getOrderHistoryListLoaded();

    this.tabTitleParam$ = this.orders$.pipe(
      map((order) => order?.pagination?.totalResults),
      filter(isNotUndefined),
      take(1)
    );
  }

  private readonly PAGE_SIZE = 5;
  sortType!: string;
  hasPONumber: boolean | undefined;

  orders$: Observable<OrderHistoryList | undefined>;

  setOrderHistoryParams(orders: OrderHistoryList | undefined) {
    if (orders?.pagination?.sort) {
      this.sortType = orders.pagination.sort;
    }
    this.hasPONumber = orders?.orders?.[0]?.purchaseOrderNumber !== undefined;
  }

  hasReplenishmentOrder$: Observable<boolean>;

  isLoaded$: Observable<boolean>;

  /**
   * When "Order Return" feature is enabled, this component becomes one tab in
   * TabParagraphContainerComponent. This can be read from TabParagraphContainer.
   */
  tabTitleParam$: Observable<number>;

  ngOnDestroy(): void {
    this.orderHistoryFacade.clearOrderList();
  }

  changeSortCode(sortCode: string): void {
    const event: { sortCode: string; currentPage: number } = {
      sortCode,
      currentPage: 0,
    };
    this.sortType = sortCode;
    this.fetchOrders(event);
  }

  pageChange(page: number): void {
    const event: { sortCode: string; currentPage: number } = {
      sortCode: this.sortType,
      currentPage: page,
    };
    this.fetchOrders(event);
  }

  goToOrderDetail(order: Order): void {
    this.routing.go(
      {
        cxRoute: 'orderDetails',
        params: order,
      },
      {
        queryParams: this.getQueryParams(order),
      }
    );
  }

  getQueryParams(order: Order): Params | null {
    return this.orderHistoryFacade.getQueryParams(order);
  }

  getSortLabels(): Observable<{ byDate: string; byOrderNumber: string }> {
    return combineLatest([
      this.translation.translate('sorting.date'),
      this.translation.translate('sorting.orderNumber'),
    ]).pipe(
      map(([textByDate, textByOrderNumber]) => {
        return {
          byDate: textByDate,
          byOrderNumber: textByOrderNumber,
        };
      })
    );
  }

  private fetchOrders(event: { sortCode: string; currentPage: number }): void {
    this.orderHistoryFacade.loadOrderList(
      this.PAGE_SIZE,
      event.currentPage,
      event.sortCode
    );
  }
}
