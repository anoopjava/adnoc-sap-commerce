/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable, OnDestroy } from '@angular/core';
import {
  CurrencySetEvent,
  EventService,
  GlobalMessageType,
  LanguageSetEvent,
  LoadUserPaymentMethodsEvent,
  OCC_USER_ID_ANONYMOUS,
} from '@spartacus/core';
import { merge, Subscription } from 'rxjs';
import {
  CheckoutPaymentCardTypesQueryReloadEvent,
  CheckoutPaymentDetailsCreatedEvent,
  CheckoutPaymentDetailsSetEvent,
  CheckoutQueryResetEvent,
} from './checkout.events';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

/**
 * Checkout payment event listener.
 */
@Injectable({
  providedIn: 'root',
})
export class CheckoutPaymentEventListener implements OnDestroy {
  protected subscriptions = new Subscription();

  constructor(
    protected eventService: EventService,
    protected globalMessageService: AdnocGlobalMessageService
  ) {
    this.onPaymentCreated();
    this.onPaymentSet();

    this.onGetCardTypesQueryReload();
  }

  protected onPaymentCreated(): void {
    this.subscriptions.add(
      this.eventService
        .get(CheckoutPaymentDetailsCreatedEvent)
        .subscribe(({ userId }) => {
          if (userId !== OCC_USER_ID_ANONYMOUS) {
            this.eventService.dispatch({ userId }, LoadUserPaymentMethodsEvent);
          }

          this.globalMessageService.add(
            { key: 'paymentForm.paymentAddedSuccessfully' },
            GlobalMessageType.MSG_TYPE_CONFIRMATION
          );
          this.eventService.dispatch({}, CheckoutQueryResetEvent);
        })
    );
  }

  protected onPaymentSet(): void {
    this.subscriptions.add(
      this.eventService.get(CheckoutPaymentDetailsSetEvent).subscribe(() => {
        this.eventService.dispatch({}, CheckoutQueryResetEvent);
      })
    );
  }

  protected onGetCardTypesQueryReload(): void {
    this.subscriptions.add(
      merge(
        this.eventService.get(LanguageSetEvent),
        this.eventService.get(CurrencySetEvent)
      ).subscribe(() => {
        this.eventService.dispatch(
          {},
          CheckoutPaymentCardTypesQueryReloadEvent
        );
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }
}
