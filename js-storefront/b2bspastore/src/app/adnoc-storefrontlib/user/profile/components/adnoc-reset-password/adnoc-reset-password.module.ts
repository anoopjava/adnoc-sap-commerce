/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  CmsConfig,
  FeaturesConfigModule,
  I18nModule,
  NotAuthGuard,
  provideDefaultConfig,
  RoutingService,
} from '@spartacus/core';
import {
  FormErrorsModule,
  FormRequiredAsterisksComponent,
  FormRequiredLegendComponent,
  PasswordVisibilityToggleModule,
  SpinnerModule,
} from '@spartacus/storefront';
import { UserPasswordFacade } from '@spartacus/user/profile/root';
import { AdnocResetPasswordComponentService } from './adnoc-reset-password-component.service';
import { AdnocResetPasswordComponent } from './adnoc-reset-password.component';
import { CdcAuthService } from '../../../../cdc/cdc-auth.service';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule,
    I18nModule,
    FormErrorsModule,
    SpinnerModule,
    PasswordVisibilityToggleModule,
    FeaturesConfigModule,
    FormRequiredAsterisksComponent,
    FormRequiredLegendComponent,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        ResetPasswordComponent: {
          component: AdnocResetPasswordComponent,
          guards: [NotAuthGuard],
          providers: [
            {
              provide: AdnocResetPasswordComponentService,
              useClass: AdnocResetPasswordComponentService,
              deps: [
                UserPasswordFacade,
                RoutingService,
                AdnocGlobalMessageService,
                CdcAuthService,
              ],
            },
          ],
        },
      },
    }),
  ],
  declarations: [AdnocResetPasswordComponent],
})
export class AdnocResetPasswordModule {}
