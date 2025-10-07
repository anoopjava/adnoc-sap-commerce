/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { ReturnRequest } from '@spartacus/order/root';
import { Observable, Subscription } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AdnocReturnRequestService } from '../adnoc-return-request.service';

export interface adnocReturnRequest extends ReturnRequest {
  statusDisplay?: string;
  reasonDisplay?: string;
}

@Component({
  selector: 'cx-return-request-overview',
  templateUrl: './adnoc-return-request-overview.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'adnoc-return-request-overview' },
  standalone: false,
})
export class AdnocReturnRequestOverviewComponent implements OnInit, OnDestroy {
  constructor(protected returnRequestService: AdnocReturnRequestService) {
    this.returnRequest$ = this.returnRequestService
      .getReturnRequest()
      .pipe(tap((returnRequest) => (this.rma = returnRequest.rma ?? '')));

    this.isCancelling$ = this.returnRequestService.isCancelling$;
  }

  rma!: string;
  subscription!: Subscription;

  returnRequest$: Observable<adnocReturnRequest>;
  isCancelling$!: Observable<boolean>;

  ngOnInit(): void {
    this.subscription = this.returnRequestService.isCancelSuccess$.subscribe(
      (success) => {
        if (success) {
          this.returnRequestService.cancelSuccess(this.rma);
        }
      }
    );
  }

  cancelReturn(returnRequestCode: string): void {
    this.returnRequestService.cancelReturnRequest(returnRequestCode);
  }

  back(): void {
    this.returnRequestService.backToList();
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
