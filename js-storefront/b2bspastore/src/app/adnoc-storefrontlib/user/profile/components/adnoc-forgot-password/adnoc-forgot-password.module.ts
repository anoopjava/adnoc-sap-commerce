/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  AuthConfigService,
  CmsConfig,
  FeaturesConfigModule,
  I18nModule,
  NotAuthGuard,
  provideDefaultConfig,
  RoutingService,
  UrlModule,
} from '@spartacus/core';
import { FormErrorsModule, SpinnerModule } from '@spartacus/storefront';
import { UserPasswordFacade } from '@spartacus/user/profile/root';
import { AdnocForgotPasswordComponentService } from './adnoc-forgot-password-component.service';
import { AdnocForgotPasswordComponent } from './adnoc-forgot-password.component';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';
import { CdcAuthService } from '../../../../cdc/cdc-auth.service';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    UrlModule,
    I18nModule,
    FormErrorsModule,
    SpinnerModule,
    FeaturesConfigModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        ForgotPasswordComponent: {
          component: AdnocForgotPasswordComponent,
          guards: [NotAuthGuard],
          providers: [
            {
              provide: AdnocForgotPasswordComponentService,
              useClass: AdnocForgotPasswordComponentService,
              deps: [
                UserPasswordFacade,
                RoutingService,
                AuthConfigService,
                AdnocGlobalMessageService,
                CdcAuthService
              ],
            },
          ],
        },
      },
    }),
  ],
  declarations: [AdnocForgotPasswordComponent],
})
export class AdnocForgotPasswordModule {}
