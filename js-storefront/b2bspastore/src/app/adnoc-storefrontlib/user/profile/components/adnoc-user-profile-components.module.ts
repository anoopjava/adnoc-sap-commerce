/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import {
  AddressBookModule,
  CloseAccountModule,
  RegisterComponentModule,
} from '@spartacus/user/profile/components';
import { AdnocUpdateProfileModule } from './adnoc-update-profile/adnoc-update-profile.module';
import { AdnocForgotPasswordModule } from './adnoc-forgot-password/adnoc-forgot-password.module';
import { AdnocUpdateEmailModule } from './adnoc-update-email';
import { AdnocUpdatePasswordModule } from './adnoc-update-password/adnoc-update-password.module';
import { AdnocResetPasswordModule } from './adnoc-reset-password/adnoc-reset-password.module';

@NgModule({
  imports: [
    RegisterComponentModule,
    AdnocUpdateProfileModule,
    AdnocUpdateEmailModule,
    AdnocUpdatePasswordModule,
    AdnocForgotPasswordModule,
    AdnocResetPasswordModule,
    CloseAccountModule,
    AddressBookModule,
  ],
})
export class AdnocUserProfileComponentsModule {}
