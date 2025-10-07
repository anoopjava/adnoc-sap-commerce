import { CommonModule } from '@angular/common';
import { inject, NgModule } from '@angular/core';
import { I18nModule, provideDefaultConfig } from '@spartacus/core';
import { PDFInvoicesComponentsModule } from '@spartacus/pdf-invoices/components';
import {
  IconModule,
  KeyboardFocusModule,
  PaginationModule,
  SortingModule,
  SpinnerModule,
} from '@spartacus/storefront';
import { defaultMyAccountV2DownloadInvoicesLayoutConfig } from './default-my-account-v2-download-invoices-layout.config';
import { AdnocMyAccountV2DownloadInvoicesEventListener } from './adnoc-my-account-v2-download-invoices-event.listener';
import { AdnocMyAccountV2DownloadInvoicesComponent } from './adnoc-my-account-v2-download-invoices.component';

@NgModule({
  imports: [
    CommonModule,
    KeyboardFocusModule,
    IconModule,
    I18nModule,
    PaginationModule,
    SortingModule,
    SpinnerModule,
    PDFInvoicesComponentsModule,
  ],
  providers: [
    provideDefaultConfig(defaultMyAccountV2DownloadInvoicesLayoutConfig),
  ],
  exports: [AdnocMyAccountV2DownloadInvoicesComponent],
  declarations: [AdnocMyAccountV2DownloadInvoicesComponent],
})
export class AdnocMyAccountV2DownloadInvoicesModule {
  protected downloadInvoicesDialogEventListener = inject(
    AdnocMyAccountV2DownloadInvoicesEventListener
  );
}
