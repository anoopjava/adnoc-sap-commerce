/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  defaultSavedCartFormLayoutConfig,
  NewSavedCartOrderEntriesContext,
  SavedCartFormDialogModule,
  SavedCartOrderEntriesContext,
} from '@spartacus/cart/saved-cart/components';
import {
  NewSavedCartOrderEntriesContextToken,
  SavedCartOrderEntriesContextToken,
} from '@spartacus/cart/saved-cart/root';
import { provideDefaultConfig } from '@spartacus/core';
import { AdnocSavedCartDetailsModule } from './details/adnoc-saved-cart-details.module';
import { AdnocSavedCartListModule } from './list/adnoc-saved-cart-list.module';
import { AddToSavedCartModule } from './adnoc-add-to-saved-cart/adnoc-add-to-saved-cart.module';

@NgModule({
  imports: [
    RouterModule,
    AddToSavedCartModule,
    SavedCartFormDialogModule,
    AdnocSavedCartListModule,
    AdnocSavedCartDetailsModule,
  ],
  providers: [
    {
      provide: SavedCartOrderEntriesContextToken,
      useExisting: SavedCartOrderEntriesContext,
    },
    {
      provide: NewSavedCartOrderEntriesContextToken,
      useExisting: NewSavedCartOrderEntriesContext,
    },
    provideDefaultConfig(defaultSavedCartFormLayoutConfig),
  ],
})
export class AdnocSavedCartComponentsModule {}
