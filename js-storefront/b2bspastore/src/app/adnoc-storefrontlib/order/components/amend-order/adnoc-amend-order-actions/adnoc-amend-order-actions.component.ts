/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  HostBinding,
  Input,
} from '@angular/core';
import { RoutingService } from '@spartacus/core';
import { UntypedFormGroup } from '@angular/forms';
import { AdnocOrderAmendService } from '../adnoc-amend-order.service';

@Component({
    selector: 'adnoc-amend-order-actions',
    templateUrl: './adnoc-amend-order-actions.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class AdnocAmendOrderActionsComponent {
  @Input() orderCode!: string;
  @Input() amendOrderForm!: UntypedFormGroup;
  @Input() backRoute!: string;
  @Input() forwardRoute!: string;

  @HostBinding('class') styles = 'row';

  constructor(
    protected routingService: RoutingService,
    protected orderAmendService: AdnocOrderAmendService
  ) {}

  continue(event: Event): void {
    if (this.amendOrderForm.valid) {
      this.routingService.go({
        cxRoute: this.forwardRoute,
        params: { code: this.orderCode },
      });
    } else {
      this.amendOrderForm.markAllAsTouched();
      event.stopPropagation();
    }
  }
}
