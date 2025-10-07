/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import {
  provideDefaultConfig,
  provideDefaultConfigFactory,
} from '@spartacus/core';
import {
  adnocUserCmsConfig,
  userTableConfigFactory,
} from './adnoc-user.config';
import { AdnocListModule } from '../shared/list/adnoc-list.module';
import {
  UserApproverListModule,
  UserPermissionListModule,
  UserUserGroupsModule,
} from '@spartacus/organization/administration/components';
import { AdnocUserFormModule } from './form';
import { AdnocUserChangePasswordFormModule } from './adnoc-change-password-form/adnoc-user-change-password-form.module';
import { AdnocUserDetailsModule } from './details/adnoc-user-details.module';
@NgModule({
  imports: [
    AdnocListModule,
    AdnocUserChangePasswordFormModule,
    AdnocUserDetailsModule,
    AdnocUserFormModule,
    UserPermissionListModule,
    UserUserGroupsModule,
    UserApproverListModule,
  ],
  providers: [
    provideDefaultConfig(adnocUserCmsConfig),
    provideDefaultConfigFactory(userTableConfigFactory),
  ],
})
export class AdnocUserComponentsModule {}
