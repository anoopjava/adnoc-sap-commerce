/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import {
  CheckoutPaymentCardTypesQueryReloadEvent,
  CheckoutPaymentCardTypesQueryResetEvent,
  CheckoutPaymentDetailsCreatedEvent,
  CheckoutPaymentDetailsSetEvent,
} from '@spartacus/checkout/base/root';
import {
  CardType,
  Command,
  CommandService,
  CommandStrategy,
  EventService,
  OCC_USER_ID_ANONYMOUS,
  PaymentDetails,
  Query,
  QueryNotifier,
  QueryState,
  UserIdService,
} from '@spartacus/core';
import { combineLatest, Observable } from 'rxjs';
import { map, switchMap, take, tap } from 'rxjs/operators';
import { CheckoutPaymentConnector } from '../connectors/checkout-payment/checkout-payment.connector';
import { CheckoutPaymentFacade } from '../../root/facade/checkout-payment.facade';
import { CheckoutQueryFacade } from '../../root/facade/checkout-query.facade';
import { AdnocActiveCartFacade } from '../../../../cart/base/root/facade/adnoc-active-cart.facade';
import { QueryService } from '../../../../../core/src/util/query.service';

@Injectable()
export class CheckoutPaymentService implements CheckoutPaymentFacade {
  /**
   * Returns the reload events for the cardTypes query
   */
  protected getCheckoutPaymentCardTypesQueryReloadEvents(): QueryNotifier[] {
    return [CheckoutPaymentCardTypesQueryReloadEvent];
  }

  /**
   * Returns the reset events for the cardTypes query
   */
  protected getCheckoutPaymentCardTypesQueryResetEvents(): QueryNotifier[] {
    return [CheckoutPaymentCardTypesQueryResetEvent];
  }

  protected paymentCardTypesQuery: Query<CardType[]>;

  protected createPaymentMethodCommand: Command<PaymentDetails, unknown>;

  protected setPaymentMethodCommand: Command<PaymentDetails, unknown>;

  constructor(
    protected activeCartFacade: AdnocActiveCartFacade,
    protected userIdService: UserIdService,
    protected queryService: QueryService,
    protected commandService: CommandService,
    protected eventService: EventService,
    protected checkoutPaymentConnector: CheckoutPaymentConnector,
    protected checkoutQueryFacade: CheckoutQueryFacade
  ) {
    this.paymentCardTypesQuery = this.queryService.create<CardType[]>(
      () => this.checkoutPaymentConnector.getPaymentCardTypes(),
      {
        reloadOn: this.getCheckoutPaymentCardTypesQueryReloadEvents(),
        resetOn: this.getCheckoutPaymentCardTypesQueryResetEvents(),
      }
    );

    this.createPaymentMethodCommand =
      this.commandService.create<PaymentDetails>(
        (paymentDetails) =>
          this.checkoutPreconditions().pipe(
            switchMap(([userId, cartId]) =>
              this.checkoutPaymentConnector
                .createPaymentDetails(userId, cartId, paymentDetails)
                .pipe(
                  tap((response) =>
                    this.eventService.dispatch(
                      { userId, cartId, paymentDetails: response },
                      CheckoutPaymentDetailsCreatedEvent
                    )
                  )
                )
            )
          ),
        {
          strategy: CommandStrategy.CancelPrevious,
        }
      );

    this.setPaymentMethodCommand = this.commandService.create<PaymentDetails>(
      (paymentDetails) =>
        this.checkoutPreconditions().pipe(
          switchMap(([userId, cartId]) => {
            const paymentDetailsId = paymentDetails?.id;
            if (!paymentDetailsId) {
              throw new Error('Checkout conditions not met');
            }

            return this.checkoutPaymentConnector
              .setPaymentDetails(userId, cartId, paymentDetailsId)
              .pipe(
                tap(() =>
                  this.eventService.dispatch(
                    {
                      userId,
                      cartId,
                      paymentDetailsId,
                    },
                    CheckoutPaymentDetailsSetEvent
                  )
                )
              );
          })
        ),
      {
        strategy: CommandStrategy.CancelPrevious,
      }
    );
  }

  /**
   * Performs the necessary checkout preconditions.
   */
  protected checkoutPreconditions(): Observable<[string, string]> {
    return combineLatest([
      this.userIdService.takeUserId(),
      this.activeCartFacade.takeActiveCartId(),
      this.activeCartFacade.isGuestCart(),
    ]).pipe(
      take(1),
      map(([userId, cartId, isGuestCart]) => {
        if (
          !userId ||
          !cartId ||
          (userId === OCC_USER_ID_ANONYMOUS && !isGuestCart)
        ) {
          throw new Error('Checkout conditions not met');
        }
        return [userId, cartId];
      })
    );
  }

  getPaymentCardTypesState(): Observable<QueryState<CardType[] | undefined>> {
    return this.paymentCardTypesQuery.getState();
  }

  getPaymentCardTypes(): Observable<CardType[]> {
    return this.getPaymentCardTypesState().pipe(
      map((state) => state.data ?? [])
    );
  }

  getPaymentDetailsState(): Observable<QueryState<PaymentDetails | undefined>> {
    return this.checkoutQueryFacade
      .getCheckoutDetailsState()
      .pipe(map((state) => ({ ...state, data: state.data?.paymentInfo })));
  }

  createPaymentDetails(paymentDetails: PaymentDetails): Observable<unknown> {
    return this.createPaymentMethodCommand.execute(paymentDetails);
  }

  setPaymentDetails(paymentDetails: PaymentDetails): Observable<unknown> {
    return this.setPaymentMethodCommand.execute(paymentDetails);
  }
}
