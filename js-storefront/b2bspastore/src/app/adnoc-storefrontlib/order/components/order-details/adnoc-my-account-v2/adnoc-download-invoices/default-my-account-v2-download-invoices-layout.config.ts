import { DIALOG_TYPE, LayoutConfig } from '@spartacus/storefront';
import { AdnocMyAccountV2DownloadInvoicesComponent } from './adnoc-my-account-v2-download-invoices.component';

export const defaultMyAccountV2DownloadInvoicesLayoutConfig: LayoutConfig = {
  launch: {
    DOWNLOAD_ORDER_INVOICES: {
      inlineRoot: true,
      component: AdnocMyAccountV2DownloadInvoicesComponent,
      dialogType: DIALOG_TYPE.DIALOG,
    },
  },
};
