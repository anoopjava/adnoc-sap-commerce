/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { inject, NgModule } from '@angular/core';
import { AdnocUpdateProfileComponent } from './adnoc-update-profile.component';
import {
  AuthGuard,
  CmsConfig,
  FeaturesConfigModule,
  I18nModule,
  provideDefaultConfig,
  provideDefaultConfigFactory,
  UrlModule,
} from '@spartacus/core';
import {
  MyAccountV2ProfileComponent,
  UpdateProfileComponentService,
  USE_MY_ACCOUNT_V2_PROFILE,
} from '@spartacus/user/profile/components';
import { UserProfileFacade } from '@spartacus/user/profile/root';
import { NgSelectModule } from '@ng-select/ng-select';
import {
  FormErrorsModule,
  NgSelectA11yModule,
  SpinnerModule,
} from '@spartacus/storefront';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

const myAccountV2ProfileMapping: CmsConfig = {
  cmsComponents: {
    UpdateProfileComponent: {
      component: MyAccountV2ProfileComponent,
    },
  },
};

@NgModule({
  declarations: [AdnocUpdateProfileComponent],
  imports: [
    CommonModule,
    I18nModule,
    NgSelectModule,
    NgSelectA11yModule,
    SpinnerModule,
    FormErrorsModule,
    UrlModule,
    RouterModule,
    ReactiveFormsModule,
    FeaturesConfigModule,
  ],
  exports: [AdnocUpdateProfileComponent],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        UpdateProfileComponent: {
          component: AdnocUpdateProfileComponent,
          guards: [AuthGuard],
          providers: [
            {
              provide: UpdateProfileComponentService,
              useClass: UpdateProfileComponentService,
              deps: [UserProfileFacade, AdnocGlobalMessageService],
            },
          ],
        },
      },
    }),
    provideDefaultConfigFactory(() =>
      inject(USE_MY_ACCOUNT_V2_PROFILE) ? myAccountV2ProfileMapping : {}
    ),
  ],
})
export class AdnocUpdateProfileModule {}
