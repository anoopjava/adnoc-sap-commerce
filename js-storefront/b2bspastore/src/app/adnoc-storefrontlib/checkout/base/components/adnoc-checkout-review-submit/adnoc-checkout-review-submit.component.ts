/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import {
  Cart,
  DeliveryMode,
  OrderEntry,
  PromotionLocation,
} from '@spartacus/cart/base/root';
import { CheckoutStep, CheckoutStepType } from '@spartacus/checkout/base/root';
import { Address, PaymentDetails, TranslationService } from '@spartacus/core';
import { deliveryAddressCard, deliveryModeCard } from '@spartacus/order/root';
import { Card, ICON_TYPE } from '@spartacus/storefront';
import { combineLatest, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { CheckoutStepService } from '../services/checkout-step.service';
import { AdnocCartOutlets } from '../../../../cart/base/root/models/cart-outlets.model';
import { CheckoutPaymentFacade } from '../../root/facade/checkout-payment.facade';
import { AdnocActiveCartFacade } from '../../../../cart/base/root/facade/adnoc-active-cart.facade';
import { CheckoutDeliveryAddressFacade } from '../../root/facade/checkout-delivery-address.facade';
import { CheckoutDeliveryModesFacade } from '../../root/facade/checkout-delivery-modes.facade';

@Component({
  selector: 'cx-review-submit',
  templateUrl: './adnoc-checkout-review-submit.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class AdnocCheckoutReviewSubmitComponent {
  readonly cartOutlets = AdnocCartOutlets;
  iconTypes = ICON_TYPE;

  checkoutStepTypeDeliveryAddress = CheckoutStepType.DELIVERY_ADDRESS;
  checkoutStepTypePaymentDetails = CheckoutStepType.PAYMENT_DETAILS;
  checkoutStepTypeDeliveryMode = CheckoutStepType.DELIVERY_MODE;

  promotionLocation: PromotionLocation = PromotionLocation.ActiveCart;

  steps$: Observable<CheckoutStep[]>;

  deliveryAddress$: Observable<Address | undefined>;

  deliveryMode$: Observable<DeliveryMode | undefined>;

  paymentDetails$: Observable<PaymentDetails | undefined>;

  constructor(
    protected checkoutDeliveryAddressFacade: CheckoutDeliveryAddressFacade,
    protected checkoutPaymentFacade: CheckoutPaymentFacade,
    protected activeCartFacade: AdnocActiveCartFacade,
    protected translationService: TranslationService,
    protected checkoutStepService: CheckoutStepService,
    protected checkoutDeliveryModesFacade: CheckoutDeliveryModesFacade
  ) {
    this.steps$ = this.checkoutStepService.steps$;
    this.deliveryAddress$ = this.checkoutDeliveryAddressFacade
      .getDeliveryAddressState()
      .pipe(
        filter((state) => !state.loading && !state.error),
        map((state) => state.data)
      );

    this.deliveryMode$ = this.checkoutDeliveryModesFacade
      .getSelectedDeliveryModeState()
      .pipe(
        filter((state) => !state.loading && !state.error),
        map((state) => state.data)
      );

    this.paymentDetails$ = this.checkoutPaymentFacade
      .getPaymentDetailsState()
      .pipe(
        filter((state) => !state.loading && !state.error),
        map((state) => state.data)
      );
  }

  get cart$(): Observable<Cart> {
    return this.activeCartFacade.getActive();
  }

  get entries$(): Observable<OrderEntry[]> {
    return this.activeCartFacade.getEntries();
  }

  protected getCheckoutDeliverySteps(): Array<CheckoutStepType | string> {
    return [CheckoutStepType.DELIVERY_ADDRESS, CheckoutStepType.DELIVERY_MODE];
  }

  protected getCheckoutPaymentSteps(): Array<CheckoutStepType | string> {
    return [
      CheckoutStepType.PAYMENT_DETAILS,
      CheckoutStepType.DELIVERY_ADDRESS,
    ];
  }

  getDeliveryAddressCard(
    deliveryAddress: Address,
    countryName?: string
  ): Observable<Card> {
    return combineLatest([
      this.translationService.translate('addressCard.shipTo'),
      this.translationService.translate('addressCard.phoneNumber'),
      this.translationService.translate('addressCard.mobileNumber'),
    ]).pipe(
      map(([textTitle, textPhone, textMobile]) =>
        deliveryAddressCard(
          textTitle,
          textPhone,
          textMobile,
          deliveryAddress,
          countryName
        )
      )
    );
  }

  getDeliveryModeCard(deliveryMode: DeliveryMode): Observable<Card> {
    return combineLatest([
      this.translationService.translate('checkoutMode.deliveryMethod'),
    ]).pipe(map(([textTitle]) => deliveryModeCard(textTitle, deliveryMode)));
  }

  getPaymentMethodCard(paymentDetails: PaymentDetails): Observable<Card> {
    return combineLatest([
      this.translationService.translate('paymentForm.payment'),
      this.translationService.translate('paymentCard.expires', {
        month: paymentDetails.expiryMonth,
        year: paymentDetails.expiryYear,
      }),
      this.translationService.translate('paymentForm.billingAddress'),
    ]).pipe(
      map(([textTitle, textExpires, billingAddress]) => {
        const region = paymentDetails.billingAddress?.region?.isocode
          ? paymentDetails.billingAddress?.region?.isocode + ', '
          : '';
        return {
          title: textTitle,
          textBold: paymentDetails.accountHolderName,
          text: [paymentDetails.cardNumber, textExpires],
          paragraphs: [
            {
              title: billingAddress + ':',
              text: [
                paymentDetails.billingAddress?.firstName +
                  ' ' +
                  paymentDetails.billingAddress?.lastName,
                paymentDetails.billingAddress?.line1,
                paymentDetails.billingAddress?.town +
                  ', ' +
                  region +
                  paymentDetails.billingAddress?.country?.isocode,
                paymentDetails.billingAddress?.postalCode,
              ],
            },
          ],
        } as Card;
      })
    );
  }

  getCheckoutStepUrl(stepType: CheckoutStepType | string): string | undefined {
    const step = this.checkoutStepService.getCheckoutStep(
      stepType as CheckoutStepType
    );
    return step?.routeName;
  }

  deliverySteps(steps: CheckoutStep[]): CheckoutStep[] {
    return steps.filter((step) =>
      this.getCheckoutDeliverySteps().includes(step.type[0])
    );
  }

  paymentSteps(steps: CheckoutStep[]): CheckoutStep[] {
    return steps.filter((step) =>
      this.getCheckoutPaymentSteps().includes(step.type[0])
    );
  }
}
