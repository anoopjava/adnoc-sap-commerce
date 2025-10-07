import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { AdnocApiEndpoints } from './adnoc-api-endpoints';
import { OccEndpointsService } from '@spartacus/core';
import {
  PrimaryProducts,
  CommunicationChannel,
  Countries,
  Geneders,
  Nationalities,
  Regions,
  IdentityTypes,
  AdnocConfigRoot,
} from './api-response.model';
import { IAdnocCategory } from '../../customer-ticketing/root/model';
import { AssociatedObjectsList } from '@spartacus/customer-ticketing/root';

@Injectable({
  providedIn: 'root',
})
export class AdnocApiService {
  constructor(
    private http: HttpClient,
    private readonly OccEndpointsService: OccEndpointsService
  ) {}

  getPrimaryProducts(): Observable<PrimaryProducts> {
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.primaryProducts
    );
    return this.http.get<PrimaryProducts>(url);
  }

  getIdentityType(): Observable<IdentityTypes> {
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.identityTypes
    );
    return this.http.get<IdentityTypes>(url);
  }

  getCountries(): Observable<Countries> {
    const url = this.OccEndpointsService.buildUrl(AdnocApiEndpoints.countries);
    return this.http.get<Countries>(url);
  }

  getRegions(countryCode: string): Observable<Regions> {
    let url = this.OccEndpointsService.buildUrl(AdnocApiEndpoints.regions, {
      urlParams: {
        countryCode,
      },
    });
    return this.http.get<Regions>(url);
  }

  getPreferredCommunicationChannel(): Observable<CommunicationChannel> {
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.preferredCommunicationChannels
    );
    return this.http.get<CommunicationChannel>(url);
  }

  getGenders(): Observable<Geneders> {
    const url = this.OccEndpointsService.buildUrl(AdnocApiEndpoints.genders);

    return this.http.get<Geneders>(url);
  }

  getNationalities(): Observable<Nationalities> {
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.nationalities
    );

    return this.http.get<Nationalities>(url);
  }

  getConfigvalue(key: String): Observable<AdnocConfigRoot> {
    let url = this.OccEndpointsService.buildUrl(AdnocApiEndpoints.configKeys, {
      urlParams: {
        key,
      },
    });

    return this.http.get<AdnocConfigRoot>(url);
  }

  getRequestDetails(userId: string): Observable<IAdnocCategory> {
    let url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.adnocCsTicketCategoryMap,
      {
        urlParams: {
          userId,
        },
      }
    );
    return this.http.get<IAdnocCategory>(url);
  }

  getAssociateMapDetails(mapId: string): Observable<AssociatedObjectsList> {
    let url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.AdnocTicketAssociatedObjectsMap,
      {
        urlParams: {
          mapId,
        },
      }
    );
    return this.http.get<AssociatedObjectsList>(url);
  }
}
