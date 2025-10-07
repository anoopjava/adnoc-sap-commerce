/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import {
  Address,
  TranslationService,
  UserAddressService,
  UserCostCenterService,
} from '@spartacus/core';
import { Card } from '@spartacus/storefront';
import { combineLatest, Observable, of, Subscription } from 'rxjs';
import { distinctUntilChanged, filter, map, switchMap } from 'rxjs/operators';
import { CheckoutStepService } from '../../../base/components/services';
import { CheckoutDeliveryAddressComponent } from '../../../base/components/adnoc-checkout-delivery-address/checkout-delivery-address.component';
import { AdnocActiveCartFacade } from '../../../../cart/base/root/facade/adnoc-active-cart.facade';
import { CheckoutPaymentTypeFacade } from '../../root/facade/checkout-payment-type.facade';
import { CheckoutCostCenterFacade } from '../../root/facade/checkout-cost-center.facade';
import { CheckoutDeliveryAddressFacade } from '../../../base/root/facade/checkout-delivery-address.facade';
import { CheckoutDeliveryModesFacade } from '../../../base/root/facade/checkout-delivery-modes.facade';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

export interface CardWithAddress {
  card: Card;
  address: Address;
}

@Component({
  selector: 'cx-delivery-address',
  templateUrl: './checkout-delivery-address.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class B2BCheckoutDeliveryAddressComponent
  extends CheckoutDeliveryAddressComponent
  implements OnInit, OnDestroy
{
  protected subscriptions = new Subscription();

  protected isAccountPayment$: Observable<boolean>;

  protected costCenterAddresses$: Observable<Address[]>;

  protected creditCardAddressLoading$: Observable<boolean> =
    super.getAddressLoading();

  protected accountAddressLoading$: Observable<boolean>;

  isAccountPayment = false;

  constructor(
    protected override userAddressService: UserAddressService,
    protected override checkoutDeliveryAddressFacade: CheckoutDeliveryAddressFacade,
    protected override activatedRoute: ActivatedRoute,
    protected override translationService: TranslationService,
    protected override activeCartFacade: AdnocActiveCartFacade,
    protected override checkoutStepService: CheckoutStepService,
    protected override checkoutDeliveryModesFacade: CheckoutDeliveryModesFacade,
    protected override globalMessageService: AdnocGlobalMessageService,
    protected checkoutCostCenterFacade: CheckoutCostCenterFacade,
    protected checkoutPaymentTypeFacade: CheckoutPaymentTypeFacade,
    protected userCostCenterService: UserCostCenterService
  ) {
    super(
      userAddressService,
      checkoutDeliveryAddressFacade,
      activatedRoute,
      translationService,
      activeCartFacade,
      checkoutStepService,
      checkoutDeliveryModesFacade,
      globalMessageService
    );

    this.isAccountPayment$ = this.checkoutPaymentTypeFacade
      .isAccountPayment()
      .pipe(distinctUntilChanged());

    this.costCenterAddresses$ = this.checkoutCostCenterFacade
      .getCostCenterState()
      .pipe(
        filter((state) => !state.loading),
        map((state) => state.data),
        distinctUntilChanged((prev, curr) => prev?.code === curr?.code),
        switchMap((costCenter) => {
          this.doneAutoSelect = false;
          return costCenter?.code
            ? this.userCostCenterService.getCostCenterAddresses(costCenter.code)
            : of([]);
        })
      );

    this.accountAddressLoading$ = combineLatest([
      this.creditCardAddressLoading$,
      this.checkoutCostCenterFacade
        .getCostCenterState()
        .pipe(map((state) => state.loading)),
    ]).pipe(
      map(
        ([creditCardAddressLoading, costCenterLoading]) =>
          creditCardAddressLoading || costCenterLoading
      ),
      distinctUntilChanged()
    );
  }

  override ngOnInit(): void {
    this.subscriptions.add(
      this.isAccountPayment$.subscribe(
        (isAccount) => (this.isAccountPayment = isAccount)
      )
    );

    super.ngOnInit();
  }

  protected override loadAddresses(): void {
    if (!this.isAccountPayment) {
      super.loadAddresses();
    }
    // else: do nothing, as we don't need to load user addresses for account payment
  }

  protected override getAddressLoading(): Observable<boolean> {
    return this.isAccountPayment$.pipe(
      switchMap((isAccountPayment) =>
        isAccountPayment
          ? this.accountAddressLoading$
          : this.creditCardAddressLoading$
      )
    );
  }

  protected override getSupportedAddresses(): Observable<Address[]> {
    return this.isAccountPayment$.pipe(
      switchMap((isAccountPayment) =>
        isAccountPayment
          ? this.costCenterAddresses$
          : super.getSupportedAddresses()
      )
    );
  }

  protected override selectDefaultAddress(
    addresses: Address[],
    selected: Address | undefined
  ): void {
    if (
      !this.doneAutoSelect &&
      addresses?.length &&
      (!selected || Object.keys(selected).length === 0)
    ) {
      if (this.isAccountPayment) {
        if (addresses.length === 1) {
          this.setAddress(addresses[0]);
        }
      } else {
        super.selectDefaultAddress(addresses, selected);
      }
      this.doneAutoSelect = true;
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
