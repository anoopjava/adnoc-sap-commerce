/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { DIALOG_TYPE, LAUNCH_CALLER, LayoutConfig } from '@spartacus/storefront';
import { AdnocSuggestedPayersDialogComponent } from './adnoc-suggested-payers-dialog.component';

export const defaultSuggestedPayersDialogLayoutConfig: LayoutConfig = {
  launch: {
    SUGGESTED_PAYERS: {
      inlineRoot: true,
      component: AdnocSuggestedPayersDialogComponent,
      dialogType: DIALOG_TYPE.DIALOG,
    },
  },
};
