import { Injectable } from '@angular/core';
import { UntypedFormControl } from '@angular/forms';
import {
  GlobalMessageType,
  RouterState,
  RoutingService,
} from '@spartacus/core';
import { UserPasswordFacade } from '@spartacus/user/profile/root';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ResetPasswordComponentService } from '@spartacus/user/profile/components';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';
import { CdcAuthService } from '../../../../cdc/cdc-auth.service';

@Injectable()
export class AdnocResetPasswordComponentService extends ResetPasswordComponentService {
  apiKey$: Observable<string>;
  override resetToken$: Observable<string>;
  constructor(
    protected override userPasswordService: UserPasswordFacade,
    protected override routingService: RoutingService,
    protected override globalMessage: AdnocGlobalMessageService,
    protected cdcAuthService: CdcAuthService
  ) {
    super(userPasswordService, routingService, globalMessage);
    this.apiKey$ = this.routingService
      .getRouterState()
      .pipe(
        map(
          (routerState: RouterState) => routerState.state.queryParams['apiKey']
        )
      );
    this.resetToken$ = this.routingService
      .getRouterState()
      .pipe(
        map((routerState: RouterState) => routerState.state.queryParams['pwrt'])
      );
  }

  /**
   * Resets the password by the given token.
   *
   * The token has been provided during the request password flow.
   * The token is validated on the CIAM side.
   */
  override resetPassword(token: string): Promise<boolean> {
    return new Promise((resolve) => {
      if (!this.form.valid) {
        this.form.markAllAsTouched();
        return;
      }

      this.busy$.next(true);

      const password = (this.form.get('password') as UntypedFormControl).value;

      this.cdcAuthService
        .resetPassword(token, password)
        .then((response) => {
          if (response?.errorCode === 0) {
            this.onSuccess();
          } else {
            this.showGlobalError(response);
            resolve(false);
          }
          resolve(true);
        })
        .catch((error) => {
          this.showGlobalError(error);
          resolve(false);
        });
    });
  }

  showGlobalError(error: any): void {
    this.busy$.next(false);
    let message = 'Something went wrong.';
    if (error?.errorDetails || error?.errorMessage) {
      message = error.errorDetails || error.errorMessage;
    }
    this.globalMessage.add(message, GlobalMessageType.MSG_TYPE_ERROR);
  }
}
