/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { CellComponent } from '../cell.component';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { UrlModule, I18nModule } from '@spartacus/core';
import { IconModule } from '@spartacus/storefront';

@Component({
    selector: 'cx-org-active-link-cell',
    templateUrl: '../cell.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    imports: [CommonModule, RouterModule, UrlModule, I18nModule, IconModule]
})
export class ActiveLinkCellComponent extends CellComponent {
  override get tabIndex() {
    return 0;
  }
}
