/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  CustomerTicketingConfig,
  LIST_VIEW_PAGE_SIZE,
  MAX_INPUT_CHARACTERS,
  MAX_INPUT_CHARACTERS_FOR_SUBJECT,
  MAX_SIZE_FOR_ATTACHMENT,
} from '@spartacus/customer-ticketing/root';

export const defaultCustomerTicketingConfig: CustomerTicketingConfig = {
  customerTicketing: {
    attachmentRestrictions: {
      maxSize: MAX_SIZE_FOR_ATTACHMENT,
      allowedTypes: ['.pdf'],
    },
    inputCharactersLimit: MAX_INPUT_CHARACTERS,
    inputCharactersLimitForSubject: MAX_INPUT_CHARACTERS_FOR_SUBJECT,
    listViewPageSize: LIST_VIEW_PAGE_SIZE,
  },
};
