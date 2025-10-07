import { Injectable } from '@angular/core';
import { AuthActions, AuthService, OCC_USER_ID_CURRENT } from '@spartacus/core';
import { lastValueFrom } from 'rxjs';

/**
 * Auth service for normal user authentication.
 * Use to check auth status, login/logout with different OAuth flows.
 */
@Injectable({
  providedIn: 'root',
})
export class AdnocAuthService extends AuthService {
  /**
   * Loads a new user token with otp tokenCode and otp tokenId.
   * @param tokenId
   * @param tokenCode
   */
  override async otpLoginWithCredentials(
    tokenId: string,
    tokenCode: string
  ): Promise<void> {
    try {
      await this.oAuthLibWrapperService.authorizeWithPasswordFlow(
        tokenId,
        tokenCode
      );

      // OCC specific user id handling. Customize when implementing different backend
      this.userIdService.setUserId(OCC_USER_ID_CURRENT);

      this.store.dispatch(new AuthActions.Login());

      this.authRedirectService.redirect();
    } catch (error) {
      throw error;
    }
  }

  override async loginWithCredentials(
    userId: string,
    password: string
  ): Promise<void> {
    let uid = userId;
    if (this.authMultisiteIsolationService) {
      uid = await lastValueFrom(
        this.authMultisiteIsolationService.decorateUserId(uid)
      );
    }

    try {
      await this.oAuthLibWrapperService.authorizeWithPasswordFlow(
        uid,
        password
      );

      // OCC specific user id handling. Customize when implementing different backend
      this.userIdService.setUserId(OCC_USER_ID_CURRENT);

      this.store.dispatch(new AuthActions.Login());

      this.authRedirectService.redirect();
    } catch (error) {
      throw error;
    }
  }
}
