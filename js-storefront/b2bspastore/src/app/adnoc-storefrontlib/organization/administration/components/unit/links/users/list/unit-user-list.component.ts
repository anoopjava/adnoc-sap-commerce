/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { B2BUnit } from '@spartacus/core';
import { ROUTE_PARAMS } from '@spartacus/organization/administration/root';
import { Observable, of } from 'rxjs';
import { UnitUserListService } from '../services/unit-user-list.service';
import { B2BUserService } from '@spartacus/organization/administration/core';
import { AdnocListService } from '../../../../shared/list/adnoc-list.service';
import { AdnocCurrentUnitService } from '../../../services/adnoc-current-unit.service';

@Component({
  selector: 'cx-org-unit-user-list',
  templateUrl: './unit-user-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'content-wrapper' },
  providers: [
    {
      provide: AdnocListService,
      useExisting: UnitUserListService,
    },
  ],
  standalone: false,
})
export class UnitUserListComponent {
  routerKey = ROUTE_PARAMS.userCode;

  unit$: Observable<B2BUnit | undefined>;

  isUpdatingUserAllowed;

  constructor(
    protected currentUnitService: AdnocCurrentUnitService,
    protected b2bUserService: B2BUserService
  ) {
    this.unit$ = this.currentUnitService
      ? this.currentUnitService.item$
      : of({ active: true });
    this.isUpdatingUserAllowed = this.b2bUserService.isUpdatingUserAllowed();
  }
}
