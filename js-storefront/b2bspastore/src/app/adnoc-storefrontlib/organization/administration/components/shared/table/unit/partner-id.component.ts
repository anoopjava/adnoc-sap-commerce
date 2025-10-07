/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { CellComponent } from '../cell.component';

@Component({
  selector: 'cx-org-unit-cell',
  templateUrl: '../cell.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class ParnterIdCellComponent extends CellComponent {
  override get property(): string | undefined {
    return this.model?.['sapBusinessPartnerID'];
  }
}
