import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  Inject,
  OnDestroy,
  OnInit,
  ViewChild,
  inject,
} from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { LAUNCH_CALLER, LaunchDialogService } from '@spartacus/storefront';
import {
  BehaviorSubject,
  catchError,
  distinctUntilChanged,
  EMPTY,
  from,
  map,
  Subject,
  switchMap,
  takeUntil,
  tap,
  withLatestFrom,
} from 'rxjs';

import {
  VerificationTokenCreation,
  VerificationTokenFacade,
} from '@spartacus/user/account/root';
import { GlobalMessageType, RoutingService } from '@spartacus/core';
import { AdnocOtploginComponentService } from './adnoc-otp-login-service';
import { ONE_TIME_PASSWORD_LOGIN_PURPOSE } from '../../constants/adnoc-user-account-constants';
import { DOCUMENT } from '@angular/common';
import { payerInfo } from '../../../core/model/adnoc-cart.model';
import { AdnocActiveCartService } from '../../cart/base/core/facade/adnoc-active-cart.service';
import { AdnocAuthService } from '../../../core/src/auth/user-auth/facade/adnoc-auth.service';
import { AdnocGlobalMessageService } from '../../../core/global-message/facade/adnoc-global-message.service';
import { CdcAuthService } from '../../cdc/cdc-auth.service';
import { CdcFlowState } from '../../cdc/cdc-model';

