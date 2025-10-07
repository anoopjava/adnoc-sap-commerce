/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { AdnocTableDataCellComponent } from '../table-data-cell/table-data-cell.component';
import { AdnocTableHeaderCellComponent } from '../table-header-cell/table-header-cell.component';
import { TableConfig } from './table.config';

export const defaultTableConfig: TableConfig = {
  tableOptions: {
    headerComponent: AdnocTableHeaderCellComponent,
    dataComponent: AdnocTableDataCellComponent,
  },
};
