/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Observable } from 'rxjs';
import { UnitUserItemService } from './unit-user-item.service';
import { AdnocUserItemService } from '../../../../user/services/adnoc-user-item.service';
import { AdnocCurrentUnitService } from '../../../services/adnoc-current-unit.service';

@Component({
    selector: 'cx-org-unit-user-create',
    templateUrl: './unit-user-create.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'content-wrapper' },
    providers: [
        // we provide a specific version of the `UnitItemService` to
        // let the form component work with child units.
        {
            provide: AdnocUserItemService,
            useExisting: UnitUserItemService,
        },
    ],
    standalone: false
})
export class UnitUserCreateComponent {
  unitKey$: Observable<string>;
  constructor(protected unitService: AdnocCurrentUnitService) {
    this.unitKey$ = this.unitService.key$;
  }
}
