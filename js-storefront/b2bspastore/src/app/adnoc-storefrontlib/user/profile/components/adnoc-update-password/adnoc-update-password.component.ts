/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  ViewEncapsulation,
} from '@angular/core';
import {
  GlobalMessageType,
  HttpErrorModel,
  useFeatureStyles,
} from '@spartacus/core';
import {
  UpdatePasswordComponent,
  UpdatePasswordComponentService,
} from '@spartacus/user/profile/components';
import { CdcAuthService } from '../../../../cdc/cdc-auth.service';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

@Component({
  selector: 'adnoc-update-password',
  templateUrl: './adnoc-update-password.component.html',
  styleUrl: './adnoc-update-password.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  host: { class: 'user-form' },
  standalone: false,
})
export class AdnocUpdatePasswordComponent extends UpdatePasswordComponent {
  constructor(
    protected override service: UpdatePasswordComponentService,
    protected cdcAuthService: CdcAuthService,
    protected globalMessageService: AdnocGlobalMessageService
  ) {
    super(service);
    useFeatureStyles('a11yPasswordVisibliltyBtnValueOverflow');
    this.form = this.service.form;
    this.isUpdating$ = this.service.isUpdating$;
  }

  async onPasswordResetSubmit(): Promise<void> {
    if (!this.form.valid) {
      this.form.markAllAsTouched();
      return;
    }

    const oldPassword = this.form.get('oldPassword')?.value;
    const newPassword = this.form.get('newPassword')?.value;

    try {
      const response = await this.cdcAuthService.updatePassword(
        newPassword,
        oldPassword
      );
      if (response?.errorCode === 0) {
        this.globalMessageService.add(
          {
            key: this.service['usingV2']
              ? 'myAccountV2PasswordForm.passwordUpdateSuccess'
              : 'updatePasswordForm.passwordUpdateSuccess',
          },
          GlobalMessageType.MSG_TYPE_CONFIRMATION
        );
        this.service['busy$'].next(false);
        this.service['authRedirectService']?.setRedirectUrl(
          this.service['routingService'].getUrl({ cxRoute: 'home' })
        );
        this.service['authService']?.coreLogout().then(() => {
          this.service['routingService'].go({ cxRoute: 'login' });
        });
      } else {
        this.showGlobalError(response);
      }
      this.form.reset();
    } catch (error) {
      this.showGlobalError(error);
      this.service['busy$'].next(false);
      this.form.reset();
    }
  }

  showGlobalError(error: any): void {
    this.service['busy$'].next(false);
    let message = 'Something went wrong.';
    if (error?.errorDetails || error?.errorMessage) {
      message = error.errorDetails || error.errorMessage;
    }
    this.globalMessageService.add(message, GlobalMessageType.MSG_TYPE_ERROR);
  }
}
