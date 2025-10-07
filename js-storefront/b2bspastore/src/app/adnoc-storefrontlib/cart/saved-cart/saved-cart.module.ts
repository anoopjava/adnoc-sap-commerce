/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { SavedCartCoreModule } from '@spartacus/cart/saved-cart/core';
import { SavedCartOccModule } from '@spartacus/cart/saved-cart/occ';
import { AdnocSavedCartComponentsModule } from './components/adnoc-saved-cart-components.module';

@NgModule({
  imports: [
    SavedCartCoreModule,
    SavedCartOccModule,
    AdnocSavedCartComponentsModule,
  ],
})
export class SavedCartModule {}
