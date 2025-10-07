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
import { AdnocFormModule } from '../../shared/adnoc-form/adnoc-form.module';
import { AdnocUserFormComponent } from './adnoc-user-form.component';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';

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
    FeaturesConfigModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
  ],
  declarations: [AdnocUserFormComponent],
  exports: [AdnocUserFormComponent],
})
export class AdnocUserFormModule {}
