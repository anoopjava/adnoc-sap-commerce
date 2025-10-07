/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { OrderEntry } from '@spartacus/cart/base/root';
import { GlobalMessageType, RoutingService } from '@spartacus/core';
import { ReturnRequest } from '@spartacus/order/root';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { AmendOrderType } from '../amend-order.model';
import { AdnocOrderAmendService } from '../adnoc-amend-order.service';
import { OrderDetailsService } from '../../order-details/order-details.service';
import { CancelOrReturnRequestEntryInput } from '../../../../../core/model/adnoc-users.model';
import { AdnocApiEndpoints } from '../../../../services/apiServices/adnoc-api-endpoints';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

@Injectable({
  providedIn: 'root',
})
export class OrderReturnService extends AdnocOrderAmendService {
  override amendType = AmendOrderType.RETURN;

  constructor(
    protected override orderDetailsService: OrderDetailsService,
    protected routing: RoutingService,
    protected globalMessageService: AdnocGlobalMessageService
  ) {
    super(orderDetailsService);
  }

  getEntries(): Observable<OrderEntry[]> {
    return this.getOrder().pipe(
      filter((order) => !!order.entries),
      map(
        (order) =>
          order.entries?.filter(
            (entry) =>
              entry.entryNumber !== -1 &&
              entry.returnableQuantity &&
              entry.returnableQuantity > 0
          ) ?? []
      )
    );
  }

  save(): void {
    const orderCode = this.form.value.orderCode;
    const entries = this.form.value.entries;
    const returnReason = {
      code: this.form.value.returnReason.split('|')[1],
      name: this.form.value.returnReason.split('|')[0],
    };
    const file = this.fileSubject.getValue();
    const inputs: CancelOrReturnRequestEntryInput[] = Object.keys(entries)
      .filter((entryNumber) => <number>entries[entryNumber] > 0)
      .map(
        (entryNumber) =>
          ({
            orderEntryNumber: Number(entryNumber),
            quantity: <number>entries[entryNumber],
          } as CancelOrReturnRequestEntryInput)
      );
    const formData = new FormData();
    const returnRequestData = {
      orderCode: orderCode,
      returnReason: returnReason,
      returnRequestEntryInputs: inputs,
    };
    formData.append('returnRequestData', JSON.stringify(returnRequestData));
    if (file) {
      formData.append('returnRequestDocument', file, file.name);
    }
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.adnocReturnOrder,
      {
        urlParams: {
          orderCode,
        },
      }
    );
    this.form.reset();
    this.http.post<ReturnRequest>(url, formData).subscribe(
      (response: ReturnRequest) => {
        this.afterSave(response);
      },
      (error) => {
        let errorType = GlobalMessageType.MSG_TYPE_ERROR;
        this.globalMessageService.remove(GlobalMessageType.MSG_TYPE_ERROR);
        if (error.error?.errors?.length) {
          let errorMessage: string = error.error.errors
            .map((err: any) => err.message)
            .join('\n');
          this.globalMessageService.add(errorMessage, errorType, 10000);
        }
      }
    );
  }

  private afterSave(response: ReturnRequest): void {
    const rma = response.rma;
    this.globalMessageService.add(
      {
        key: 'orderDetails.cancellationAndReturn.returnSuccess',
        params: { rma },
      },
      GlobalMessageType.MSG_TYPE_CONFIRMATION
    );
    this.routing.go({
      cxRoute: 'returnRequestDetails',
      params: { rma },
    });
  }
}
