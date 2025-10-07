/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { DIALOG_TYPE, LayoutConfig } from '@spartacus/storefront';
import { AdnocClearCartDialogComponent } from './adnoc-clear-cart-dialog.component';

export const defaultClearCartLayoutConfig: LayoutConfig = {
  launch: {
    CLEAR_CART: {
      inline: true,
      component: AdnocClearCartDialogComponent,
      dialogType: DIALOG_TYPE.DIALOG,
    },
  },
};
