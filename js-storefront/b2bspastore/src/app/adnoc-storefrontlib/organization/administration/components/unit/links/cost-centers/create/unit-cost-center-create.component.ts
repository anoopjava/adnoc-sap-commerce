/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Observable } from 'rxjs';
import { CostCenterItemService } from '../../../../cost-center/services/cost-center-item.service';
import { UnitCostCenterItemService } from './unit-cost-center-item.service';
import { AdnocCurrentUnitService } from '../../../services/adnoc-current-unit.service';

@Component({
    selector: 'cx-org-unit-cost-center-create',
    templateUrl: './unit-cost-center-create.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'content-wrapper' },
    providers: [
        // we provide a specific version of the `CostCenterItemService` to
        // let the form component work with unit cost centers.
        {
            provide: CostCenterItemService,
            useExisting: UnitCostCenterItemService,
        },
    ],
    standalone: false
})
export class UnitCostCenterCreateComponent {
  unitKey$: Observable<string>;
  constructor(protected unitService: AdnocCurrentUnitService) {
    this.unitKey$ = this.unitService.key$;
  }
}
