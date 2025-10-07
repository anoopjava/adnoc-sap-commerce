/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {
  CmsConfig,
  I18nModule,
  provideDefaultConfig,
  UrlModule,
} from '@spartacus/core';
import { AdnocClearCartComponent } from './adnoc-clear-cart.component';
import { ClearCartDialogModule } from '../adnoc-clear-cart-dialog/adnoc-clear-cart-dialog.module';
import { defaultClearCartLayoutConfig } from '../adnoc-clear-cart-dialog/default-clear-cart-layout.config';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [AdnocClearCartComponent],
  exports: [AdnocClearCartComponent],
  imports: [
    CommonModule,
    I18nModule,
    RouterModule,
    UrlModule,
    ClearCartDialogModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        ClearCartComponent: {
          component: AdnocClearCartComponent,
        },
      },
    }),
    provideDefaultConfig(defaultClearCartLayoutConfig),
  ],
})
export class AdnocClearCartModule {}