@Component({
  selector: 'adnoc-otp-login',
  templateUrl: './adnoc-otp-login.component.html',
  styleUrl: './adnoc-otp-login.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class AdnocOtploginComponent implements OnInit, OnDestroy {
  constructor(
    @Inject(DOCUMENT) private document: Document,
    protected CartPayerService: AdnocActiveCartService,
    protected globalMessageService: AdnocGlobalMessageService,
    protected cdcAuthService: CdcAuthService
  ) {}
  protected service: AdnocOtploginComponentService = inject(
    AdnocOtploginComponentService
  );
  protected launchDialogService: LaunchDialogService =
    inject(LaunchDialogService);
  protected cdr: ChangeDetectorRef = inject(ChangeDetectorRef);
  protected verificationTokenFacade = inject(VerificationTokenFacade);
  protected auth: AdnocAuthService = inject(AdnocAuthService);
  protected routingService: RoutingService = inject(RoutingService);
  protected busy$ = new BehaviorSubject(false);
  private payerList$ = new Subject<void>();
  waitTime: number = 60;
  loginForm: UntypedFormGroup = this.service.loginForm;
  OTP_form: UntypedFormGroup = this.service.OTP_form;
  @ViewChild('noReceiveCodeLink') element: ElementRef | undefined;
  @ViewChild('resendLink') resendLink: ElementRef | undefined;
  tokenId: string = '';
  tokenCode: string = '';
  target: string = '';
  password: string = '';
  isResendDisabled: boolean = false;
  disableButton: boolean = false;
  payers: payerInfo[] = [];
  destroy$ = new Subject<void>();

  private cdcFlowState: CdcFlowState = {
    loginResponse: null,
    regToken: '',
    tfaProvidersResponse: null,
    initTfaResponse: null,
    tfaEmailsResponse: null,
    tfaSendCodeResponse: null,
    completeVerificationResponse: null,
    finalizeTfaResponse: null,
    finalizeRegistrationResponse: null,
  };

  ngOnInit() {
    this.document.body.classList.add('loginPage', 'hide-header-footer');
    this.loginForm.enable();
    this.OTP_form.enable();
    this.OTP_form.reset();
    this.getPayerList();
    localStorage.removeItem('gigyaLoginToken');
  }

  // List of OTP control names
  otpControls = this.service.otpControlsData();

  onKeyUp(event: KeyboardEvent, controlId: string) {
    const input = event.target as HTMLInputElement;
    const nextControlId = this.getNextControlId(controlId, input.value);

    if (event.key === 'Backspace' || event.key === 'Delete') {
      // Move focus to previous field on backspace or delete
      this.focusPreviousControl(controlId);
    } else if (input.value) {
      // Move focus to next field on valid input
      this.focusControl(nextControlId);
    }
  }

  private focusControl(controlId: string) {
    const control = document.getElementById(controlId);
    if (control) {
      (control as HTMLInputElement).focus();
    }
  }

  private focusPreviousControl(currentControlId: string) {
    const previousControlId = this.getPreviousControlId(currentControlId);
    if (previousControlId) {
      this.focusControl(previousControlId);
    }
  }

  private getNextControlId(currentControlId: string, value: string): string {
    const formControls = Object.keys(this.OTP_form.controls);
    const currentIndex = formControls.indexOf(currentControlId);
    if (value && currentIndex < formControls.length - 1) {
      return formControls[currentIndex + 1];
    }
    return '';
  }

  private getPreviousControlId(currentControlId: string): string | undefined {
    const formControls = Object.keys(this.OTP_form.controls);
    const currentIndex = formControls.indexOf(currentControlId);
    if (currentIndex > 0) {
      return formControls[currentIndex - 1];
    }
    return undefined;
  }

  // Method to check if the control is filled
  isControlFilled(controlId: string): boolean {
    const control = this.OTP_form.get(controlId);
    return control?.value && control.value.length > 0;
  }

  onLoginSubmit(): void {
    if (!this.loginForm.valid) {
      this.loginForm.markAllAsTouched();
      return;
    }
    const email = this.loginForm.get('userId')?.value;
    const password = this.loginForm.get('password')?.value;
    this.busy$.next(true);
    //gigya Login Info Check
    this.gigyaLoginCheck(email, password);
  }

  async gigyaLoginCheck(email: string, password: string): Promise<void> {
    try {
      const response = await this.cdcAuthService.login(email, password);
      this.cdcFlowState.loginResponse = response;
      const { errorCode, UID, UIDSignature, signatureTimestamp, regToken } =
        response;
      if (errorCode === 0 && UID && UIDSignature && signatureTimestamp) {
        this.verifyGigyaLoginToken();
      } else if (response.errorCode === 206001 && regToken) {
        //Call Finalize method here, when we get error Registration was not finalized
        this.cdcFlowState.regToken = response.regToken;
        await this.finalizeRegistration();
      } else if (response.errorCode === 403101 && regToken) {
        this.cdcFlowState.regToken = response.regToken;
        await this.handleTfaProviderLookup(response.regToken);
      } else {
        this.showGlobalError(response);
      }
    } catch (error) {
      this.showGlobalError(error);
    }
  }

  async handleTfaProviderLookup(regToken: string): Promise<void> {
    try {
      const response = await this.cdcAuthService.getTfaProviders(regToken);
      this.cdcFlowState.tfaProvidersResponse = response;

      if (
        response?.errorCode === 0 &&
        Array.isArray(response.activeProviders) &&
        response.activeProviders.some((p: any) => p.name === 'gigyaEmail')
      ) {
        await this.initTfaFlow('gigyaEmail', regToken);
      } else {
        this.showGlobalError(response);
      }
    } catch (error) {
      this.showGlobalError(error);
    }
  }

  async initTfaFlow(
    provider: string,
    regToken: string,
    resendOtp: boolean = false
  ): Promise<void> {
    try {
      const response = await this.cdcAuthService.initTfa(provider, regToken);
      this.cdcFlowState.initTfaResponse = response;

      if (response?.errorCode === 0) {
        const assertion = response.gigyaAssertion || '';
        if (!resendOtp) {
          await this.getTfaEmailAddresses(assertion);
        } else {
          const emailId =
            this.cdcFlowState.tfaEmailsResponse?.emails?.[0]?.id || '';
          await this.handleSendOtpCode(emailId);
        }
      } else {
        this.showGlobalError(response);
      }
    } catch (error) {
      this.showGlobalError(error);
    }
  }

  async getTfaEmailAddresses(assertion: string): Promise<void> {
    try {
      const response = await this.cdcAuthService.getTfaEmails(assertion);
      this.cdcFlowState.tfaEmailsResponse = response;
      if (response?.errorCode === 0) {
        const emailList = response?.emails ?? [];
        const selectedEmailId = emailList[0]?.id;
        await this.handleSendOtpCode(selectedEmailId);
      } else {
        this.showGlobalError(response);
      }
    } catch (error) {
      this.showGlobalError(error);
    }
  }

  async handleSendOtpCode(emailID: string): Promise<void> {
    try {
      const assertion = this.cdcFlowState.initTfaResponse?.gigyaAssertion;
      const regToken = this.cdcFlowState.regToken;

      if (!assertion || !regToken || !emailID) {
        this.showGlobalError({
          key: 'loginTranslation.gigyaErrors.requiredFields',
        });
        return;
      }

      const response = await this.cdcAuthService.sendTfaVerificationCode(
        emailID,
        assertion,
        regToken
      );
      this.cdcFlowState.tfaSendCodeResponse = response;

      if (response?.errorCode === 0) {
        this.busy$.next(false);
        //Show OTP input UI here
        this.disableButton = true;
        this.loginForm.disable();
        this.cdr.detectChanges();
      } else {
        this.showGlobalError(response);
      }
    } catch (error) {
      this.showGlobalError(error);
    }
  }

  onOTPSubmit(): void {
    this.busy$.next(true);
    const otpValue = this.otpControls
      .map((control) => this.OTP_form.get(control)?.value)
      .join('');
    this.handleCompleteOtpVerification(otpValue);
  }

  async handleCompleteOtpVerification(otpCode: string): Promise<void> {
    try {
      const assertion = this.cdcFlowState.initTfaResponse?.gigyaAssertion;
      const regToken = this.cdcFlowState.regToken;
      const phvToken = this.cdcFlowState.tfaSendCodeResponse?.phvToken;

      if (!assertion || !regToken || !phvToken || !otpCode) {
        this.showGlobalError({ errorMessage: 'Missing required values' });
        return;
      }

      const response = await this.cdcAuthService.completeTfaVerification(
        assertion,
        phvToken,
        otpCode,
        regToken
      );

      this.cdcFlowState.completeVerificationResponse = response;

      if (response?.errorCode === 0) {
        await this.handleFinalizeTfa();
      } else {
        this.showGlobalError(response);
      }
    } catch (error) {
      this.showGlobalError(error);
    }
  }

  async handleFinalizeTfa(): Promise<void> {
    try {
      const assertion = this.cdcFlowState.initTfaResponse?.gigyaAssertion;
      const providerAssertion =
        this.cdcFlowState.completeVerificationResponse?.providerAssertion;
      const regToken = this.cdcFlowState.regToken;

      if (!assertion || !providerAssertion || !regToken) {
        this.showGlobalError({
          key: 'loginTranslation.gigyaErrors.requiredFields',
        });
        return;
      }
      const rememberMe = this.loginForm.get('rememberMe')?.value ?? true;
      const response = await this.cdcAuthService.finalizeTfa(
        assertion,
        providerAssertion,
        regToken,
        !rememberMe
      );

      this.cdcFlowState.finalizeTfaResponse = response;

      if (response?.errorCode === 0) {
        await this.finalizeRegistration();
      } else {
        this.showGlobalError(response);
      }
    } catch (error) {
      this.showGlobalError(error);
    }
  }

  async finalizeRegistration(): Promise<void> {
    try {
      const regToken = this.cdcFlowState.regToken;

      if (!regToken) {
        this.showGlobalError({
          key: 'loginTranslation.gigyaErrors.missingRegToken',
        });
        return;
      }
      const response = await this.cdcAuthService.finalizeRegistration(regToken);
      this.cdcFlowState.finalizeRegistrationResponse = response;

      if (response?.errorCode === 0) {
        // Login + 2FA + Registration flow complete!
        // TODO: Use response.profile or sessionInfo if needed
        this.verifyGigyaLoginToken();
      } else {
        this.showGlobalError(response);
      }
    } catch (error) {
      this.showGlobalError(error);
    }
  }

  verifyGigyaLoginToken() {
    const loginResp = this.cdcFlowState.loginResponse;
    const finalizeResp = this.cdcFlowState.finalizeRegistrationResponse;

    const hasAllValidValues =
      loginResp &&
      loginResp.UID &&
      loginResp.UIDSignature &&
      loginResp.UIDSignature.trim() !== '' &&
      loginResp.signatureTimestamp;

    const source = hasAllValidValues ? loginResp : finalizeResp;

    const payload = {
      UID: source?.UID || '',
      UIDSignature: source?.UIDSignature || '',
      signatureTimestamp: source?.signatureTimestamp || '',
    };
    this.service.verifyGigyaLoginToken(payload).subscribe({
      next: (res) => {
        if (res?.errorCode === '0') {
          localStorage.setItem(
            'gigyaLoginToken',
            JSON.stringify(source?.sessionInfo?.cookieValue)
          );
          this.loginWithCommerce();
        } else {
          this.showGlobalError(res);
        }
      },
      error: (err) => {
        this.showGlobalError(err);
      },
    });
  }

  loginWithCommerce() {
    from(
      this.auth.loginWithCredentials(
        // TODO: consider dropping toLowerCase as this should not be part of the UI,
        // as it's too opinionated and doesn't work with other AUTH services
        this.loginForm.get('userId')?.value.toLowerCase(),
        this.loginForm.get('password')?.value
      )
    )
      .pipe(
        catchError((error) => {
          this.globalMessageService.remove(GlobalMessageType.MSG_TYPE_ERROR);
          this.showGlobalError(error);
          return EMPTY;
        }),
        withLatestFrom(this.auth.isUserLoggedIn()),
        tap(([_, isLoggedIn]) => {
          this.onSuccess(isLoggedIn);
          if (isLoggedIn) {
            this.routingService.go({ cxRoute: 'home' });
            this.auth
              .isUserLoggedIn()
              .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
              .subscribe((data) => {
                if (data) {
                  this.payerList$.next();
                } else {
                  this.busy$.next(false);
                }
              });
          }
        })
      )
      .subscribe();
  }

  protected onSuccess(isLoggedIn: boolean): void {
    if (isLoggedIn) {
      // We want to remove error messages on successful login (primary the bad
      // username/password combination)
      this.globalMessageService.remove(GlobalMessageType.MSG_TYPE_ERROR);
      this.loginForm.reset();
    }
    this.busy$.next(false);
  }

  showGlobalError(error: any): void {
    this.busy$.next(false);
    let message: string | { key: string } = 'Something went wrong.';
    if (error?.errorCode === 403042) {
      message = { key: 'loginTranslation.invalidEmailOrPassword' };
    } else if (
      error?.errorDetails ||
      error?.errorMessage ||
      error?.error?.error_description
    ) {
      message =
        error.errorDetails ||
        error.errorMessage ||
        error.error.error_description;
    }
    this.globalMessageService.add(
      message,
      GlobalMessageType.MSG_TYPE_ERROR,
      15000
    );
  }

  removeUserData(): void {
    localStorage.removeItem('gigyaLoginToken');
  }

  protected collectDataFromLoginForm(): VerificationTokenCreation {
    return {
      loginId: this.loginForm.value.userId.toLowerCase(),
      password: this.loginForm.value.password,
      purpose: ONE_TIME_PASSWORD_LOGIN_PURPOSE,
    };
  }

  getPayerList() {
    this.payerList$
      .pipe(
        switchMap(() => this.service.getPayerOptions()),
        map((data) => data?.b2bUnitListData)
      )
      .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe((data) => {
        if (data) {
          this.payers = data;
          if (this.payers && this.payers.length > 1) {
            this.openPayerInfoDailog();
            this.service.payerInfo$.next(data);
            this.cdr.detectChanges();
          }
        }
      });
  }

  resendOTP(): void {
    this.isResendDisabled = true;
    this.cdr.detectChanges();
    if (this.resendLink) {
      this.resendLink.nativeElement.tabIndex = -1;
      this.resendLink.nativeElement.blur();
    }
    this.waitTime = 60;
    this.startWaitTimeInterval();
    let resendOtp = true;
    this.initTfaFlow('gigyaEmail', this.cdcFlowState.regToken, resendOtp);
  }

  startWaitTimeInterval(): void {
    const interval = setInterval(() => {
      this.waitTime--;
      this.cdr.detectChanges();
      if (this.waitTime <= 0) {
        clearInterval(interval);
        this.isResendDisabled = false;
        if (this.resendLink) {
          this.resendLink.nativeElement.tabIndex = 0;
        }
        this.cdr.detectChanges();
      }
    }, 1000);
  }

  openInfoDailog(): void {
    this.launchDialogService.openDialogAndSubscribe(
      LAUNCH_CALLER.ACCOUNT_VERIFICATION_TOKEN,
      this.element
    );
  }

  openPayerInfoDailog(): void {
    this.launchDialogService.openDialogAndSubscribe(
      'SUGGESTED_PAYERS',
      this.element
    );
  }

  onOpenInfoDailogKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      this.openInfoDailog();
    }
  }

  clearForm() {
    this.disableButton = false;
    this.loginForm.enable();
    this.OTP_form.enable();
    this.loginForm.reset();
    this.OTP_form.reset();
    this.removeUserData();
  }

  ngOnDestroy(): void {
    this.document.body.classList.remove('loginPage', 'hide-header-footer');
    this.destroy$.next();
    this.destroy$.complete();
  }
}
