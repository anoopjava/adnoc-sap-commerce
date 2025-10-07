/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { inject, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdnocUpdateEmailComponent } from './adnoc-update-email.component';
import {
  MyAccountV2EmailComponent,
  UpdateEmailComponentService,
  USE_MY_ACCOUNT_V2_EMAIL,
} from '@spartacus/user/profile/components';
import {
  AuthGuard,
  AuthRedirectService,
  CmsConfig,
  FeaturesConfigModule,
  I18nModule,
  provideDefaultConfig,
  provideDefaultConfigFactory,
  RoutingService,
  UrlModule,
} from '@spartacus/core';
import { UserEmailFacade } from '@spartacus/user/profile/root';
import { ReactiveFormsModule } from '@angular/forms';
import { FormErrorsModule, SpinnerModule } from '@spartacus/storefront';
import { RouterModule } from '@angular/router';
import { AdnocAuthService } from '../../../../../core/src/auth/user-auth/facade/adnoc-auth.service';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

const myAccountV2EmailMapping: CmsConfig = {
  cmsComponents: {
    UpdateEmailComponent: {
      component: MyAccountV2EmailComponent,
    },
  },
};

@NgModule({
  declarations: [AdnocUpdateEmailComponent],
  imports: [
    CommonModule,
    I18nModule,
    UrlModule,
    FormErrorsModule,
    ReactiveFormsModule,
    SpinnerModule,
    RouterModule,
    FeaturesConfigModule,
  ],
  exports: [AdnocUpdateEmailComponent],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        UpdateEmailComponent: {
          component: AdnocUpdateEmailComponent,
          guards: [AuthGuard],
          providers: [
            {
              provide: UpdateEmailComponentService,
              useClass: UpdateEmailComponentService,
              deps: [
                UserEmailFacade,
                RoutingService,
                AdnocGlobalMessageService,
                AdnocAuthService,
                AuthRedirectService,
              ],
            },
          ],
        },
      },
    }),
    provideDefaultConfigFactory(() =>
      inject(USE_MY_ACCOUNT_V2_EMAIL) ? myAccountV2EmailMapping : {}
    ),
  ],
})
export class AdnocUpdateEmailModule {}
