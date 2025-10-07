/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { GlobalMessageType, RoutingService } from '@spartacus/core';
import { OrderReturnRequestFacade, ReturnRequest } from '@spartacus/order/root';
import { combineLatest, Observable } from 'rxjs';
import { distinctUntilChanged, filter, map, tap } from 'rxjs/operators';
import { AdnocGlobalMessageService } from '../../../../core/global-message/facade/adnoc-global-message.service';

@Injectable({
  providedIn: 'root',
})
export class AdnocReturnRequestService {
  constructor(
    protected routingService: RoutingService,
    protected returnRequestService: OrderReturnRequestFacade,
    protected globalMessageService: AdnocGlobalMessageService
  ) {}

  get isCancelling$(): Observable<boolean> {
    return this.returnRequestService.getCancelReturnRequestLoading();
  }

  get isCancelSuccess$(): Observable<boolean> {
    return this.returnRequestService.getCancelReturnRequestSuccess();
  }

  getReturnRequest(): Observable<ReturnRequest> {
    return combineLatest([
      this.routingService.getRouterState(),
      this.returnRequestService.getOrderReturnRequest(),
      this.returnRequestService.getReturnRequestLoading(),
    ]).pipe(
      map(([routingState, returnRequest, isLoading]) => [
        routingState.state.params['returnCode'],
        returnRequest,
        isLoading,
      ]),
      filter(([returnCode]) => Boolean(returnCode)),
      tap(([returnCode, returnRequest, isLoading]) => {
        if (
          (returnRequest === undefined || returnRequest.rma !== returnCode) &&
          !isLoading
        ) {
          this.returnRequestService.loadOrderReturnRequestDetail(returnCode);
        }
      }),
      map(([_, returnRequest]) => returnRequest),
      filter((returnRequest) => Boolean(returnRequest)),
      distinctUntilChanged()
    );
  }

  clearReturnRequest(): void {
    this.returnRequestService.clearOrderReturnRequestDetail();
  }

  cancelReturnRequest(returnRequestCode: string): void {
    this.returnRequestService.cancelOrderReturnRequest(returnRequestCode, {
      status: 'CANCELLING',
    });
  }

  cancelSuccess(rma: string): void {
    this.returnRequestService.resetCancelReturnRequestProcessState();
    this.globalMessageService.add(
      {
        key: 'returnRequest.cancelSuccess',
        params: { rma },
      },
      GlobalMessageType.MSG_TYPE_CONFIRMATION
    );
    this.routingService.go({
      cxRoute: 'orders',
    });
  }

  backToList(): void {
    this.routingService.go(
      { cxRoute: 'orders' },
      {
        state: {
          activeTab: 1,
        },
      }
    );
  }
}
