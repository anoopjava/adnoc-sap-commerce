/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  ViewContainerRef,
} from '@angular/core';
import { CustomerTicketingCreateComponent } from '@spartacus/customer-ticketing/components';
import { LaunchDialogService } from '@spartacus/storefront';
@Component({
    selector: 'cx-customer-ticketing-create',
    templateUrl: './adnoc-customer-ticketing-create.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'adnoc-customer-ticketing-create' },
    standalone: false
})
export class AdnocCustomerTicketingCreateComponent extends CustomerTicketingCreateComponent {
  constructor(
    protected override launchDialogService: LaunchDialogService,
    protected override vcr: ViewContainerRef
  ) {
    super(launchDialogService, vcr);
  }
}
