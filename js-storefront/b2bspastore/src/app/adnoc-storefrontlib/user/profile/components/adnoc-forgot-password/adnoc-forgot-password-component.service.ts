/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { ForgotPasswordComponentService } from '@spartacus/user/profile/components';
import { CdcAuthService } from '../../../../cdc/cdc-auth.service';
import { UserPasswordFacade } from '@spartacus/user/profile/root';
import {
  AuthConfigService,
  GlobalMessageType,
  RoutingService,
} from '@spartacus/core';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

@Injectable()
export class AdnocForgotPasswordComponentService extends ForgotPasswordComponentService {
  constructor(
    protected override userPasswordService: UserPasswordFacade,
    protected override routingService: RoutingService,
    protected override authConfigService: AuthConfigService,
    protected override globalMessage: AdnocGlobalMessageService,
    protected cdcAuthService: CdcAuthService
  ) {
    super(
      userPasswordService,
      routingService,
      authConfigService,
      globalMessage
    );
  }

  override requestEmail(): Promise<boolean> {
    return new Promise((resolve) => {
      if (!this.form.valid) {
        this.form.markAllAsTouched();
        return;
      }
      this.busy$.next(true);
      this.userPasswordService
        .requestForgotPasswordEmail(this.form.value.userEmail)
        .subscribe({
          next: () => {
            this.onSuccess();
            resolve(true);
          },
          error: (error: Error) => {
            this.onError(error);
            resolve(false);
          },
        });
    });
  }

  requestPasswordReset(): Promise<boolean> {
    return new Promise((resolve) => {
      if (!this.form.valid) {
        this.form.markAllAsTouched();
        return resolve(false);
      }

      this.busy$.next(true);

      this.cdcAuthService
        .requestPasswordReset(this.form.value.userEmail)
        .then((response) => {
          if (response?.errorCode === 0) {
            this.onSuccess();
          } else {
            this.showGlobalError(response);
            resolve(false);
          }
          resolve(true);
        })
        .catch((error) => {
          this.onError(error);
          this.showGlobalError(error);
          resolve(false);
        });
    });
  }

  showGlobalError(error: any): void {
    this.busy$.next(false);
    let message = 'Something went wrong.';
    if (error?.errorDetails || error?.errorMessage) {
      message = error.errorDetails || error.errorMessage;
    }
    this.globalMessage.add(
      message,
      GlobalMessageType.MSG_TYPE_ERROR
    );
  }

  protected override onSuccess(): void {
    this.busy$.next(false);
    this.form.reset();
  }
}
