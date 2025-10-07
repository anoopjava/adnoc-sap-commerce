import { CommonModule } from '@angular/common';
import {
  APP_INITIALIZER,
  CUSTOM_ELEMENTS_SCHEMA,
  NgModule,
} from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import {
  CmsConfig,
  ConfigModule,
  FeaturesConfigModule,
  I18nModule,
  NotAuthGuard,
  provideDefaultConfig,
  UrlModule,
} from '@spartacus/core';
import {
  FormErrorsModule,
  IconModule,
  KeyboardFocusModule,
  PasswordVisibilityToggleModule,
  SpinnerModule,
} from '@spartacus/storefront';
import { AdnocOtploginComponent } from './adnoc-otp-login.component';
import { AdnocOtploginComponentService } from './adnoc-otp-login-service';
import { defaultSuggestedPayersDialogLayoutConfig } from '../adnoc-suggested-payers-dialog/default-suggested-payers-dialog-layout.config';
import { AdnocSuggestedPayersDialogComponent } from '../adnoc-suggested-payers-dialog/adnoc-suggested-payers-dialog.component';
import { CdcConfigService } from '../../cdc/cdc-config.service';
import { CdcScriptLoaderService } from '../../cdc/cdc-script-loader.service';

export function initCdc(
  configService: CdcConfigService,
  scriptLoader: CdcScriptLoaderService
) {
  return () =>
    configService.load().then(() => {
      scriptLoader.loadCdcScript();
    });
}

@NgModule({
  declarations: [AdnocOtploginComponent, AdnocSuggestedPayersDialogComponent],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    KeyboardFocusModule,
    RouterModule,
    UrlModule,
    I18nModule,
    IconModule,
    FormErrorsModule,
    SpinnerModule,
    PasswordVisibilityToggleModule,
    FeaturesConfigModule,
    ConfigModule.withConfig({
      cmsComponents: {
        ReturningCustomerOTPLoginComponent: {
          component: AdnocOtploginComponent,
          guards: [NotAuthGuard],
        },
        VerifyOTPTokenComponent: {
          component: '',
          guards: [NotAuthGuard],
        },
      },
    } as CmsConfig),
  ],
  providers: [
    AdnocOtploginComponentService,
    provideDefaultConfig(defaultSuggestedPayersDialogLayoutConfig),
    {
      provide: APP_INITIALIZER,
      useFactory: initCdc,
      deps: [CdcConfigService, CdcScriptLoaderService],
      multi: true,
    },
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
})
export class AdnocOtploginModule {}
