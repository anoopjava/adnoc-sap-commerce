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
  selector: 'cx-return-order-confirmation',
  templateUrl: './adnoc-return-order-confirmation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class AdnocReturnOrderConfirmationComponent {
  orderCode!: string;

  form$: Observable<any>;

  entries$: Observable<OrderEntry[]>;

  constructor(protected orderAmendService: AdnocOrderAmendService) {
    this.form$ = this.orderAmendService
      .getForm()
      .pipe(tap((form) => (this.orderCode = form.value.orderCode)));

    this.entries$ = this.orderAmendService.getAmendedEntries();
  }

  submit(form: UntypedFormGroup): void {
    form.disable();
    this.orderAmendService.save();
  }
}
