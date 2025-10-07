import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { OccEndpointsService } from '@spartacus/core';
import { Observable } from 'rxjs';
import { B2BUnitNode } from '../../../core/model/unit-node.model';

@Injectable({
  providedIn: 'root',
})
export class AdnocUpdatedUnitTreeService {
  constructor(
    protected OccEndpointsService: OccEndpointsService,
    protected http: HttpClient
  ) {}

  getUpdatedOrgUnitsTreeEndpoint(): Observable<B2BUnitNode> {
    let url = this.OccEndpointsService.buildUrl('b2bUnitLinkUpdate');
    return this.http.get<B2BUnitNode>(url);
  }
}
