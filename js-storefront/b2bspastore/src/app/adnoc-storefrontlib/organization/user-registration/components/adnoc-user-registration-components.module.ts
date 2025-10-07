/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AdnocUserRegistrationFormModule } from './form/adnoc-user-registration-form.module';

@NgModule({
  imports: [RouterModule, AdnocUserRegistrationFormModule],
})
export class AdnocUserRegistrationComponentsModule {}
