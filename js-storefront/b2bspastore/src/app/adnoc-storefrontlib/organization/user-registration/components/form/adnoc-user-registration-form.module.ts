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
  CmsConfig,
  ConfigModule,
  FeaturesConfigModule,
  I18nModule,
  NotAuthGuard,
  UrlModule,
} from '@spartacus/core';
import {
  FormErrorsModule,
  NgSelectA11yModule,
  SpinnerModule,
} from '@spartacus/storefront';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { provideNativeDateAdapter } from '@angular/material/core';
import { AdnocUserRegistrationFormComponent } from './adnoc-user-registration-form.component';
import { AdnocUserRegistrationFormService } from './adnoc-user-registration-form.service';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    UrlModule,
    I18nModule,
    SpinnerModule,
    FormErrorsModule,
    NgSelectModule,
    NgSelectA11yModule,
    ConfigModule.withConfig(<CmsConfig>{
      cmsComponents: {
        OrganizationUserRegistrationComponent: {
          component: AdnocUserRegistrationFormComponent,
          guards: [NotAuthGuard],
        },
      },
    }),
    FeaturesConfigModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
  ],
  declarations: [AdnocUserRegistrationFormComponent],
  exports: [AdnocUserRegistrationFormComponent],
  providers: [AdnocUserRegistrationFormService, provideNativeDateAdapter()],
})
export class AdnocUserRegistrationFormModule {}
