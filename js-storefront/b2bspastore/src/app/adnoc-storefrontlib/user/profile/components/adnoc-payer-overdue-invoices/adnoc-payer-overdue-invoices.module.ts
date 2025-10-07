import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  CmsConfig,
  FeaturesConfigModule,
  I18nModule,
  provideDefaultConfig,
  UrlModule,
} from '@spartacus/core';
import { RouterModule } from '@angular/router';
import { AdnocPayerOverdueInvoicesComponent } from './adnoc-payer-overdue-invoices.component';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatTableModule } from '@angular/material/table';
import { IconModule, PopoverModule, SpinnerModule } from '@spartacus/storefront';
import { FormsModule } from '@angular/forms';
import { AdnocOverdueInvoiceFacade } from '../../../core-overdueInvoice/facade/adnoc-overdue-invoice.facade';
import { AdnocOverdueInvoiceService } from '../../../core-overdueInvoice/service/adnoc-overdue-invoice.service';

@NgModule({
  declarations: [AdnocPayerOverdueInvoicesComponent],
  imports: [
    CommonModule,
    I18nModule,
    UrlModule,
    RouterModule,
    UrlModule,
    FeaturesConfigModule,
    MatTableModule,
    MatCheckboxModule,
    SpinnerModule,
    MatTableModule,
    MatCheckboxModule,
    FormsModule,
    PopoverModule,
    IconModule
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        overDueInvoiceComponent: {
          component: () =>
            import('./adnoc-payer-overdue-invoices.component').then(
              (m) => m.AdnocPayerOverdueInvoicesComponent
            ),
        },
      },
    }),
    {
      provide: AdnocOverdueInvoiceFacade,
      useExisting: AdnocOverdueInvoiceService,
    },
  ],
  exports: [AdnocPayerOverdueInvoicesComponent],
})
export class AdnocPayerOverdueInvoicesModule {}
