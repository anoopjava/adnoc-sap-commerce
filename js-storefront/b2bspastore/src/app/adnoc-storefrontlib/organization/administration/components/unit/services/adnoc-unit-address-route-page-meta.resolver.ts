/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import {
  Address,
  DefaultRoutePageMetaResolver,
  TranslationService,
} from '@spartacus/core';
import { CurrentUnitAddressService } from '@spartacus/organization/administration/components';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AdnocUnitAddressRoutePageMetaResolver extends DefaultRoutePageMetaResolver {
  constructor(
    translation: TranslationService,
    protected currentItemService: CurrentUnitAddressService
  ) {
    super(translation);
  }

  protected override getParams(): Observable<Address | undefined> {
    return this.currentItemService.item$;
  }
}
