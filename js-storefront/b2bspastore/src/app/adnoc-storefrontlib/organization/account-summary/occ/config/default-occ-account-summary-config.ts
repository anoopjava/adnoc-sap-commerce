/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { AccountSummaryOccEndpoints } from '@spartacus/organization/account-summary/occ';
import { AdnocOccConfig } from '../../../../../core/occ/config/adnoc-occ-config';

const adnocAccountSummaryHeaderOccEndpoints: AccountSummaryOccEndpoints = {
  accountSummary: 'users/${userId}/orgUnits/${orgUnitId}/adnocAccountSummary',
  accountSummaryDocument:
    'users/${userId}/orgUnits/${orgUnitId}/adnocOrgDocuments',
  accountSummaryDocumentAttachment:
    'users/${userId}/orgUnits/${orgUnitId}/adnocOrgDocuments/${orgDocumentId}/adnocAttachments',
};

export const defaultOccAccountSummaryConfig: AdnocOccConfig = {
  backend: {
    occ: {
      endpoints: {
        ...adnocAccountSummaryHeaderOccEndpoints,
      },
    },
  },
};
