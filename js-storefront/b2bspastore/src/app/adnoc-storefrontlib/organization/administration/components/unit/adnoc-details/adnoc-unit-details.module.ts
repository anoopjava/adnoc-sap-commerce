/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { I18nModule, UrlModule } from '@spartacus/core';
import { KeyboardFocusModule } from '@spartacus/storefront';
import { ItemExistsModule } from '../../shared/item-exists.module';
import { AdnocUnitDetailsComponent } from './adnoc-unit-details.component';
import { AdnocCardModule } from '../../shared/adnoc-card/adnoc-card.module';
//import { ToggleStatusModule } from '@spartacus/organization/administration/components';
import { DisableInfoModule } from '../../shared/detail/disable-info/disable-info.module';
import { ToggleStatusModule } from '../../shared/detail/toggle-status-action/toggle-status.module';

@NgModule({
  imports: [
    CommonModule,
    AdnocCardModule,
    RouterModule,
    UrlModule,
    I18nModule,
    ToggleStatusModule,
    ItemExistsModule,
    KeyboardFocusModule,
    DisableInfoModule
  ],
  declarations: [AdnocUnitDetailsComponent],
  exports: [AdnocUnitDetailsComponent],
})
export class AdnocUnitDetailsModule {}
