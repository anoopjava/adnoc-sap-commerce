import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { OccEndpointsService } from '@spartacus/core';
import { Observable, Subject } from 'rxjs';
import { AdnocApiEndpoints } from '../../../../services/apiServices/adnoc-api-endpoints';
import {
  ICreditLimit,
  ICurrentUser,
  userOrdersSummary,
} from '../../../../services/apiServices/api-response.model';

@Injectable({
  providedIn: 'root',
})
export class AdnocUserCreditOrderInfoComponentService {
  private payerUpdatedSource = new Subject<void>();
  payerUpdated$ = this.payerUpdatedSource.asObservable();
  constructor(
    private readonly OccEndpointsService: OccEndpointsService,
    private http: HttpClient
  ) {}

  getCurrentUser(): Observable<ICurrentUser> {
    let url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.adnocGetCurrentuser
    );
    return this.http.get<ICurrentUser>(url);
  }

  getCreditLimit(b2bUnitUid: string): Observable<ICreditLimit> {
    let url = this.OccEndpointsService.buildUrl(
      'orgUsers/current/getCreditLimit'
    );
    return this.http.post<ICreditLimit>(url, { b2bUnitUid });
  }

  getUserOderSummary() {
    let url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.adnocUserOrdersInfo
    );
    return this.http.get<userOrdersSummary>(url);
  }

  notifyPayerUpdated() {
    this.payerUpdatedSource.next();
  }
}
