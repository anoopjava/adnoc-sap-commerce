/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Observable } from 'rxjs';
import { UnitChildItemService } from './unit-child-item.service';
import { AdnocCurrentUnitService } from '../../../services/adnoc-current-unit.service';
import { AdnocUnitItemService } from '../../../services/adnoc-unit-item.service';

@Component({
    selector: 'cx-org-unit-child-create',
    templateUrl: './unit-child-create.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'content-wrapper' },
    providers: [
        // we provide a specific version of the `UnitItemService` to
        // let the form component work with child units.
        {
            provide: AdnocUnitItemService,
            useExisting: UnitChildItemService,
        },
    ],
    standalone: false
})
export class UnitChildCreateComponent {
  unitKey$: Observable<string>;
  constructor(protected unitService: AdnocCurrentUnitService) {
    this.unitKey$ = this.unitService.key$;
  }
}
