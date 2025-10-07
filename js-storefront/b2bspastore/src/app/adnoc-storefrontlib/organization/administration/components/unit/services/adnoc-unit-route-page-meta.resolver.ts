/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import {
  B2BUnit,
  DefaultRoutePageMetaResolver,
  TranslationService,
} from '@spartacus/core';
import { AdnocCurrentUnitService } from './adnoc-current-unit.service';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AdnocUnitRoutePageMetaResolver extends DefaultRoutePageMetaResolver {
  constructor(
    translation: TranslationService,
    protected adnocCurrentUnitService: AdnocCurrentUnitService
  ) {
    super(translation);
  }

  protected override getParams(): Observable<B2BUnit | undefined> {
    return this.adnocCurrentUnitService.item$;
  }
}
