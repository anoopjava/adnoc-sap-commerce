/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Provider } from '@angular/core';
import { CheckoutCostCenterService } from './checkout-cost-center.service';
import { CheckoutPaymentTypeService } from './checkout-payment-type.service';
import { CheckoutPaymentTypeFacade } from '../../root/facade/checkout-payment-type.facade';
import { CheckoutCostCenterFacade } from '../../root/facade/checkout-cost-center.facade';

export const facadeProviders: Provider[] = [
  CheckoutCostCenterService,
  {
    provide: CheckoutCostCenterFacade,
    useExisting: CheckoutCostCenterService,
  },
  CheckoutPaymentTypeService,
  {
    provide: CheckoutPaymentTypeFacade,
    useExisting: CheckoutPaymentTypeService,
  },
];
