/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import {
  CheckoutStepType,
} from '@spartacus/checkout/base/root';
import { PaymentDetails, TranslationService } from '@spartacus/core';
import { billingAddressCard, paymentMethodCard } from '@spartacus/order/root';
import { Card, ICON_TYPE } from '@spartacus/storefront';
import { combineLatest, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { CheckoutStepService } from '../../services/checkout-step.service';
import { CheckoutPaymentFacade } from '../../../root/facade/checkout-payment.facade';

@Component({
    selector: 'cx-checkout-review-payment',
    templateUrl: './checkout-review-payment.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CheckoutReviewPaymentComponent {
  iconTypes = ICON_TYPE;
  paymentDetails$: Observable<PaymentDetails | undefined>;
  paymentDetailsStepRoute!: string | undefined;

  constructor(
    protected checkoutStepService: CheckoutStepService,
    protected checkoutPaymentFacade: CheckoutPaymentFacade,
    protected translationService: TranslationService
  ) {
    this.paymentDetails$ =
    this.checkoutPaymentFacade.getPaymentDetailsState().pipe(
      filter((state) => !state.loading && !state.error),
      map((state) => state.data)
    );
    this.paymentDetailsStepRoute = this.checkoutStepService.getCheckoutStepRoute(
      CheckoutStepType.PAYMENT_DETAILS
    );
  }

  getPaymentMethodCard(paymentDetails: PaymentDetails): Observable<Card> {
    return combineLatest([
      this.translationService.translate('paymentForm.payment'),
      this.translationService.translate('paymentCard.expires', {
        month: paymentDetails.expiryMonth,
        year: paymentDetails.expiryYear,
      }),
    ]).pipe(
      map(([textTitle, textExpires]) =>
        paymentMethodCard(textTitle, textExpires, paymentDetails)
      )
    );
  }

  getBillingAddressCard(paymentDetails: PaymentDetails): Observable<Card> {
    return combineLatest([
      this.translationService.translate('paymentForm.billingAddress'),
      this.translationService.translate('addressCard.billTo'),
    ]).pipe(
      map(([billingAddress, billTo]) =>
        billingAddressCard(billingAddress, billTo, paymentDetails)
      )
    );
  }
}
