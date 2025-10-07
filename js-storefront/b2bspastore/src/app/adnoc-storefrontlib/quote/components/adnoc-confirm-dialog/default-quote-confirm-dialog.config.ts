/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { DIALOG_TYPE, LayoutConfig } from '@spartacus/storefront';
import { AdnocQuoteConfirmDialogComponent } from './adnoc-quote-confirm-dialog.component';

export const defaultQuoteActionDialogConfig: LayoutConfig = {
  launch: {
    QUOTE_ACTION_CONFIRMATION: {
      inline: true,
      component: AdnocQuoteConfirmDialogComponent,
      dialogType: DIALOG_TYPE.DIALOG,
    },
  },
};
