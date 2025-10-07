import { Injectable, inject } from '@angular/core';
import {
  UntypedFormControl,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import {
  GlobalMessageType,
  OccEndpointsService,
  RoutingService,
} from '@spartacus/core';
import { CustomFormValidators } from '@spartacus/storefront';
import { VerificationTokenFacade } from '@spartacus/user/account/root';
import { BehaviorSubject, EMPTY, from, Observable } from 'rxjs';
import { catchError, tap, withLatestFrom } from 'rxjs/operators';
import { AdnocApiService } from '../../services/apiServices/adnoc-api.service';
import { AdnocActiveCartService } from '../../cart/base/core/facade/adnoc-active-cart.service';
import { IPayer, payerInfo } from '../../../core/model/adnoc-cart.model';
import { AdnocAuthService } from '../../../core/src/auth/user-auth/facade/adnoc-auth.service';
import { AdnocGlobalMessageService } from '../../../core/global-message/facade/adnoc-global-message.service';
import { AdnocApiEndpoints } from '../../services/apiServices/adnoc-api-endpoints';
import { HttpClient } from '@angular/common/http';

const globalMsgShowTime: number = 10000;
@Injectable({
  providedIn: 'root',
})
export class AdnocOtploginComponentService {
  loginForm: UntypedFormGroup = new UntypedFormGroup({
    userId: new UntypedFormControl('', [
      Validators.required,
      CustomFormValidators.emailValidator,
    ]),
    password: new UntypedFormControl('', Validators.required),
    rememberMe: new UntypedFormControl(false),
  });
  OTP_form: UntypedFormGroup;
  otpControls: string[] = ['otp1', 'otp2', 'otp3', 'otp4', 'otp5', 'otp6'];
  payerInfo$ = new BehaviorSubject<payerInfo[]>([]);
  constructor(
    protected adnocApiService: AdnocApiService,
    private readonly OccEndpointsService: OccEndpointsService,
    private http: HttpClient
  ) {
    const otpFormControls = this.otpControls.reduce((controls: any, otp) => {
      controls[otp] = new UntypedFormControl('', [
        Validators.required,
        Validators.minLength(1),
        Validators.maxLength(1),
      ]);
      return controls;
    }, {});

    this.OTP_form = new UntypedFormGroup({
      //tokenId: new UntypedFormControl('', [Validators.required]),
      ...otpFormControls,
    });
  }
  protected globalMessage: AdnocGlobalMessageService = inject(
    AdnocGlobalMessageService
  );
  protected verificationTokenFacade: VerificationTokenFacade = inject(
    VerificationTokenFacade
  );
  protected auth: AdnocAuthService = inject(AdnocAuthService);
  protected CartPayerService: AdnocActiveCartService = inject(
    AdnocActiveCartService
  );
  protected routingService: RoutingService = inject(RoutingService);
  protected busy$ = new BehaviorSubject(false);

  isUpdating$ = this.busy$.pipe(
    tap((state) => {
      state === true ? this.OTP_form.disable() : this.OTP_form.enable();
    })
  );

  otpControlsData() {
    return this.otpControls;
  }

  login() {
    const otpValue = this.otpControls
      .map((control) => this.OTP_form.get(control)?.value)
      .join('');
    if (!this.OTP_form.valid) {
      this.OTP_form.markAllAsTouched();
      return;
    }
    this.busy$.next(true);

    from(
      this.auth.otpLoginWithCredentials(this.OTP_form.value.tokenId, otpValue)
    )
      .pipe(
        withLatestFrom(this.auth.isUserLoggedIn()),
        tap(([_, isLoggedIn]) => this.onSuccess(isLoggedIn)),
        catchError((error) => {
          this.globalMessage.remove(GlobalMessageType.MSG_TYPE_ERROR);
          this.busy$.next(false);
          const backendMessage =
            error?.error?.error_description ||
            error?.error?.error ||
            'Login failed. Please try again.';

          this.globalMessage.add(
            { raw: backendMessage },
            GlobalMessageType.MSG_TYPE_ERROR
          );
          return EMPTY;
        })
      )
      .subscribe();
  }

  displayMessage(key: string, params: Object) {
    this.globalMessage.add(
      {
        key: key,
        params,
      },
      GlobalMessageType.MSG_TYPE_CONFIRMATION,
      globalMsgShowTime
    );
  }

  createVerificationToken(loginId: string, password: string, purpose: string) {
    return this.verificationTokenFacade.createVerificationToken({
      loginId,
      password,
      purpose,
    });
  }

  protected onSuccess(isLoggedIn: boolean): void {
    if (isLoggedIn) {
      // We want to remove error messages on successful login (primary the bad
      // username/password combination)
      this.globalMessage.remove(GlobalMessageType.MSG_TYPE_ERROR);
      this.loginForm.reset();
      this.OTP_form.reset();
      this.routingService.go({ cxRoute: 'home' });
    }
    this.busy$.next(false);
  }

  /* loading the payer service from API and sharing to child components */
  getPayerOptions(): Observable<IPayer> {
    return this.CartPayerService.getPayersList();
  }

  verifyGigyaLoginToken(payload: {
    UID: string;
    UIDSignature: string;
    signatureTimestamp: string;
  }): Observable<any> {
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.verifyGigyaLoginToken
    );

    return this.http.post<any>(url, payload);
  }
}
