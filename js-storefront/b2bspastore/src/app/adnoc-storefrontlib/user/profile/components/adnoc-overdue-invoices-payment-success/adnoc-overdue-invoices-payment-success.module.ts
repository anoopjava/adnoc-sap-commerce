import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  CmsConfig,
  I18nModule,
  provideDefaultConfig,
  UrlModule,
} from '@spartacus/core';
import { AdnocOverdueInvoicesPaymentSuccessComponent } from './adnoc-overdue-invoices-payment-success.component';
import { RouterModule } from '@angular/router';
import { provideNativeDateAdapter } from '@angular/material/core';
import { StoreModule } from '@ngrx/store';
import { overdueInvoicePaymentReducer } from '../../../overdueInvoice-store/reducer/overdueInvoice.reducer';

@NgModule({
  declarations: [AdnocOverdueInvoicesPaymentSuccessComponent],
  imports: [
    CommonModule,
    I18nModule,
    UrlModule,
    RouterModule,
    StoreModule.forFeature(
      'overdueInvoicePaymentStatus',
      overdueInvoicePaymentReducer
    ),
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        overdueinvoiceSuccessComponent: {
          component: () =>
            import('./adnoc-overdue-invoices-payment-success.component').then(
              (m) => m.AdnocOverdueInvoicesPaymentSuccessComponent
            ),
        },
      },
    }),
    provideNativeDateAdapter(),
  ],
  exports: [AdnocOverdueInvoicesPaymentSuccessComponent],
})
export class AdnocOverdueInvoicesPaymentSuccessModule {}
