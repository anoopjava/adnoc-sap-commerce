/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { I18nModule, UrlModule } from '@spartacus/core';
import { KeyboardFocusModule } from '@spartacus/storefront';
import { DisableInfoModule } from '../../shared/detail/disable-info/disable-info.module';
import { ToggleStatusModule } from '../../shared/detail/toggle-status-action/toggle-status.module';
import { ItemExistsModule } from '../../shared/item-exists.module';
import { AdnocUserDetailsComponent } from './adnoc-user-details.component';
import { AdnocCardModule } from '../../shared/adnoc-card/adnoc-card.module';

@NgModule({
  imports: [
    CommonModule,
    AdnocCardModule,
    RouterModule,
    UrlModule,
    I18nModule,
    ToggleStatusModule,
    ItemExistsModule,
    DisableInfoModule,
    KeyboardFocusModule,
  ],
  declarations: [AdnocUserDetailsComponent],
  exports: [AdnocUserDetailsComponent],
})
export class AdnocUserDetailsModule {}
