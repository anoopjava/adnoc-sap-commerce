/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { OccEndpoints } from '@spartacus/core';

export interface AdnocOccEndpoints extends OccEndpoints {
  adnocConfig?: string;
  getPayerList?: string;
  getAddressForEntries?: string;
  updateforcheckout?: string;
  getPayersList?: string;
  createPayer?: string;
  b2bUnitCreate?: string;
  statementOfAccount?: string;
  returnReason?: string;
  cartToQuotes?: string;
  incoTerms?: string;
  pickupAddress?: string;
  getPayerOverdueInvoiceList?: string;
  getInvoicePaymentType?: string;
  getInvoicePaymentSessionId?: string;
  getRetrieveOverdueInvoicePayment?: string;
  cancelReason?: string;
  cancelConfirmation?: string;
  productAttachments?: string;
  b2bUnitLinkUpdate?: string;
  getFinalizeOverdueInvoicePayment?: string;
}
