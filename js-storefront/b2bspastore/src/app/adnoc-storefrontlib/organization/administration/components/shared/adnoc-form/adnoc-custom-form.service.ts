import { Injectable } from '@angular/core';
import { OccEndpointsService, WindowRef } from '@spartacus/core';
import { HttpClient } from '@angular/common/http';
import { FormGroup } from '@angular/forms';
import { BehaviorSubject, Observable } from 'rxjs';
import {
  ADNOC_B2B_UNIT_REGISTRATION_KEY,
  ADNOC_B2B_USER_REGISTRATION_KEY,
} from '../../../../../constants/adnoc-user-account-constants';
import { AdnocApiEndpoints } from '../../../../../services/apiServices/adnoc-api-endpoints';

@Injectable({
  providedIn: 'root',
})
export class AdnocCustomFormService {
  private fileSubject = new BehaviorSubject<File | null>(null);
  unitParentInfo$ = new BehaviorSubject<{
    uid: string;
    partnerFunction: string;
    parentOrgUnitName: string;
  }>({ uid: '', partnerFunction: '', parentOrgUnitName: '' });
  identificationNumberDocument: File | null = null;
  vatIdDocument: File | null = null;
  constructor(
    protected winRef: WindowRef,
    protected OccEndpointsService: OccEndpointsService,
    protected http: HttpClient
  ) {}

  setFile(file: File): void {
    this.fileSubject.next(file);
  }
  setIdentificationNumberDocumentFile(file: File): void {
    this.identificationNumberDocument = file;
  }
  setvatIdDocumentFile(file: File): void {
    this.vatIdDocument = file;
  }
  registerPayerorShiptoaddress(form: FormGroup<any>): Observable<any> {
    const formData: FormData = new FormData();
    const unitData = form.value;
    delete unitData.otherDocument;
    delete unitData.identificationNumberDocument;
    delete unitData.vatIdDocument;
    const unitParentInfo = this.unitParentInfo$.getValue();
    unitData.parentB2BUnitUid = unitParentInfo?.uid || '';
    unitData.partnerFunction = unitParentInfo?.partnerFunction
      ? unitParentInfo.partnerFunction === 'SP'
        ? 'PY'
        : 'SH'
      : '';
    formData.append(ADNOC_B2B_UNIT_REGISTRATION_KEY, JSON.stringify(unitData));
    const file = this.fileSubject.getValue();
    if (file) {
      formData.append('otherDocument', file, file.name);
    }
    if (this.identificationNumberDocument) {
      formData.append(
        'identificationNumberDocument',
        this.identificationNumberDocument,
        this.identificationNumberDocument.name
      );
    }
    if (this.vatIdDocument) {
      formData.append(
        'vatIdDocument',
        this.vatIdDocument,
        this.vatIdDocument.name
      );
    }
    const url = this.OccEndpointsService.buildUrl('b2bUnitCreate');
    return this.http.post<any>(url, formData);
  }

  createSubUser(form: any): Observable<any> {
    const formData: FormData = new FormData();
    const subUserData = form.value;
    delete subUserData.identificationNumberDocument;
    formData.append(
      ADNOC_B2B_USER_REGISTRATION_KEY,
      JSON.stringify(subUserData)
    );
    if (this.identificationNumberDocument) {
      formData.append(
        'identificationNumberDocument',
        this.identificationNumberDocument,
        this.identificationNumberDocument.name
      );
    }
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.adnocSubUserCreation
    );
    return this.http.post<any>(url, formData);
  }
}
