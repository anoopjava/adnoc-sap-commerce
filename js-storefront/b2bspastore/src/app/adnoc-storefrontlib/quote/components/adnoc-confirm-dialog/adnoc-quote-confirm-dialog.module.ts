/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  FeaturesConfigModule,
  I18nModule,
  provideDefaultConfig,
  UrlModule,
} from '@spartacus/core';
import {
  FormErrorsModule,
  IconModule,
  KeyboardFocusModule,
  SpinnerModule,
} from '@spartacus/storefront';
import { defaultQuoteActionDialogConfig } from './default-quote-confirm-dialog.config';
import { AdnocQuoteConfirmDialogComponent } from './adnoc-quote-confirm-dialog.component';

@NgModule({
  imports: [
    CommonModule,
    I18nModule,
    UrlModule,
    IconModule,
    FormsModule,
    ReactiveFormsModule,
    FormErrorsModule,
    RouterModule,
    KeyboardFocusModule,
    SpinnerModule,
    FeaturesConfigModule,
  ],
  providers: [provideDefaultConfig(defaultQuoteActionDialogConfig)],
  declarations: [AdnocQuoteConfirmDialogComponent],
  exports: [AdnocQuoteConfirmDialogComponent],
})
export class AdnocQuoteConfirmDialogModule {}
