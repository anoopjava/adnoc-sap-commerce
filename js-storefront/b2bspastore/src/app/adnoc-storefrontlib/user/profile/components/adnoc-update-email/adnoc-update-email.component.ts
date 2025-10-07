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
  UpdateEmailComponent,
} from '@spartacus/user/profile/components';

@Component({
    selector: 'adnoc-update-email',
    templateUrl: './adnoc-update-email.component.html',
    styleUrl: './adnoc-update-email.component.scss',
    encapsulation: ViewEncapsulation.None,
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'user-form adnoc-update-email' },
    standalone: false
})
export class AdnocUpdateEmailComponent extends UpdateEmailComponent {}
