/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */
import { Injectable } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {
  AuthConfigService,
  GlobalMessageType,
  OccEndpointsService,
  OAuthFlow,
  RoutingService,
  TranslationService,
} from '@spartacus/core';
import { CustomFormValidators } from '@spartacus/storefront';
import { Title, UserRegisterFacade } from '@spartacus/user/profile/root';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import {
  Countries,
  Regions,
  IdentityTypes,
  PrimaryProducts,
  CommunicationChannel,
  Geneders,
  Nationalities,
  AdnocConfigRoot,
  Designations,
  TradeLicenseAuthority,
} from '../../../../services/apiServices/api-response.model';
import { OrganizationUserRegistration } from '../../root/model';
import { AdnocApiEndpoints } from '../../../../services/apiServices/adnoc-api-endpoints';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

@Injectable({
  providedIn: 'root',
})
export class AdnocUserRegistrationFormService {
  private _form: FormGroup;
  tlnDocument: File | null = null;
  vatIdDocument: File | null = null;
  selectedFile: File | null = null;
  identificationNumberDocument: File | null = null;

  constructor(
    protected userRegisterFacade: UserRegisterFacade,
    protected translationService: TranslationService,
    protected globalMessageService: AdnocGlobalMessageService,
    protected authConfigService: AuthConfigService,
    protected routingService: RoutingService,
    protected formBuilder: FormBuilder,
    private readonly OccEndpointsService: OccEndpointsService,
    private http: HttpClient
  ) {
    this._form = this.buildForm();
  }

  // Initializes form structure for registration.
  protected buildForm(): FormGroup {
    return this.formBuilder.group({
      partnerFunction: ['SP'],
      primaryProduct: [null, Validators.required],
      companyName: ['', [Validators.required, Validators.maxLength(40)]],
      companyName2: ['', Validators.maxLength(40)],
      companyEmail: [
        '',
        [
          Validators.required,
          CustomFormValidators.emailValidator,
          Validators.maxLength(241),
        ],
      ],
      companyWebsite: ['', Validators.maxLength(40)],
      companyPhoneNumber: [
        '',
        [
          Validators.required,
          Validators.pattern('^[0-9]*$'),
          Validators.minLength(10),
          Validators.maxLength(10),
        ],
      ],
      companyMobileNumber: [
        '',
        [
          Validators.pattern('^[0-9]*$'),
          Validators.minLength(10),
          Validators.maxLength(10),
        ],
      ],
      tradeLicenseNumber: [
        '',
        [
          Validators.required,
          Validators.minLength(10),
          Validators.maxLength(10),
          Validators.pattern(/^[A-Z]{2}-\d{7}$/),
        ],
      ],
      tlnDocument: ['', Validators.required],
      tradeLicenseAuthority: [null, Validators.required],
      tlnValidFrom: [null, Validators.required],
      tlnValidTo: [null, Validators.required],
      vatId: [
        '',
        [
          Validators.required,
          Validators.minLength(15),
          Validators.maxLength(15),
        ],
      ],
      vatIdDocument: ['', Validators.required],
      otherDocument: [''],
      faxNumber: ['', [Validators.minLength(10), Validators.maxLength(10)]],
      companyAddressStreet: [
        '',
        [Validators.required, Validators.maxLength(60)],
      ],
      companyAddressStreetLine2: ['', [Validators.maxLength(35)]],
      companyAddressCountryIso: [null, Validators.required],
      companyAddressRegion: [null, Validators.required],
      companyAddressCity: ['', [Validators.required, Validators.maxLength(40)]],
      poBox: [
        '',
        [Validators.maxLength(10), Validators.pattern('^[a-zA-Z0-9]*$')],
      ],
      companyAddressPostalCode: [
        '',
        [Validators.maxLength(10), Validators.pattern('^[a-zA-Z0-9]*$')],
      ],
      preferredCommunicationChannel: [null],
      firstName: ['', [Validators.required, Validators.maxLength(40)]],
      lastName: ['', [Validators.required, Validators.maxLength(40)]],
      titleCode: [null, [Validators.required]],
      gender: [null, Validators.required],
      nationality: [null, Validators.required],
      countryOfOrigin: [null, Validators.required],
      identityType: [null, Validators.required],
      identificationNumber: [
        '',
        [Validators.required, Validators.maxLength(20)],
      ],
      identificationNumberDocument: ['', Validators.required],
      identificationValidFrom: [null, Validators.required],
      identificationValidTo: [null, Validators.required],
      designation: [null, Validators.required],
      email: [
        '',
        [
          Validators.required,
          Validators.maxLength(241),
          CustomFormValidators.emailValidator,
        ],
      ],
      telephone: [
        '',
        [
          Validators.pattern('^[0-9]*$'),
          Validators.minLength(10),
          Validators.maxLength(10),
        ],
      ],
      mobileNumber: [
        '',
        [
          Validators.required,
          Validators.pattern('^[0-9]*$'),
          Validators.minLength(10),
          Validators.maxLength(10),
        ],
      ],
    });
  }

