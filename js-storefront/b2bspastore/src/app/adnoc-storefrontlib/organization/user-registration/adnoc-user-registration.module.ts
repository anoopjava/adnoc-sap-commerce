/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { AdnocUserRegistrationComponentsModule } from './components/adnoc-user-registration-components.module';
import { UserRegistrationCoreModule } from '@spartacus/organization/user-registration/core';
import { UserRegistrationOccModule } from '@spartacus/organization/user-registration/occ';

@NgModule({
  imports: [
    UserRegistrationCoreModule.forRoot(),
    AdnocUserRegistrationComponentsModule,
    UserRegistrationOccModule,
  ],
})
export class AdnocOrganizationUserRegistrationModule {}
