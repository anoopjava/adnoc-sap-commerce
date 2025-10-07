/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  FeaturesConfigModule,
  I18nModule,
  provideDefaultConfig,
  UrlModule,
} from '@spartacus/core';
import {
  BtnLikeLinkModule,
  IconModule,
  ItemCounterModule,
  KeyboardFocusModule,
  PromotionsModule,
  SpinnerModule,
} from '@spartacus/storefront';
import { AdnocAddedToCartDialogEventListener } from './adnoc-added-to-cart-dialog-event.listener';
import { AdnocAddedToCartDialogComponent } from './adnoc-added-to-cart-dialog.component';
import { defaultAddedToCartLayoutConfig } from './default-added-to-cart-layout.config';
import { AdnocCartSharedModule } from '../cart-shared/adnoc-cart-shared.module';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    AdnocCartSharedModule,
    RouterModule,
    SpinnerModule,
    PromotionsModule,
    UrlModule,
    IconModule,
    I18nModule,
    ItemCounterModule,
    KeyboardFocusModule,
    FeaturesConfigModule,
    BtnLikeLinkModule,
  ],
  providers: [provideDefaultConfig(defaultAddedToCartLayoutConfig)],
  declarations: [AdnocAddedToCartDialogComponent],
  exports: [AdnocAddedToCartDialogComponent],
})
export class AdnocAddedToCartDialogModule {
  constructor(_addToCartDialogEventListener: AdnocAddedToCartDialogEventListener) {
    // Intentional empty constructor
  }
}
