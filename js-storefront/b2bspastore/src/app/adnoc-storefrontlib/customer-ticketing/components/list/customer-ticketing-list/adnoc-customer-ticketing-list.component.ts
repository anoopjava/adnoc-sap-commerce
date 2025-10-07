/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RoutingService, TranslationService } from '@spartacus/core';
import { CustomerTicketingListComponent } from '@spartacus/customer-ticketing/components';
import {
  CustomerTicketingConfig,
  CustomerTicketingFacade,
} from '@spartacus/customer-ticketing/root';

@Component({
  selector: 'cx-customer-ticketing-list',
  templateUrl: './adnoc-customer-ticketing-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'adnoc-customer-ticketing-list' },
  standalone: false,
})
export class AdnocCustomerTicketingListComponent extends CustomerTicketingListComponent {
  ticketList = {
    pagination: {
      totalPages: 0,
    },
  };

  constructor(
    protected override customerTicketingFacade: CustomerTicketingFacade,
    protected override routingService: RoutingService,
    protected override translationService: TranslationService,
    protected override customerTicketingConfig: CustomerTicketingConfig
  ) {
    super(
      customerTicketingFacade,
      routingService,
      translationService,
      customerTicketingConfig
    );
  }

  castToAny(ticket: any): any {
    return ticket;
  }
}