  //Gets form structure for registration.
  public get form(): FormGroup {
    return this._form;
  }

  // Gets all titles.
  getTitles(): Observable<Title[]> {
    return this.userRegisterFacade.getTitles();
  }

  // Gets all gender.
  getGenderOptions(): Observable<Geneders> {
    const url = this.OccEndpointsService.buildUrl(AdnocApiEndpoints.genders);
    return this.http.get<Geneders>(url);
  }

  // Gets all primaryProducts.
  getprimaryProductOptions(): Observable<PrimaryProducts> {
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.primaryProducts
    );
    return this.http.get<PrimaryProducts>(url);
  }

  // Gets all identity Types.
  getIdentityType(): Observable<IdentityTypes> {
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.identityTypes
    );
    return this.http.get<IdentityTypes>(url);
  }

  // Gets all CommunicationChannels.
  getcommunicationChannels(): Observable<CommunicationChannel> {
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.preferredCommunicationChannels
    );
    return this.http.get<CommunicationChannel>(url);
  }

  // Gets all countries list.
  getCountries(): Observable<Countries> {
    const url = this.OccEndpointsService.buildUrl(AdnocApiEndpoints.countries);
    return this.http.get<Countries>(url);
  }

  //Gets all regions(city) list for specific selected country.
  getRegions(country = 'AE'): Observable<Regions> {
    let countryCode =
      this._form.get('companyAddressCountryIso')?.value || country;
    let url = this.OccEndpointsService.buildUrl(AdnocApiEndpoints.regions, {
      urlParams: {
        countryCode,
      },
    });
    return this.http.get<Regions>(url);
  }

  // Gets all Nationalities list.
  getNationalities(): Observable<Nationalities> {
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.nationalities
    );
    return this.http.get<Nationalities>(url);
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

  //Displays confirmation global message.
  protected displayGlobalMessage(): void {
    return this.globalMessageService.add(
      { key: 'userRegistrationForm.successFormSubmitMessage' },
      GlobalMessageType.MSG_TYPE_CONFIRMATION
    );
  }

  // Redirects the user back to the login page.
  // This only happens in case of the `ResourceOwnerPasswordFlow` OAuth flow.
  protected redirectToLogin(): void {
    if (
      this.authConfigService.getOAuthFlow() ===
      OAuthFlow.ResourceOwnerPasswordFlow
    ) {
      this.routingService.go({ cxRoute: 'login' });
    }
  }

  handleFileInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files ? input.files[0] : null;
    if (input.files && input.files.length > 0) {
      switch (input.id) {
        case 'tlnDocument':
          this.tlnDocument = file;
          break;
        case 'vatIdDocument':
          this.vatIdDocument = file;
          break;
        case 'identificationNumberDocument':
          this.identificationNumberDocument = file;
          break;
        default:
          this.selectedFile = file;
          break;
      }
    }
  }

  //Registers new organization user.
  registerUser(form: FormGroup<any>): Observable<OrganizationUserRegistration> {
    const formData: FormData = new FormData();
    const userData = form.value;
    delete userData.tlnDocument;
    delete userData.vatIdDocument;
    delete userData.otherDocument;
    delete userData.identificationNumberDocument;
    formData.append('orgUserRegistrationData', JSON.stringify(userData));
    if (this.tlnDocument) {
      formData.append('tlnDocument', this.tlnDocument, this.tlnDocument.name);
    }
    if (this.vatIdDocument) {
      formData.append(
        'vatIdDocument',
        this.vatIdDocument,
        this.vatIdDocument.name
      );
    }
    if (this.selectedFile) {
      formData.append(
        'otherDocument',
        this.selectedFile,
        this.selectedFile.name
      );
    }
    if (this.identificationNumberDocument) {
      formData.append(
        'identificationNumberDocument',
        this.identificationNumberDocument,
        this.identificationNumberDocument.name
      );
    }
    const url = this.OccEndpointsService.buildUrl(AdnocApiEndpoints.createUser);
    return this.http.post<OrganizationUserRegistration>(url, formData);
  }
}
