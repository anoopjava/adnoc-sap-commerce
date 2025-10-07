/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule, inject } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  AuthGuard,
  AuthRedirectService,
  CmsConfig,
  FeaturesConfigModule,
  I18nModule,
  RoutingService,
  UrlModule,
  provideDefaultConfig,
  provideDefaultConfigFactory,
} from '@spartacus/core';
import {
  BtnLikeLinkModule,
  FormErrorsModule,
  FormRequiredAsterisksComponent,
  FormRequiredLegendComponent,
  MessageComponentModule,
  PasswordVisibilityToggleModule,
  SpinnerModule,
} from '@spartacus/storefront';
import { UserPasswordFacade } from '@spartacus/user/profile/root';
import {
  UpdatePasswordComponentService,
  USE_MY_ACCOUNT_V2_PASSWORD,
} from '@spartacus/user/profile/components';
import { AdnocMyAccountV2PasswordComponent } from './adnoc-my-account-v2-password.component';
import { AdnocUpdatePasswordComponent } from './adnoc-update-password.component';
import { AdnocAuthService } from '../../../../../core/src/auth/user-auth/facade/adnoc-auth.service';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

const adnocMyAccountV2PasswordMapping: CmsConfig = {
  cmsComponents: {
    UpdatePasswordComponent: {
      component: AdnocMyAccountV2PasswordComponent,
    },
  },
};

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    SpinnerModule,
    I18nModule,
    FormErrorsModule,
    UrlModule,
    RouterModule,
    PasswordVisibilityToggleModule,
    FeaturesConfigModule,
    MessageComponentModule,
    BtnLikeLinkModule,
    FormRequiredAsterisksComponent,
    FormRequiredLegendComponent,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        UpdatePasswordComponent: {
          component: AdnocUpdatePasswordComponent,
          guards: [AuthGuard],
          providers: [
            {
              provide: UpdatePasswordComponentService,
              useClass: UpdatePasswordComponentService,
              deps: [
                UserPasswordFacade,
                RoutingService,
                AdnocGlobalMessageService,
                AuthRedirectService,
                AdnocAuthService,
              ],
            },
          ],
        },
      },
    }),
    provideDefaultConfigFactory(() =>
      inject(USE_MY_ACCOUNT_V2_PASSWORD) ? adnocMyAccountV2PasswordMapping : {}
    ),
  ],
  declarations: [
    AdnocUpdatePasswordComponent,
    AdnocMyAccountV2PasswordComponent,
  ],
  exports: [AdnocUpdatePasswordComponent, AdnocMyAccountV2PasswordComponent],
})
export class AdnocUpdatePasswordModule {}
