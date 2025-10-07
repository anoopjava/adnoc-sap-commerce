/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdnocLoginModule } from './adnoc-login/adnoc-login.module';
import { AdnocOtploginModule } from './adnoc-otp-login/adnoc-otp-login.module';
import { AdnocFooterModule } from './adnoc-footer/adnoc-footer.module';
import { AdnocUserRegistrationSuccessModule } from './adnoc-user-registration-success/adnoc-user-registration-success.module';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    AdnocLoginModule,
    AdnocUserRegistrationSuccessModule,
    AdnocOtploginModule,
    AdnocFooterModule,
  ],
})
export class AdnocComponentModule {}
