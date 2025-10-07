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
import { MyAccountV2PasswordComponent } from '@spartacus/user/profile/components';

@Component({
    selector: 'adnoc-my-account-v2-password',
    templateUrl: './adnoc-my-account-v2-password.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    encapsulation: ViewEncapsulation.None,
    standalone: false
})
export class AdnocMyAccountV2PasswordComponent extends MyAccountV2PasswordComponent {}
