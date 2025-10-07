/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AdnocApiService } from '../../../../../services/apiServices/adnoc-api.service';
import { IAdnocCategory } from '../../../../root/model';
import { AssociatedObjectsList } from '@spartacus/customer-ticketing/root';

@Injectable({
  providedIn: 'root',
})
export class AdnocCustomerDialogFormService {
  constructor(protected adnocApiService: AdnocApiService) {}

  getRequestDetails(userId: string): Observable<IAdnocCategory> {
    return this.adnocApiService.getRequestDetails(userId);
  }

  getAssociateMapDetails(mapId: string): Observable<AssociatedObjectsList> {
    return this.adnocApiService.getAssociateMapDetails(mapId);
  }

  getConfigvalue(key: string) {
    return this.adnocApiService.getConfigvalue(key);
  }
}
