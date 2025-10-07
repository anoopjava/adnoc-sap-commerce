/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Observable } from 'rxjs';
import { AdnocReturnRequestService } from '../adnoc-return-request.service';
import { ReturnRequest } from '../../../root/model/adnoc-order.model';

@Component({
  selector: 'cx-return-request-items',
  templateUrl: './adnoc-return-request-items.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'adnoc-return-request-items' },
  standalone: false,
})
export class AdnocReturnRequestItemsComponent {
  constructor(protected returnRequestService: AdnocReturnRequestService) {
    this.returnRequest$ = this.returnRequestService.getReturnRequest();
  }

  returnRequest$: Observable<ReturnRequest>;
}
