/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import {
  UntypedFormControl,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { B2BUnit, OccEndpointsService } from '@spartacus/core';
import { CustomFormValidators } from '@spartacus/storefront';
import { Title, UserRegisterFacade } from '@spartacus/user/profile/root';
import { AdnocFormService } from '../../shared/adnoc-form/adnoc-form.service';
import {
  AdnocConfigRoot,
  Countries,
  Designations,
  Geneders,
  IdentityTypes,
  IncoTerms,
  Nationalities,
  Regions,
  TradeLicenseAuthority,
} from '../../../../../services/apiServices/api-response.model';
import { Observable } from 'rxjs';
import { AdnocApiEndpoints } from '../../../../../services/apiServices/adnoc-api-endpoints';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AdnocUnitFormService extends AdnocFormService<B2BUnit> {
  constructor(
    protected userRegisterFacade: UserRegisterFacade,
    private readonly OccEndpointsService: OccEndpointsService,
    private http: HttpClient
  ) {
    super();
  }

  protected build() {
    const form = new UntypedFormGroup({
      companyName: new UntypedFormControl('', [
        Validators.required,
        Validators.maxLength(40),
      ]),
      companyName2: new UntypedFormControl('', [Validators.maxLength(40)]),
      incoTerms: new UntypedFormControl(null, Validators.required),
      companyAddressStreet: new UntypedFormControl('', [
        Validators.required,
        Validators.maxLength(60),
      ]),
      companyAddressStreetLine2: new UntypedFormControl('', [
        Validators.maxLength(35),
      ]),
      companyAddressCountryIso: new UntypedFormControl(
        null,
        Validators.required
      ),
      companyAddressRegion: new UntypedFormControl(null, Validators.required),
      companyAddressCity: new UntypedFormControl('', [
        Validators.required,
        Validators.maxLength(40),
      ]),
      poBox: new UntypedFormControl('', [
        Validators.maxLength(10),
        Validators.pattern('^[a-zA-Z0-9]*$'),
      ]),
      companyAddressPostalCode: new UntypedFormControl('', [
        Validators.maxLength(10),
        Validators.pattern('^[a-zA-Z0-9]*$'),
      ]),
      faxNumber: new UntypedFormControl('', [
        Validators.minLength(10),
        Validators.maxLength(10),
      ]),
      vatId: new UntypedFormControl([
        '',
        [
          Validators.required,
          Validators.minLength(15),
          Validators.maxLength(15),
        ],
      ]),
      vatIdDocument: new UntypedFormControl('', [
        Validators.required,
        Validators.minLength(15),
        Validators.maxLength(15),
      ]),
      otherDocument: new UntypedFormControl(''),
      titleCode: new UntypedFormControl(null, Validators.required),
      firstName: new UntypedFormControl('', [
        Validators.required,
        Validators.maxLength(40),
      ]),
      lastName: new UntypedFormControl('', [
        Validators.required,
        Validators.maxLength(40),
      ]),
      gender: new UntypedFormControl(null, Validators.required),
      nationality: new UntypedFormControl(null, Validators.required),
      countryOfOrigin: new UntypedFormControl(null, Validators.required),
      identityType: new UntypedFormControl(null, Validators.required),
      identificationNumber: new UntypedFormControl('', [
        Validators.required,
        Validators.maxLength(20),
      ]),
      identificationNumberDocument: new UntypedFormControl(
        '',
        Validators.required
      ),
      identificationValidFrom: new UntypedFormControl(
        null,
        Validators.required
      ),
      identificationValidTo: new UntypedFormControl(null, Validators.required),
      designation: new UntypedFormControl(null, Validators.required),
      email: new UntypedFormControl('', [
        Validators.required,
        Validators.maxLength(241),
        CustomFormValidators.emailValidator,
      ]),
      telephone: new UntypedFormControl('', [
        Validators.pattern('^[0-9]*$'),
        Validators.minLength(10),
        Validators.maxLength(10),
      ]),
      mobileNumber: new UntypedFormControl('', [
        Validators.required,
        Validators.pattern('^[0-9]*$'),
        Validators.minLength(10),
        Validators.maxLength(10),
      ]),
      latitude: new UntypedFormControl('', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(8),
        Validators.pattern(/^\d{8}$/),
      ]),
      longitude: new UntypedFormControl('', [
        Validators.required,
        Validators.minLength(8),
        Validators.maxLength(8),
        Validators.pattern(/^\d{8}$/),
      ]),
    });
    
    this.form = form;
  }

  protected isRootUnit(item: B2BUnit | undefined): boolean {
    // as we don't have full response after toggle item status,
    // we have situation where we have object like {uid, active},
    // so decided to check name as alternative required property
    return Boolean(
      item?.uid &&
        item?.name &&
        (!item?.parentOrgUnit || item?.uid === item?.parentOrgUnit)
    );
  }

  // Gets all titles.
  getTitles(): Observable<Title[]> {
    return this.userRegisterFacade.getTitles();
  }

  // Gets all countries list.
  getCountries(): Observable<Countries> {
    const url = this.OccEndpointsService.buildUrl(AdnocApiEndpoints.countries);
    return this.http.get<Countries>(url);
  }

  //Gets all regions(city) list for specific selected country.
  getRegions(country = 'AE'): Observable<Regions> {
    let countryCode =
      this.form?.get('companyAddressCountryIso')?.value || country;
    let url = this.OccEndpointsService.buildUrl(AdnocApiEndpoints.regions, {
      urlParams: {
        countryCode,
      },
    });
    return this.http.get<Regions>(url);
  }

  // Gets all gender.
  getGenderOptions(): Observable<Geneders> {
    const url = this.OccEndpointsService.buildUrl(AdnocApiEndpoints.genders);
    return this.http.get<Geneders>(url);
  }

  // Gets all Nationalities list.
  getNationalities(): Observable<Nationalities> {
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.nationalities
    );
    return this.http.get<Nationalities>(url);
  }

  // Gets all identity Types.
  getIdentityType(): Observable<IdentityTypes> {
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.identityTypes
    );
    return this.http.get<IdentityTypes>(url);
  }

  getConfigvalue(key: string) {
    let url = this.OccEndpointsService.buildUrl(AdnocApiEndpoints.configKeys, {
      urlParams: {
        key,
      },
    });

    return this.http.get<AdnocConfigRoot>(url);
  }

  getTradeLicenseAuthorityTypes(): Observable<TradeLicenseAuthority> {
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.tradeLicenseAuthorityTypes
    );
    return this.http.get<TradeLicenseAuthority>(url);
  }

  getDesignationTypes(): Observable<Designations> {
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.designation
    );
    return this.http.get<Designations>(url);
  }

  getIncoTerms(): Observable<IncoTerms> {
    const url = this.OccEndpointsService.buildUrl(AdnocApiEndpoints.incoTerms);
    return this.http.get<IncoTerms>(url);
  }
}
