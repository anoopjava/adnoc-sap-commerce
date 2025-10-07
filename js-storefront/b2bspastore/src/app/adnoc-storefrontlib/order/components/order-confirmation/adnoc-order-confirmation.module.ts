/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { AbstractOrderContextModule } from '@spartacus/cart/base/components';
import {
  CmsConfig,
  FeaturesConfigModule,
  I18nModule,
  provideDefaultConfig,
} from '@spartacus/core';
import {
  OrderConfirmationTotalsComponent,
  OrderConfirmationShippingComponent,
  OrderConfirmationGuard,
  OrderOverviewComponent,
  OrderDetailBillingComponent,
  OrderConfirmationOrderEntriesContext,
  OrderDetailsService,
} from '@spartacus/order/components';
import {
  OrderConfirmationOrderEntriesContextToken,
  OrderFacade,
  OrderOutlets,
} from '@spartacus/order/root';
import {
  CardModule,
  FormErrorsModule,
  OutletModule,
  PasswordVisibilityToggleModule,
  PromotionsModule,
  PwaModule,
  provideOutlet,
} from '@spartacus/storefront';
import { AdnocOrderConfirmationThankYouMessageComponent } from './adnoc-order-confirmation-thank-you-message/adnoc-order-confirmation-thank-you-message.component';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    CommonModule,
    CardModule,
    RouterModule,
    PwaModule,
    PromotionsModule,
    I18nModule,
    ReactiveFormsModule,
    FormErrorsModule,
    OutletModule.forChild(),
    PasswordVisibilityToggleModule,
    AbstractOrderContextModule,
    FeaturesConfigModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        OrderConfirmationThankMessageComponent: {
          component: AdnocOrderConfirmationThankYouMessageComponent,
          guards: [OrderConfirmationGuard],
        },

        OrderConfirmationTotalsComponent: {
          component: OrderConfirmationTotalsComponent,
          guards: [OrderConfirmationGuard],
        },
        ReplenishmentConfirmationTotalsComponent: {
          component: OrderConfirmationTotalsComponent,
          guards: [OrderConfirmationGuard],
        },

        OrderConfirmationOverviewComponent: {
          component: OrderOverviewComponent,
          providers: [
            {
              provide: OrderDetailsService,
              useExisting: OrderFacade,
            },
          ],
          guards: [OrderConfirmationGuard],
        },
        ReplenishmentConfirmationOverviewComponent: {
          component: OrderOverviewComponent,
          providers: [
            {
              provide: OrderDetailsService,
              useExisting: OrderFacade,
            },
          ],
          guards: [OrderConfirmationGuard],
        },

        OrderConfirmationShippingComponent: {
          component: OrderConfirmationShippingComponent,
          guards: [OrderConfirmationGuard],
        },

        OrderConfirmationBillingComponent: {
          component: OrderDetailBillingComponent,
          providers: [
            {
              provide: OrderDetailsService,
              useExisting: OrderFacade,
            },
          ],
          guards: [OrderConfirmationGuard],
        },
      },
    }),
    {
      provide: OrderConfirmationOrderEntriesContextToken,
      useExisting: OrderConfirmationOrderEntriesContext,
    },
    provideOutlet({
      id: OrderOutlets.CONSIGNMENT_DELIVERY_INFO,
      component: OrderConfirmationShippingComponent,
    }),
  ],
  declarations: [AdnocOrderConfirmationThankYouMessageComponent],
  exports: [AdnocOrderConfirmationThankYouMessageComponent],
})
export class AdnocOrderConfirmationModule {}
