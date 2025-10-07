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
  UpdateProfileComponent,
} from '@spartacus/user/profile/components';

@Component({
    selector: 'adnoc-update-profile',
    templateUrl: './adnoc-update-profile.component.html',
    styleUrl: './adnoc-update-profile.component.scss',
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'user-form adnoc-update-profile' },
    standalone: false
})
export class AdnocUpdateProfileComponent extends UpdateProfileComponent {}
