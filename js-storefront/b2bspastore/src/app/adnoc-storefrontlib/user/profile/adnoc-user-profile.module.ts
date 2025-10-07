/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { UserProfileCoreModule } from '@spartacus/user/profile/core';
import { UserProfileOccModule } from '@spartacus/user/profile/occ';
import { AdnocUserProfileComponentsModule } from './components/adnoc-user-profile-components.module';

@NgModule({
  imports: [
    UserProfileCoreModule,
    UserProfileOccModule,
    AdnocUserProfileComponentsModule,
  ],
})
export class AdnocUserProfileModule {}
