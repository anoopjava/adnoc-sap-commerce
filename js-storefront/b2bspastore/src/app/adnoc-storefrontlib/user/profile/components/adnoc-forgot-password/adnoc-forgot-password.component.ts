/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  Inject,
  OnInit,
  ViewEncapsulation,
} from '@angular/core';
import { AdnocForgotPasswordComponentService } from './adnoc-forgot-password-component.service';
import { DOCUMENT } from '@angular/common';
import { ForgotPasswordComponent } from '@spartacus/user/profile/components';

@Component({
    selector: 'adnoc-forgot-password',
    templateUrl: './adnoc-forgot-password.component.html',
    styleUrl: './adnoc-forgot-password.component.scss',
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class AdnocForgotPasswordComponent
  extends ForgotPasswordComponent
  implements OnInit
{
  constructor(
    @Inject(DOCUMENT) private document: Document,
    override service: AdnocForgotPasswordComponentService
  ) {
    super(service);
  }

  isEmailSent: boolean = false;

  ngOnInit() {
    this.document.body.classList.add(
      'forgotPasswordPage',
      'hide-header-footer'
    );
  }

  override onSubmit(): void {
    this.service.requestPasswordReset().then((success) => {
      this.isEmailSent = success;
    });
  }
}
