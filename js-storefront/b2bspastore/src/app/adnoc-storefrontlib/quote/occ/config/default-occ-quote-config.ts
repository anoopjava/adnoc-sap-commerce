/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { OccConfig } from '@spartacus/core';
import { AdnocOccConfig } from '../../../../core/occ/config/adnoc-occ-config';

const PRICE_HEADER_FIELDS =
  ',totalPrice(formattedValue),quoteDiscounts(formattedValue),orderDiscounts(formattedValue),productDiscounts(formattedValue)';

export const defaultOccQuoteConfig: AdnocOccConfig = {
  backend: {
    occ: {
      endpoints: {
        getQuotes: 'users/${userId}/quotes',
        createQuote: 'users/${userId}/quotes',
        getQuote:
          'users/${userId}/quotes/${quoteCode}?fields=FULL,expirationTime' +
          PRICE_HEADER_FIELDS +
          ',entries(FULL)',
        editQuote: 'users/${userId}/quotes/${quoteCode}',
        performQuoteAction: 'orgUsers/${userId}/quotes/${quoteCode}/adnocAction',
        addComment: 'users/${userId}/quotes/${quoteCode}/comments',
        addDiscount: 'users/${userId}/quotes/${quoteCode}/discounts',
        addQuoteEntryComment:
          'users/${userId}/quotes/${quoteCode}/entries/${entryNumber}/comments',
        downloadAttachment:
          'users/${userId}/quotes/${quoteCode}/attachments/${attachmentId}',
          cartToQuotes:
          '/orgUsers/current/carts/${cartId}/adnocQuotes'
      },
    },
  },
};
