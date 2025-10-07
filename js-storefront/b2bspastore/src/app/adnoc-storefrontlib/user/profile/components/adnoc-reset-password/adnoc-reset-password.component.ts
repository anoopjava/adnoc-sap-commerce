import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { useFeatureStyles } from '@spartacus/core';
import { AdnocResetPasswordComponentService } from './adnoc-reset-password-component.service';
import { ResetPasswordComponent } from '@spartacus/user/profile/components';

@Component({
  selector: 'cx-reset-password',
  templateUrl: './adnoc-reset-password.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'user-form adnoc-reset-password' },
  standalone: false,
})
export class AdnocResetPasswordComponent extends ResetPasswordComponent {
  constructor(
    @Inject(DOCUMENT) private document: Document,
    protected override service: AdnocResetPasswordComponentService
  ) {
    useFeatureStyles('a11yPasswordVisibliltyBtnValueOverflow');
    super(service);
    this.token$ = this.service.resetToken$;
  }

  ngOnInit() {
    this.document.body.classList.add('resetPasswordPage');
  }

  override onSubmit(token: string) {
    this.service.resetPassword(token);
  }
}
