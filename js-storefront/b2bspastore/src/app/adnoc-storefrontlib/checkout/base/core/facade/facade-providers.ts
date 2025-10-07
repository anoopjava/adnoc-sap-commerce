/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Provider } from '@angular/core';
import { CheckoutPaymentService } from './checkout-payment.service';
import { CheckoutQueryService } from './checkout-query.service';
import { CheckoutQueryFacade } from '../../root/facade/checkout-query.facade';
import { CheckoutDeliveryAddressFacade } from '../../root/facade/checkout-delivery-address.facade';
import { CheckoutDeliveryAddressService } from './checkout-delivery-address.service';
import { CheckoutDeliveryModesService } from './checkout-delivery-modes.service';
import { CheckoutDeliveryModesFacade } from '../../root/facade/checkout-delivery-modes.facade';
import { CheckoutPaymentFacade } from '../../root/facade/checkout-payment.facade';

export const facadeProviders: Provider[] = [
  CheckoutDeliveryAddressService,
  {
    provide: CheckoutDeliveryAddressFacade,
    useExisting: CheckoutDeliveryAddressService,
  },
  CheckoutDeliveryModesService,
  {
    provide: CheckoutDeliveryModesFacade,
    useExisting: CheckoutDeliveryModesService,
  },
  CheckoutPaymentService,
  {
    provide: CheckoutPaymentFacade,
    useExisting: CheckoutPaymentService,
  },
  CheckoutQueryService,
  {
    provide: CheckoutQueryFacade,
    useExisting: CheckoutQueryService,
  },
];
