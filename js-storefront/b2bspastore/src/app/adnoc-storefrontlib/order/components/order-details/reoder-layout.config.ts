import { DIALOG_TYPE, LayoutConfig } from '@spartacus/storefront';
import { AdnocReorderDialogComponent } from './adnoc-order-detail-reorder/adnoc-reorder-dialog/adnoc-reorder-dialog.component';

export const defaultReorderLayoutConfig: LayoutConfig = {
  launch: {
    REORDER: {
      inline: true,
      component: AdnocReorderDialogComponent,
      dialogType: DIALOG_TYPE.DIALOG,
    },
  },
};
