
import {
  Component,
  ChangeDetectionStrategy,
} from '@angular/core';
import { MyAccountV2DownloadInvoicesComponent } from '@spartacus/order/components';

@Component({
    selector: 'cx-my-account-v2-download-invoices',
    templateUrl: './adnoc-my-account-v2-download-invoices.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'adnoc-my-account-v2-download-invoices' },
    standalone: false
})
export class AdnocMyAccountV2DownloadInvoicesComponent extends MyAccountV2DownloadInvoicesComponent {}
