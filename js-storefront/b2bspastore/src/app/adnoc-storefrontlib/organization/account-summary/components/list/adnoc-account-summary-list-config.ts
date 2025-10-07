/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { OrganizationTableType } from '@spartacus/organization/administration/components';
import { TableConfig } from '../../../../shared/table/config/table.config';
import { BREAKPOINT, TableLayout } from '@spartacus/storefront';
import { AuthGuard, CmsConfig } from '@spartacus/core';
import { AdnocAccountSummaryListComponent } from './adnoc-account-summary-list.component';
import {
  AdnocListService,
  ItemService,
} from '../../../administration/components/shared';
import { AccountSummaryDocumentComponent } from '@spartacus/organization/account-summary/components';
import { ACCOUNT_SUMMARY_LIST_TRANSLATION_KEY } from '@spartacus/organization/account-summary/core';
import { ROUTE_PARAMS } from '@spartacus/organization/administration/root';
import { AdnocAccountSummaryUnitListService } from '../services/adnoc-account-summary-unit-list.service';
import { AccountSummaryItemService } from '../services/account-summary-item.service';
import { AdnocAdminGuard } from '../../../administration/core/guards/adnocAdmin.guard';
import { AdnocToggleLinkCellComponent } from '../../../administration/components/unit/list/toggle-link/toggle-link-cell.component';

export const ACCOUNT_SUMMARY_DETAILS_TRANSLATION_KEY =
  'orgAccountSummaryList.breadcrumbs.details';

export function accountSummaryUnitsTableConfigFactory(): TableConfig {
  return {
    table: {
      [OrganizationTableType.ACCOUNT_SUMMARY_UNIT]: {
        cells: ['name', 'detail'],
        options: {
          layout: TableLayout.VERTICAL,
          cells: {
            name: {
              dataComponent: AdnocToggleLinkCellComponent,
            },
          },
        },
        [BREAKPOINT.lg]: {
          cells: ['name', 'detail'],
        },
      },
    },
  };
}

export const accountSummaryListCmsConfig: CmsConfig = {
  cmsComponents: {
    ManageAccountSummaryListComponent: {
      component: AdnocAccountSummaryListComponent,
      providers: [
        {
          provide: AdnocListService,
          useExisting: AdnocAccountSummaryUnitListService,
        },
        {
          provide: ItemService,
          useExisting: AccountSummaryItemService,
        },
      ],
      childRoutes: {
        parent: {
          data: {
            cxPageMeta: {
              breadcrumb: ACCOUNT_SUMMARY_LIST_TRANSLATION_KEY,
            },
          },
        },
        children: [
          {
            path: `:${ROUTE_PARAMS.unitCode}`,
            component: AccountSummaryDocumentComponent,
            data: {
              cxPageMeta: {
                breadcrumb: ACCOUNT_SUMMARY_DETAILS_TRANSLATION_KEY,
              },
            },
          },
        ],
      },
      guards: [AuthGuard, AdnocAdminGuard],
    },
  },
};
