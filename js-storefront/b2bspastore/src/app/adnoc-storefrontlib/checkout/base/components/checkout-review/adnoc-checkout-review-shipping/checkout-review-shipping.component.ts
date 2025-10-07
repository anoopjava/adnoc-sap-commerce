/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import {
  CartOutlets,
  DeliveryMode,
  OrderEntry,
} from '@spartacus/cart/base/root';
import {
  CheckoutStepType,
} from '@spartacus/checkout/base/root';
import {
  Address,
  FeatureConfigService,
  TranslationService,
} from '@spartacus/core';
import { deliveryAddressCard, deliveryModeCard } from '@spartacus/order/root';
import { Card, ICON_TYPE } from '@spartacus/storefront';
import { combineLatest, Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { CheckoutStepService } from '../../services/checkout-step.service';
import { AdnocCartOutlets } from '../../../../../cart/base/root/models/cart-outlets.model';
import { AdnocActiveCartFacade } from '../../../../../cart/base/root/facade/adnoc-active-cart.facade';
import { CheckoutDeliveryAddressFacade } from '../../../root/facade/checkout-delivery-address.facade';
import { CheckoutDeliveryModesFacade } from '../../../root/facade/checkout-delivery-modes.facade';

@Component({
    selector: 'cx-checkout-review-shipping',
    templateUrl: './checkout-review-shipping.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CheckoutReviewShippingComponent {
  protected featureConfig = inject(FeatureConfigService);
  private showDeliveryOptionsTranslation = this.featureConfig.isEnabled(
    'showDeliveryOptionsTranslation'
  );
  readonly cartOutlets = AdnocCartOutlets;
  iconTypes = ICON_TYPE;
  deliveryAddressStepRoute!: string | undefined;
  deliveryModeStepRoute!: string | undefined;
  entries$: Observable<OrderEntry[]>;

  deliveryAddress$: Observable<Address | undefined>;
  deliveryMode$: Observable<DeliveryMode | undefined>;
 
  constructor(
    protected activeCartFacade: AdnocActiveCartFacade,
    protected checkoutDeliveryModesFacade: CheckoutDeliveryModesFacade,
    protected checkoutDeliveryAddressFacade: CheckoutDeliveryAddressFacade,
    protected translationService: TranslationService,
    protected checkoutStepService: CheckoutStepService
  ) {
    this.deliveryAddressStepRoute = this.checkoutStepService.getCheckoutStepRoute(
      CheckoutStepType.DELIVERY_ADDRESS
    );
    this.deliveryModeStepRoute = this.checkoutStepService.getCheckoutStepRoute(
      CheckoutStepType.DELIVERY_MODE
    );

    this.entries$ = this.activeCartFacade.getDeliveryEntries();

    this.deliveryAddress$ =
    this.checkoutDeliveryAddressFacade.getDeliveryAddressState().pipe(
      filter((state) => !state.loading && !state.error),
      map((state) => state.data)
    );

    this.deliveryAddress$ =
    this.checkoutDeliveryAddressFacade.getDeliveryAddressState().pipe(
      filter((state) => !state.loading && !state.error),
      map((state) => state.data)
    );

  this.deliveryMode$ =
    this.checkoutDeliveryModesFacade.getSelectedDeliveryModeState().pipe(
      filter((state) => !state.loading && !state.error),
      map((state) => state.data)
    );
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
      this.translationService.translate(
        this.showDeliveryOptionsTranslation
          ? 'checkoutMode.deliveryOptions'
          : 'checkoutMode.deliveryMethod'
      ),
    ]).pipe(map(([textTitle]) => deliveryModeCard(textTitle, deliveryMode)));
  }
}
