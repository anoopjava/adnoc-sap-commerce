/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { OrganizationTableType } from '@spartacus/organization/administration/components';
import { AdnocSummaryUnitListService } from '../../../administration/components/unit/services/adnoc-summary-unit-list.service';

@Injectable({
  providedIn: 'root',
})
export class AdnocAccountSummaryUnitListService extends AdnocSummaryUnitListService {
  protected override tableType =
    OrganizationTableType.ACCOUNT_SUMMARY_UNIT as any;
}
