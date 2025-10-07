/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { EntitiesModel, PaginationModel } from '@spartacus/core';
import {
  B2BUnitNode,
  OrgUnitService,
} from '@spartacus/organization/administration/core';
import { TableService } from '@spartacus/storefront';
import { Observable } from 'rxjs';
//import { SubListService } from '../../../shared/sub-list/sub-list.service';
import { OrganizationTableType } from '../../../shared/organization.model';
import { SubListService } from '@spartacus/organization/administration/components';

@Injectable({
  providedIn: 'root',
})
export class UnitChildrenService extends SubListService<B2BUnitNode> {
  protected tableType = OrganizationTableType.UNIT_CHILDREN;
  protected override _domainType = OrganizationTableType.UNIT;

  constructor(
    protected override tableService: TableService,
    protected orgUnitService: OrgUnitService
  ) {
    super(tableService);
  }

  // method to be adjusted for proper children list when ready
  protected load(
    _pagination: PaginationModel,
    code: string
  ): Observable<EntitiesModel<B2BUnitNode>> {
    return this.orgUnitService.getChildUnits(code);
  }
}
