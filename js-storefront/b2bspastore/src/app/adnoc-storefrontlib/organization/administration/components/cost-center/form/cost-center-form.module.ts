/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import {
  CurrencyService,
  FeaturesConfigModule,
  I18nModule,
  UrlModule,
} from '@spartacus/core';
import { OrgUnitService } from '@spartacus/organization/administration/core';
import { FormErrorsModule } from '@spartacus/storefront';
//import { AdnocFormModule } from '../../shared/form/form.module';
import { ItemActiveModule } from '../../shared/item-active.module';
import { CostCenterFormComponent } from './cost-center-form.component';
import { AdnocFormModule } from '../../shared/adnoc-form/adnoc-form.module';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    AdnocFormModule,
    NgSelectModule,
    UrlModule,
    I18nModule,
    ReactiveFormsModule,
    FormErrorsModule,
    ItemActiveModule,
    FeaturesConfigModule,
  ],
  declarations: [CostCenterFormComponent],
  exports: [CostCenterFormComponent],
  providers: [CurrencyService, OrgUnitService],
})
export class CostCenterFormModule {}
