/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component, ViewEncapsulation } from '@angular/core';
import { CellComponent } from '../cell.component';

@Component({
    selector: 'cx-org-roles-cell',
    templateUrl: './roles-cell.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['./roles-cell.component.scss'],
    encapsulation: ViewEncapsulation.None,
    standalone: false
})
export class RolesCellComponent extends CellComponent {}
