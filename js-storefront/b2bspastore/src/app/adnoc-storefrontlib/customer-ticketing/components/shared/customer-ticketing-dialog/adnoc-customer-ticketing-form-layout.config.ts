/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  CustomerTicketingCloseDialogComponent,
  CustomerTicketingReopenDialogComponent,
} from '@spartacus/customer-ticketing/components';
import { DIALOG_TYPE, LayoutConfig } from '@spartacus/storefront';
import { AdnocCustomerTicketingCreateDialogComponent } from '../../list/customer-ticketing-create/customer-ticketing-create-dialog/adnoc-customer-ticketing-create-dialog.component';

export const AdnocCustomerTicketingFormLayoutConfig: LayoutConfig = {
  launch: {
    CUSTOMER_TICKETING_REOPEN: {
      inline: true,
      component: CustomerTicketingReopenDialogComponent,
      dialogType: DIALOG_TYPE.DIALOG,
    },

    CUSTOMER_TICKETING_CLOSE: {
      inline: true,
      component: CustomerTicketingCloseDialogComponent,
      dialogType: DIALOG_TYPE.DIALOG,
    },

    CUSTOMER_TICKETING_CREATE: {
      inline: true,
      component: AdnocCustomerTicketingCreateDialogComponent,
      dialogType: DIALOG_TYPE.DIALOG,
    },
  },
};
