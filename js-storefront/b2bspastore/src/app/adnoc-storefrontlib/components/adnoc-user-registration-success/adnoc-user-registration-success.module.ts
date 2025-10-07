import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdnocUserRegistrationSuccessComponent } from './adnoc-user-registration-success.component';
import { I18nModule, UrlModule } from '@spartacus/core';

@NgModule({
  declarations: [AdnocUserRegistrationSuccessComponent],
  imports: [CommonModule, I18nModule, UrlModule],
})
export class AdnocUserRegistrationSuccessModule {}
