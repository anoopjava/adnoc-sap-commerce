import { Injectable } from '@angular/core';
import {
  UntypedFormArray,
  UntypedFormControl,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { B2BUser, B2BUserRole, OccEndpointsService } from '@spartacus/core';
import { AdnocFormService } from '../../shared/adnoc-form/adnoc-form.service';
import { CustomFormValidators } from '@spartacus/storefront';
import { Observable } from 'rxjs';
import {
  AdnocConfigRoot,
  CommunicationChannel,
  Countries,
  Designations,
  Geneders,
  IdentityTypes,
  Nationalities,
  Regions,
} from '../../../../../services/apiServices/api-response.model';
import { AdnocApiEndpoints } from '../../../../../services/apiServices/adnoc-api-endpoints';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class AdnocUserFormService extends AdnocFormService<B2BUser> {
  constructor(
    private readonly OccEndpointsService: OccEndpointsService,
    private http: HttpClient
  ) {
    super();
  }
  protected build() {
    const form = new UntypedFormGroup({});
    form.setControl('customerId', new UntypedFormControl(''));
    form.setControl(
      'titleCode',
      new UntypedFormControl(null, [Validators.required])
    );
    form.setControl(
      'firstName',
      new UntypedFormControl('', [
        Validators.required,
        Validators.maxLength(40),
      ])
    );
    form.setControl(
      'lastName',
      new UntypedFormControl('', [
        Validators.required,
        Validators.maxLength(40),
      ])
    );
    form.setControl(
      'email',
      new UntypedFormControl('', [
        Validators.required,
        CustomFormValidators.emailValidator,
        Validators.maxLength(241),
      ])
    );
    form.setControl(
      'orgUnit',
      new UntypedFormGroup({
        uid: new UntypedFormControl(undefined, Validators.required),
      })
    );
    form.setControl('roles', new UntypedFormArray([]));
    form.setControl('isAssignedToApprovers', new UntypedFormControl(false));

    form.get('roles')?.valueChanges.subscribe((roles: string[]) => {
      if (roles.includes(B2BUserRole.APPROVER)) {
        form.get('isAssignedToApprovers')?.enable();
      } else {
        form.get('isAssignedToApprovers')?.disable();
        form.get('isAssignedToApprovers')?.reset();
      }
    });
    form.setControl(
      'nationality',
      new UntypedFormControl(null, Validators.required)
    );
    form.setControl(
      'countryOfOrigin',
      new UntypedFormControl(null, Validators.required)
    );
    form.setControl(
      'identityType',
      new UntypedFormControl(null, Validators.required)
    );
    form.setControl(
      'identificationNumber',
      new UntypedFormControl('', [
        Validators.required,
        Validators.maxLength(20),
      ])
    );
    form.setControl(
      'identificationNumberDocument',
      new UntypedFormControl('', [Validators.required])
    );
    form.setControl(
      'identificationValidFrom',
      new UntypedFormControl(null, [Validators.required])
    );
    form.setControl(
      'identificationValidTo',
      new UntypedFormControl(null, [Validators.required])
    );
    form.setControl(
      'designation',
      new UntypedFormControl('', [
        Validators.required,
        Validators.maxLength(40),
      ])
    );
    form.setControl(
      'gender',
      new UntypedFormControl(null, [Validators.required])
    );
    form.setControl(
      'telephone',
      new UntypedFormControl('', [
        Validators.pattern('^[0-9]*$'),
        Validators.maxLength(10),
      ])
    );
    form.setControl(
      'mobileNumber',
      new UntypedFormControl('', [
        Validators.required,
        Validators.pattern('^[0-9]*$'),
        Validators.maxLength(10),
      ])
    );
    form.setControl(
      'preferredCommunicationChannel',
      new UntypedFormControl(null)
    );
    form.setControl(
      'companyAddressStreet',
      new UntypedFormControl('', [
        Validators.required,
        Validators.maxLength(60),
      ])
    );
    form.setControl(
      'companyAddressStreetLine2',
      new UntypedFormControl('', [Validators.maxLength(35)])
    );
    form.setControl(
      'companyAddressCountryIso',
      new UntypedFormControl(null, [Validators.required])
    );
    form.setControl(
      'companyAddressRegion',
      new UntypedFormControl(null, [Validators.required])
    );
    form.setControl(
      'companyAddressCity',
      new UntypedFormControl('', [
        Validators.required,
        Validators.maxLength(40),
      ])
    );
    this.form = form;
  }

  protected override patchData(item: B2BUser) {
    super.patchData(item);
    if (item) {
      const roles = this.form?.get('roles') as UntypedFormArray;
      const emailFormControl = this.form?.get('email');
      item.roles?.forEach((role) => {
        if (!(roles.value as string[]).includes(role)) {
          roles.push(new UntypedFormControl(role));
        }
      });
      if (item.displayUid && emailFormControl) {
        emailFormControl.setValue(item.displayUid);
      }
    }
  }

  getGenderOptions(): Observable<Geneders> {
    const url = this.OccEndpointsService.buildUrl(AdnocApiEndpoints.genders);
    return this.http.get<Geneders>(url);
  }

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

  // Gets all CommunicationChannels.
  getcommunicationChannels(): Observable<CommunicationChannel> {
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.preferredCommunicationChannels
    );
    return this.http.get<CommunicationChannel>(url);
  }
  getDesignationTypes(): Observable<Designations> {
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.designation
    );
    return this.http.get<Designations>(url);
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

  getConfigvalue(key: string) {
    let url = this.OccEndpointsService.buildUrl(AdnocApiEndpoints.configKeys, {
      urlParams: {
        key,
      },
    });
    return this.http.get<AdnocConfigRoot>(url);
  }
}
