/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  ViewEncapsulation,
} from '@angular/core';
import { PaymentType } from '@spartacus/cart/base/root';
import { CheckoutStepType } from '@spartacus/checkout/base/root';
import {
  CostCenter,
  getLastValueSync,
  TranslationService,
  UserCostCenterService,
} from '@spartacus/core';
import { Card } from '@spartacus/storefront';
import { combineLatest, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { CheckoutStepService } from '../../../base/components/services';
import { AdnocCheckoutReviewSubmitComponent } from '../../../base/components/adnoc-checkout-review-submit/adnoc-checkout-review-submit.component';
import { AdnocActiveCartFacade } from '../../../../cart/base/root/facade/adnoc-active-cart.facade';
import { CheckoutPaymentTypeFacade } from '../../root/facade/checkout-payment-type.facade';
import { CheckoutCostCenterFacade } from '../../root/facade/checkout-cost-center.facade';
import { CheckoutDeliveryAddressFacade } from '../../../base/root/facade/checkout-delivery-address.facade';
import { CheckoutPaymentFacade } from '../../../base/root/facade/checkout-payment.facade';
import { CheckoutDeliveryModesFacade } from '../../../base/root/facade/checkout-delivery-modes.facade';
import { AdnocCart } from '../../assets/checkout/checkout-model';

@Component({
    selector: 'cx-review-submit',
    templateUrl: './adnoc-checkout-review-submit.component.html',
    styleUrl: './adnoc-checkout-review-submit.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
    encapsulation: ViewEncapsulation.None,
    standalone: false
})
export class AdnocB2BCheckoutReviewSubmitComponent extends AdnocCheckoutReviewSubmitComponent {
  checkoutStepTypePaymentType = CheckoutStepType.PAYMENT_TYPE;
  activeCart$: Observable<AdnocCart>;
  cartData;
  poNumberCart$: Observable<string>;
  paymentTypeCart$: Observable<any>;

  constructor(
    protected override checkoutDeliveryAddressFacade: CheckoutDeliveryAddressFacade,
    protected override checkoutPaymentFacade: CheckoutPaymentFacade,
    protected override activeCartFacade: AdnocActiveCartFacade,
    protected override translationService: TranslationService,
    protected override checkoutStepService: CheckoutStepService,
    protected override checkoutDeliveryModesFacade: CheckoutDeliveryModesFacade,
    protected checkoutPaymentTypeFacade: CheckoutPaymentTypeFacade,
    protected checkoutCostCenterFacade: CheckoutCostCenterFacade,
    protected userCostCenterService: UserCostCenterService
  ) {
    super(
      checkoutDeliveryAddressFacade,
      checkoutPaymentFacade,
      activeCartFacade,
      translationService,
      checkoutStepService,
      checkoutDeliveryModesFacade
    );
    this.activeCart$ = this.activeCartFacade.getActive();
    this.cartData = getLastValueSync(this.activeCart$);
    this.poNumberCart$ = this.activeCart$.pipe(
      map((cart) => cart?.purchaseOrderNumber ?? '')
    );
    this.paymentTypeCart$ = this.activeCart$.pipe(
      map((cart) => cart?.paymentType ?? '')
    );
  }

  get poNumber$(): Observable<string | undefined> {
    return this.checkoutPaymentTypeFacade.getPurchaseOrderNumberState().pipe(
      filter((state) => !state.loading && !state.error),
      map((state) => state.data)
    );
  }

  get paymentType$(): Observable<PaymentType | undefined> {
    return this.checkoutPaymentTypeFacade.getSelectedPaymentTypeState().pipe(
      filter((state) => !state.loading && !state.error),
      map((state) => state.data)
    );
  }

  get isAccountPayment$(): Observable<boolean> {
    return this.checkoutPaymentTypeFacade.isAccountPayment();
  }

  get costCenter$(): Observable<CostCenter | undefined> {
    return this.checkoutCostCenterFacade.getCostCenterState().pipe(
      filter((state) => !state.loading && !state.error),
      map((state) => state.data)
    );
  }

  protected override getCheckoutPaymentSteps(): Array<
    CheckoutStepType | string
  > {
    return [
      CheckoutStepType.PAYMENT_DETAILS,
      CheckoutStepType.PAYMENT_TYPE,
      CheckoutStepType.DELIVERY_ADDRESS,
    ];
  }

  getCostCenterCard(costCenter?: CostCenter): Observable<Card> {
    return combineLatest([
      this.translationService.translate('checkoutB2B.costCenter'),
    ]).pipe(
      map(([textTitle]) => {
        return {
          title: textTitle,
          textBold: costCenter?.name,
          text: ['(' + costCenter?.unit?.name + ')'],
        };
      })
    );
  }

  getPoNumberCard(poNumber?: string | null): Observable<Card> {
    return combineLatest([
      this.translationService.translate('checkoutTransaltions.review.poNumber'),
      this.translationService.translate('checkoutB2B.noPoNumber'),
    ]).pipe(
      map(([textTitle, noneTextTitle]) => {
        return {
          title: textTitle,
          textBold: poNumber ? poNumber : noneTextTitle,
        };
      })
    );
  }

  getPaymentTypeCard(paymentType: PaymentType): Observable<Card> {
    const creditLimit =
      this.cartData?.creditLimitUsed && paymentType.code != 'CREDIT_LIMIT'
        ? ', Credit Limit'
        : '';
    return combineLatest([
      this.translationService.translate(
        'checkoutTransaltions.progress.methodOfPayment'
      ),
      this.translationService.translate('payments.' + paymentType.code),
    ]).pipe(
      map(([textTitle, paymentTypeTranslation]) => {
        return {
          title: textTitle,
          textBold: paymentTypeTranslation + creditLimit,
        };
      })
    );
  }
}
