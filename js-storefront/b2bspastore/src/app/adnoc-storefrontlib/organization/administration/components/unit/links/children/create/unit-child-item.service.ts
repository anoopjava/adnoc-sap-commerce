/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { B2BUnit, RoutingService } from '@spartacus/core';
import {
  OrganizationItemStatus,
  OrgUnitService,
} from '@spartacus/organization/administration/core';
import { Observable } from 'rxjs';
import { CurrentUnitChildService } from './current-unit-child.service';
import { AdnocUnitFormService } from '../../../form/adnoc-unit-form.service';
import { AdnocUnitItemService } from '../../../services/adnoc-unit-item.service';

@Injectable({
  providedIn: 'root',
})
export class UnitChildItemService extends AdnocUnitItemService {
  constructor(
    protected override currentItemService: CurrentUnitChildService,
    protected override routingService: RoutingService,
    protected override formService: AdnocUnitFormService,
    protected override unitService: OrgUnitService
  ) {
    super(currentItemService, routingService, formService, unitService);
  }

  override save(
    form: UntypedFormGroup,
    key?: string
  ): Observable<OrganizationItemStatus<B2BUnit>> {
    // we enable the parentOrgUnit temporarily so that the underlying
    // save method can read the complete form.value.
    form.get('parentOrgUnit')?.enable();
    return super.save(form, key);
  }

  /**
   * @override
   * Returns 'unitDetails'
   */
  protected override getDetailsRoute(): string {
    return 'orgUnitChildren';
  }

  protected override buildRouteParams(item: B2BUnit) {
    return { uid: item.parentOrgUnit?.uid };
  }
}
