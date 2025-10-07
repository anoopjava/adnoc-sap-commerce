/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgSelectModule } from '@ng-select/ng-select';
import { FeaturesConfigModule, I18nModule, UrlModule } from '@spartacus/core';
import {
  FormErrorsModule,
  KeyboardFocusModule,
  PasswordVisibilityToggleModule,
} from '@spartacus/storefront';
import { AdnocUserChangePasswordFormComponent } from './adnoc-user-change-password-form.component';
import { FormRequiredAsterisksComponent } from '../../../../../shared/form/form-required-asterisks';
import { FormRequiredLegendComponent } from '../../../../../shared/form/form-required-legend';
import { AdnocCardModule } from '../../shared/adnoc-card/adnoc-card.module';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    NgSelectModule,
    UrlModule,
    I18nModule,
    ReactiveFormsModule,
    FormErrorsModule,
    AdnocCardModule,
    KeyboardFocusModule,
    PasswordVisibilityToggleModule,
    FeaturesConfigModule,
    FormRequiredAsterisksComponent,
    FormRequiredLegendComponent,
  ],
  declarations: [AdnocUserChangePasswordFormComponent],
})
export class AdnocUserChangePasswordFormModule {}
