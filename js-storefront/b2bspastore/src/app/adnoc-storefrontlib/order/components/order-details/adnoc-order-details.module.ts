import { CommonModule } from '@angular/common';
import { ComponentFactoryResolver, inject, NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { AbstractOrderContextModule } from '@spartacus/cart/base/components';
import { AddToCartModule } from '@spartacus/cart/base/components/add-to-cart';
import {
  AuthGuard,
  CmsConfig,
  FeaturesConfig,
  FeaturesConfigModule,
  I18nModule,
  MODULE_INITIALIZER,
  provideDefaultConfig,
  provideDefaultConfigFactory,
  UrlModule,
} from '@spartacus/core';
import { OrderOutlets, USE_MY_ACCOUNT_V2_ORDER } from '@spartacus/order/root';
import {
  CardModule,
  IconModule,
  KeyboardFocusModule,
  OutletModule,
  OutletPosition,
  OutletService,
  PromotionsModule,
  ProvideOutletOptions,
  SpinnerModule,
} from '@spartacus/storefront';
import { defaultConsignmentTrackingLayoutConfig } from './adnoc-order-detail-items/default-consignment-tracking-layout.config';
import { defaultReorderLayoutConfig } from './reoder-layout.config';
import { AdnocOrderOverviewComponent } from './adnoc-order-overview/adnoc-order-overview.component';
import { AdnocOrderDetailActionsComponent } from './adnoc-order-detail-actions/adnoc-order-detail-actions.component';
import { OrderOverviewComponentService } from '@spartacus/order/components';
import { AdnocOrderDetailItemsComponent } from './adnoc-order-detail-items';
import { AdnocMyAccountV2OrderDetailsActionsComponent } from './adnoc-my-account-v2/adnoc-order-details-actions/adnoc-my-account-v2-order-details-actions.component';
import {
  AdnocMyAccountV2ConsignmentTrackingComponent,
  AdnocMyAccountV2DownloadInvoicesModule,
} from './adnoc-my-account-v2';
import { AdnocOrderDetailBillingComponent } from './adnoc-order-detail-billing/adnoc-order-detail-billing.component';
import { AdnocOrderDetailReorderComponent } from './adnoc-order-detail-reorder/adnoc-order-detail-reorder.component';
import { AdnocOrderConsignedEntriesComponent } from './adnoc-order-detail-items/adnoc-order-consigned-entries/adnoc-order-consigned-entries.component';
import { AdnocOrderDetailTotalsComponent } from './adnoc-order-detail-totals/adnoc-order-detail-totals.component';
import { AdnocReorderDialogComponent } from './adnoc-order-detail-reorder/adnoc-reorder-dialog/adnoc-reorder-dialog.component';
import { AdnocTrackingEventsComponent } from './adnoc-order-detail-items/adnoc-consignment-tracking/adnoc-tracking-events/adnoc-tracking-events.component';
import { AdnocConsignmentTrackingComponent } from './adnoc-order-detail-items/adnoc-consignment-tracking/adnoc-consignment-tracking.component';

function registerOrderOutletFactory(): () => void {
  const isMyAccountV2 = inject(USE_MY_ACCOUNT_V2_ORDER);
  const outletService = inject(OutletService);
  const componentFactoryResolver = inject(ComponentFactoryResolver);
  return () => {
    const config: ProvideOutletOptions = {
      component: AdnocMyAccountV2ConsignmentTrackingComponent,
      id: OrderOutlets.ORDER_CONSIGNMENT,
      position: OutletPosition.REPLACE,
    };
    if (isMyAccountV2) {
      const template = componentFactoryResolver.resolveComponentFactory(
        config.component
      );
      outletService.add(config.id, template, config.position);
    }
  };
}

const myAccountV2CmsMapping: CmsConfig = {
  cmsComponents: {
    AccountOrderDetailsActionsComponent: {
      component: AdnocMyAccountV2OrderDetailsActionsComponent,
      //guards: inherited from standard config,
    },
  },
};

const moduleComponents = [
  AdnocOrderOverviewComponent,
  AdnocOrderDetailActionsComponent,
  AdnocOrderDetailItemsComponent,
  AdnocOrderDetailTotalsComponent,
  AdnocOrderDetailBillingComponent,
  AdnocTrackingEventsComponent,
  AdnocConsignmentTrackingComponent,
  AdnocOrderConsignedEntriesComponent,
  AdnocOrderDetailReorderComponent,
  AdnocReorderDialogComponent,
  AdnocMyAccountV2OrderDetailsActionsComponent,
  AdnocMyAccountV2ConsignmentTrackingComponent,
];

@NgModule({
  imports: [
    CardModule,
    CommonModule,
    I18nModule,
    FeaturesConfigModule,
    PromotionsModule,
    UrlModule,
    SpinnerModule,
    RouterModule,
    OutletModule,
    AddToCartModule,
    KeyboardFocusModule,
    IconModule,
    AdnocMyAccountV2DownloadInvoicesModule,
    AbstractOrderContextModule,
  ],
  providers: [
    OrderOverviewComponentService,
    provideDefaultConfig(<CmsConfig | FeaturesConfig>{
      cmsComponents: {
        AccountOrderDetailsActionsComponent: {
          component: AdnocOrderDetailActionsComponent,
          guards: [AuthGuard],
        },
        AccountOrderDetailsItemsComponent: {
          component: AdnocOrderDetailItemsComponent,
          guards: [AuthGuard],
          data: {
            enableAddToCart: true,
          },
        },
        AccountOrderDetailsGroupedItemsComponent: {
          component: AdnocOrderDetailItemsComponent,
          guards: [AuthGuard],
          data: {
            enableAddToCart: true,
            groupCartItems: true,
          },
        },
        AccountOrderDetailsTotalsComponent: {
          component: AdnocOrderDetailTotalsComponent,
          guards: [AuthGuard],
        },
        AccountOrderDetailsOverviewComponent: {
          component: AdnocOrderOverviewComponent,
          guards: [AuthGuard],
        },
        AccountOrderDetailsSimpleOverviewComponent: {
          component: AdnocOrderOverviewComponent,
          guards: [AuthGuard],
          data: {
            simple: true,
          },
        },
        AccountOrderDetailsReorderComponent: {
          component: AdnocOrderDetailReorderComponent,
          guards: [AuthGuard],
        },
      },
      features: {
        consignmentTracking: '1.2',
      },
    }),
    provideDefaultConfig(defaultConsignmentTrackingLayoutConfig),
    provideDefaultConfig(defaultReorderLayoutConfig),
    provideDefaultConfigFactory(() =>
      inject(USE_MY_ACCOUNT_V2_ORDER) ? myAccountV2CmsMapping : {}
    ),
    {
      provide: MODULE_INITIALIZER,
      useFactory: registerOrderOutletFactory,
      multi: true,
    },
  ],
  declarations: [...moduleComponents],
  exports: [...moduleComponents],
})
export class AdnocOrderDetailsModule {}
