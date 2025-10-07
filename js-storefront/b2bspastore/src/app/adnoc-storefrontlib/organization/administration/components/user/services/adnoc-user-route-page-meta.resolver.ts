/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import {
  B2BUser,
  DefaultRoutePageMetaResolver,
  TranslationService,
} from '@spartacus/core';
import { CurrentUserService } from '@spartacus/organization/administration/components';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AdnocUserRoutePageMetaResolver extends DefaultRoutePageMetaResolver {
  constructor(
    translation: TranslationService,
    protected currentItemService: CurrentUserService
  ) {
    super(translation);
  }

  override getParams(): Observable<B2BUser | undefined> {
    return this.currentItemService.item$;
  }
}
