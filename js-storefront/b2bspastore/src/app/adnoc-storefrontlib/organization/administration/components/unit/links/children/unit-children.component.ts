/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { B2BUnit } from '@spartacus/core';
import { Observable, of } from 'rxjs';
import { UnitChildrenService } from './unit-children.service';
import { AdnocListService } from '../../../shared/list/adnoc-list.service';
import { AdnocCurrentUnitService } from '../../services/adnoc-current-unit.service';

@Component({
    selector: 'cx-org-unit-children',
    templateUrl: './unit-children.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'content-wrapper' },
    providers: [
        {
            provide: AdnocListService,
            useExisting: UnitChildrenService,
        },
    ],
    standalone: false
})
export class UnitChildrenComponent {
  unit$: Observable<B2BUnit | undefined>;

  constructor(protected currentUnitService: AdnocCurrentUnitService) {
    this.unit$ = this.currentUnitService
    ? this.currentUnitService.item$
    : of({ active: true });
  }
}
