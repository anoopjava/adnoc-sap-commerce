import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  CmsConfig,
  FeaturesConfigModule,
  I18nModule,
  provideDefaultConfig,
  UrlModule,
} from '@spartacus/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { provideNativeDateAdapter } from '@angular/material/core';
import { RouterModule } from '@angular/router';
import { AdnocStatementOfAccount } from './adnoc-statement-of-account.component';

@NgModule({
  declarations: [AdnocStatementOfAccount],
  imports: [
    CommonModule,
    I18nModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    UrlModule,
    RouterModule,
    UrlModule,
    FeaturesConfigModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        downloadStatementComponent: {
          component: () =>
            import('./adnoc-statement-of-account.component').then(
              (m) => m.AdnocStatementOfAccount
            ),
        },
      },
    }),
    provideNativeDateAdapter(),
  ],
  exports: [AdnocStatementOfAccount],
})
export class AdnocStatementOfAccountModule {}
