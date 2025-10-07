/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { OrderEntry } from '@spartacus/cart/base/root';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { AdnocOrderAmendService } from '../../adnoc-amend-order.service';

@Component({
  selector: 'adnoc-return-order',
  templateUrl: './adnoc-return-order.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class AdnocReturnOrderComponent {
  orderCode!: string;

  form$: Observable<UntypedFormGroup>;

  entries$: Observable<OrderEntry[]>;

  constructor(protected orderAmendService: AdnocOrderAmendService) {
    this.form$ = this.orderAmendService.getForm().pipe(
      tap((form) => {
        this.orderCode = form.value.orderCode;
      })
    );
    this.entries$ = this.orderAmendService.getEntries();
  }
}
