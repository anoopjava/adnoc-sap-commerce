import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { LanguageService } from '@spartacus/core';
import { CdcConfigService } from './cdc-config.service';
import { CdcApiEndpoints } from './cdc-api-endpoints';
import { firstValueFrom } from 'rxjs';
import {
  CompleteVerificationResponse,
  FinalizeRegistrationResponse,
  FinalizeTfaResponse,
  InitTfaResponse,
  LoginResponse,
  RequestPasswordReset,
  ResetPassword,
  TfaEmailsResponse,
  TfaProvidersResponse,
  TfaSendCodeResponse,
  UpdatePassword,
} from './cdc-model';

@Injectable({
  providedIn: 'root',
})
export class CdcAuthService {
  private readonly dataCenter: string;
  private readonly apiKey: string;
  constructor(
    private http: HttpClient,
    private cdcConfig: CdcConfigService,
    private languageService: LanguageService
  ) {
    this.dataCenter = this.cdcConfig.getDataCenter() || 'eu1';
    this.apiKey = this.cdcConfig.getApiKey();
  }

  private getJsonHeaders(): HttpHeaders {
    return new HttpHeaders({
      'Content-Type': 'application/json',
    });
  }

  buildCdcUrl(template: string, urlParams: Record<string, string>): string {
    return Object.entries(urlParams).reduce(
      (url, [key, value]) => url.replace(`\${${key}}`, value),
      template
    );
  }

  async login(email: string, password: string): Promise<LoginResponse> {
    const lang = await firstValueFrom(this.languageService.getActive());
    const sessionExpiration = this.cdcConfig.getSessionExpiration();
    const include = this.cdcConfig.getIncludeParams();
    const formData: FormData = new FormData();
    formData.append('loginID', email);
    formData.append('password', password);
    formData.append('sessionExpiration', sessionExpiration.toString());
    formData.append('include', include);
    formData.append('includeUserInfo', 'true');
    formData.append('loginMode', 'standard');
    formData.append('lang', lang);
    formData.append('APIKey', this.apiKey);
    const url = this.buildCdcUrl(CdcApiEndpoints.login, {
      dataCenter: this.dataCenter,
    });
    return firstValueFrom(this.http.post<LoginResponse>(url, formData));
  }

  async getTfaProviders(regToken: string): Promise<TfaProvidersResponse> {
    const params = new HttpParams()
      .set('APIKey', this.apiKey)
      .set('regToken', regToken);
    const url = this.buildCdcUrl(CdcApiEndpoints.getTfaProviders, {
      dataCenter: this.dataCenter,
    });
    return firstValueFrom(this.http.get<TfaProvidersResponse>(url, { params }));
  }

  async initTfa(provider: string, regToken: string): Promise<InitTfaResponse> {
    const params = new HttpParams()
      .set('provider', provider)
      .set('mode', 'verify')
      .set('regToken', regToken)
      .set('APIKey', this.apiKey);
    const url = this.buildCdcUrl(CdcApiEndpoints.initTfa, {
      dataCenter: this.dataCenter,
    });

    return firstValueFrom(
      this.http.get<InitTfaResponse>(url, { params, withCredentials: true })
    );
  }

  async getTfaEmails(gigyaAssertion: string): Promise<TfaEmailsResponse> {
    const params = new HttpParams()
      .set('APIKey', this.apiKey)
      .set('gigyaAssertion', gigyaAssertion);
    const url = this.buildCdcUrl(CdcApiEndpoints.getTfaEmails, {
      dataCenter: this.dataCenter,
    });
    return firstValueFrom(
      this.http.get<TfaEmailsResponse>(url, { params, withCredentials: true })
    );
  }

  async sendTfaVerificationCode(
    emailID: string,
    assertion: string,
    regToken: string
  ): Promise<TfaSendCodeResponse> {
    const lang = await firstValueFrom(this.languageService.getActive());
    const payload = new HttpParams()
      .set('emailID', emailID)
      .set('gigyaAssertion', assertion)
      .set('lang', lang)
      .set('regToken', regToken)
      .set('APIKey', this.apiKey);
    const url = this.buildCdcUrl(CdcApiEndpoints.sendTfaVerificationCode, {
      dataCenter: this.dataCenter,
    });
    return firstValueFrom(
      this.http.post<TfaSendCodeResponse>(url, payload, {
        headers: new HttpHeaders({
          'Content-Type': 'application/x-www-form-urlencoded',
        }),
        withCredentials: true,
      })
    );
  }

