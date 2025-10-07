import { LayoutConfig, DIALOG_TYPE } from '@spartacus/storefront';
import { AdnocTrackingEventsComponent } from './adnoc-consignment-tracking/adnoc-tracking-events/adnoc-tracking-events.component';

export const defaultConsignmentTrackingLayoutConfig: LayoutConfig = {
  launch: {
    CONSIGNMENT_TRACKING: {
      inlineRoot: true,
      component: AdnocTrackingEventsComponent,
      dialogType: DIALOG_TYPE.DIALOG,
    },
  },
};
