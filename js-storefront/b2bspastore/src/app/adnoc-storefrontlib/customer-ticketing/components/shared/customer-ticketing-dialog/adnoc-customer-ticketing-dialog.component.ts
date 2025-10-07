/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Directive, ElementRef } from '@angular/core';
import { RoutingService } from '@spartacus/core';
import { CustomerTicketingDialogComponent } from '@spartacus/customer-ticketing/components';
import {
  CustomerTicketingConfig,
  CustomerTicketingFacade,
} from '@spartacus/customer-ticketing/root';
import {
  FilesFormValidators,
  LaunchDialogService,
} from '@spartacus/storefront';

@Directive()
export class AdnocCustomerTicketingDialogComponent extends CustomerTicketingDialogComponent {
  constructor(
    protected override launchDialogService: LaunchDialogService,
    protected override el: ElementRef,
    protected override customerTicketingConfig: CustomerTicketingConfig,
    protected override filesFormValidators: FilesFormValidators,
    protected override customerTicketingFacade: CustomerTicketingFacade,
    protected override routingService: RoutingService
  ) {
    super(
      launchDialogService,
      el,
      customerTicketingConfig,
      filesFormValidators,
      customerTicketingFacade,
      routingService
    );
  }
}
