/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

//import { CheckoutStepType } from '../model/checkout-step.model';
import { CheckoutStepType } from '@spartacus/checkout/base/root';
import { CheckoutConfig, DeliveryModePreferences } from './checkout-config';

export const defaultCheckoutConfig: CheckoutConfig = {
  checkout: {
    steps: [
      // {
      //   id: 'deliveryAddress',
      //   name: 'checkoutProgress.deliveryAddress',
      //   routeName: 'checkoutDeliveryAddress',
      //   type: [CheckoutStepType.DELIVERY_ADDRESS],
      //   disabled: true,
      // },
      // {
      //   id: 'deliveryMode',
      //   name: 'checkoutProgress.deliveryMode',
      //   routeName: 'checkoutDeliveryMode',
      //   type: [CheckoutStepType.DELIVERY_MODE],
      //   disabled: true,
      // },
      // {
      //   id: 'paymentDetails',
      //   name: 'checkoutProgress.paymentDetails',
      //   routeName: 'checkoutPaymentDetails',
      //   type: [CheckoutStepType.PAYMENT_DETAILS],
      // },
      {
        id: 'reviewOrder',
        name: 'checkoutProgress.reviewOrder',
        routeName: 'checkoutReviewOrder',
        type: [CheckoutStepType.REVIEW_ORDER],
      },
    ],
    express: false,
    defaultDeliveryMode: [DeliveryModePreferences.FREE],
    guest: false,
  },
};
