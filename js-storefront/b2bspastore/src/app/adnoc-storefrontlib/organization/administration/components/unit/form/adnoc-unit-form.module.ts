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
import { FeaturesConfigModule, I18nModule, UrlModule } from '@spartacus/core';
import { FormErrorsModule } from '@spartacus/storefront';
import { NgSelectA11yModule } from '@spartacus/storefront';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { provideNativeDateAdapter } from '@angular/material/core';
import { ItemActiveModule } from '../../shared/item-active.module';
import { AdnocFormModule } from '../../shared/adnoc-form/adnoc-form.module';
import { AdnocUnitFormComponent } from './adnoc-unit-form.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    NgSelectModule,
    UrlModule,
    I18nModule,
    ReactiveFormsModule,
    FormErrorsModule,
    ItemActiveModule,
    FeaturesConfigModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    AdnocFormModule,
    NgSelectA11yModule
  ],
  providers: [provideNativeDateAdapter()],
  declarations: [AdnocUnitFormComponent],
  exports: [AdnocUnitFormComponent],
})
export class AdnocUnitFormModule {}