  async completeTfaVerification(
    assertion: string,
    phvToken: string,
    otpCode: string,
    regToken: string
  ): Promise<CompleteVerificationResponse> {
    const formData: FormData = new FormData();
    formData.append('gigyaAssertion', assertion);
    formData.append('phvToken', phvToken);
    formData.append('code', otpCode);
    formData.append('regToken', regToken);
    formData.append('APIKey', this.apiKey);
    const url = this.buildCdcUrl(CdcApiEndpoints.completeTfaVerification, {
      dataCenter: this.dataCenter,
    });
    return firstValueFrom(
      this.http.post<CompleteVerificationResponse>(url, formData)
    );
  }

  async finalizeTfa(
    assertion: string,
    providerAssertion: string,
    regToken: string,
    rememberMe: boolean
  ): Promise<FinalizeTfaResponse> {
    const formData = new FormData();
    formData.append('gigyaAssertion', assertion);
    formData.append('providerAssertion', providerAssertion);
    formData.append('tempDevice', rememberMe.toString());
    formData.append('regToken', regToken);
    formData.append('APIKey', this.apiKey);
    const url = this.buildCdcUrl(CdcApiEndpoints.finalizeTfa, {
      dataCenter: this.dataCenter,
    });

    return firstValueFrom(this.http.post<FinalizeTfaResponse>(url, formData));
  }

  async finalizeRegistration(
    regToken: string
  ): Promise<FinalizeRegistrationResponse> {
    const include = this.cdcConfig.getIncludeParams();

    const formData: FormData = new FormData();
    formData.append('regToken', regToken);
    formData.append('include', include);
    formData.append('includeUserInfo', 'true');
    formData.append('APIKey', this.apiKey);
    const url = this.buildCdcUrl(CdcApiEndpoints.finalizeRegistration, {
      dataCenter: this.dataCenter,
    });

    return firstValueFrom(
      this.http.post<FinalizeRegistrationResponse>(url, formData, {
        withCredentials: true,
      })
    );
  }

  async requestPasswordReset(loginID: string): Promise<RequestPasswordReset> {
    const lang = await firstValueFrom(this.languageService.getActive());
    const formData: FormData = new FormData();
    formData.append('loginID', loginID);
    formData.append('lang', lang);
    formData.append('APIKey', this.apiKey);
    const url = this.buildCdcUrl(CdcApiEndpoints.resetPassword, {
      dataCenter: this.dataCenter,
    });
    return firstValueFrom(
      this.http.post<RequestPasswordReset>(url, formData, {
        withCredentials: true,
      })
    );
  }

  async resetPassword(
    token: string,
    newPassword: string
  ): Promise<ResetPassword> {
    const lang = await firstValueFrom(this.languageService.getActive());
    const formData: FormData = new FormData();
    formData.append('lang', lang);
    formData.append('APIKey', this.apiKey);
    formData.append('passwordResetToken', token);
    formData.append('newPassword', newPassword);
    const url = this.buildCdcUrl(CdcApiEndpoints.resetPassword, {
      dataCenter: this.dataCenter,
    });
    return firstValueFrom(
      this.http.post<ResetPassword>(url, formData, { withCredentials: true })
    );
  }

  async updatePassword(
    newPassword: string,
    currentPassword: string
  ): Promise<UpdatePassword> {
    const regToken = JSON.parse(localStorage.getItem('gigyaLoginToken') || '');
    const lang = await firstValueFrom(this.languageService.getActive());
    const formData: FormData = new FormData();
    formData.append('lang', lang);
    formData.append('APIKey', this.apiKey);
    formData.append('password', currentPassword);
    formData.append('newPassword', newPassword);
    formData.append('conflictHandling', 'fail');
    formData.append('regToken', regToken);
    formData.append('authMode', 'cookie');
    const url = this.buildCdcUrl(CdcApiEndpoints.setAccountInfo, {
      dataCenter: this.dataCenter,
    });

    return firstValueFrom(
      this.http.post<UpdatePassword>(url, formData, { withCredentials: true })
    );
  }
}
