/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { AdnocCurrentUnitService } from '../../../services/adnoc-current-unit.service';

@Injectable({
  providedIn: 'root',
})
export class CurrentUnitChildService extends AdnocCurrentUnitService {
  protected override getParamKey(): string {
    // We must come up with a fake param key, to avoid that the (parent) unit
    // code is loaded from the route parameter map.
    return 'childUnitCode';
  }
}
