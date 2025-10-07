/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component, OnDestroy } from '@angular/core';
import { Observable } from 'rxjs';
import { AdnocReturnRequestService } from '../adnoc-return-request.service';
import { ReturnRequest } from '../../../root/model/adnoc-order.model';

@Component({
  selector: 'cx-return-request-totals',
  templateUrl: './adnoc-return-request-totals.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class AdnocReturnRequestTotalsComponent implements OnDestroy {
  constructor(protected returnRequestService: AdnocReturnRequestService) {
    this.returnRequest$ = this.returnRequestService.getReturnRequest();
  }

  returnRequest$: Observable<ReturnRequest>;

  ngOnDestroy() {
    this.returnRequestService.clearReturnRequest();
  }
}
