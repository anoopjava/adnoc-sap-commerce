/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { PaymentType } from '@spartacus/cart/base/root';
import {
  B2BPaymentTypeEnum,
  CheckoutPaymentTypeSetEvent,
  CheckoutPaymentTypesQueryReloadEvent,
  CheckoutPaymentTypesQueryResetEvent,
} from '@spartacus/checkout/b2b/root';
import {
  Command,
  CommandService,
  CommandStrategy,
  EventService,
  HttpErrorModel,
  OCC_USER_ID_ANONYMOUS,
  Query,
  QueryNotifier,
  QueryState,
  UserIdService,
  OccEndpointsService,
} from '@spartacus/core';
import {
  Observable,
  Subject,
  combineLatest,
  of,
  throwError,
  BehaviorSubject,
} from 'rxjs';
import { concatMap, filter, map, switchMap, take, tap } from 'rxjs/operators';
import { CheckoutPaymentTypeConnector } from '../connectors/checkout-payment-type/checkout-payment-type.connector';
import { AdnocActiveCartFacade } from '../../../../cart/base/root/facade/adnoc-active-cart.facade';
import { CheckoutPaymentTypeFacade } from '../../root/facade/checkout-payment-type.facade';
import { CheckoutQueryFacade } from '../../../base/root/facade/checkout-query.facade';
import { HttpClient } from '@angular/common/http';
import { QueryService } from '../../../../../core/src/util/query.service';
import {
  IadnocConfigs,
  IBankFinalize,
  IBankPaymentParams,
  ICartwsDTO,
  ICreditLimit,
  ICurrentUser,
  IOrderStatusParams,
  IOverdue,
  IPaymentCheckoutParams,
  IRetriveStatus,
  ISessionData,
  IUrlParams,
} from '../../assets/checkout/checkout-model';

@Injectable({
  providedIn: 'root',
})
export class CheckoutPaymentTypeService implements CheckoutPaymentTypeFacade {
  protected getCheckoutPaymentTypesQueryReloadEvents(): QueryNotifier[] {
    return [CheckoutPaymentTypesQueryReloadEvent];
  }
  protected getCheckoutPaymentTypesQueryResetEvents(): QueryNotifier[] {
    return [CheckoutPaymentTypesQueryResetEvent];
  }

  protected paymentTypesQuery: Query<PaymentType[]>;

  protected setPaymentTypeCommand: Command<
    { paymentTypeCode: string; purchaseOrderNumber?: string },
    unknown
  >;
  proceedToNext$ = new Subject<Boolean>();
  proceedToBack$ = new Subject<Boolean>();
  validatePaymentTypes$ = new BehaviorSubject<{
    isValid: boolean;
    error: string;
  }>({
    isValid: false,
    error: '',
  });

  // readonly currentUser$ = this.createCurrentUserObservable();
  // readonly b2bUnitUid$ = this.createB2bUnitUidObservable();

  constructor(
    protected activeCartFacade: AdnocActiveCartFacade,
    protected userIdService: UserIdService,
    protected queryService: QueryService,
    protected commandService: CommandService,
    protected paymentTypeConnector: CheckoutPaymentTypeConnector,
    protected eventService: EventService,
    protected checkoutQueryFacade: CheckoutQueryFacade,
    protected OccEndpointsService: OccEndpointsService,
    protected http: HttpClient
  ) {
    this.paymentTypesQuery = this.queryService.create(
      () => this.paymentTypeConnector.getPaymentTypes(),
      {
        reloadOn: this.getCheckoutPaymentTypesQueryReloadEvents(),
        resetOn: this.getCheckoutPaymentTypesQueryResetEvents(),
      }
    );

    this.setPaymentTypeCommand = this.commandService.create<{
      paymentTypeCode: string;
      purchaseOrderNumber?: string;
    }>(
      ({ paymentTypeCode, purchaseOrderNumber }) =>
        this.checkoutPreconditions().pipe(
          switchMap(([userId, cartId]) =>
            this.paymentTypeConnector
              .setPaymentType(
                userId,
                cartId,
                paymentTypeCode,
                purchaseOrderNumber
              )
              .pipe(
                tap(() =>
                  this.eventService.dispatch(
                    {
                      userId,
                      cartId,
                      paymentTypeCode,
                      purchaseOrderNumber,
                    },
                    CheckoutPaymentTypeSetEvent
                  )
                )
              )
          )
        ),
      {
        strategy: CommandStrategy.CancelPrevious,
      }
    );
  }

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
  // private createCurrentUserObservable(): Observable<ICurrentUser> {
  //   return this.getCurrentUser();
  // }

  // private createB2bUnitUidObservable(): Observable<string> {
  //   return this.currentUser$.pipe(map((user) => user.orgUnit.uid));
  // }

  getPaymentTypesState(): Observable<QueryState<PaymentType[] | undefined>> {
    return this.paymentTypesQuery.getState();
  }

  getPaymentTypes(): Observable<PaymentType[]> {
    return this.getPaymentTypesState().pipe(
      concatMap((state) =>
        (state?.error as HttpErrorModel)
          ? throwError(state.error as HttpErrorModel)
          : of(state)
      ),
      map((state) => state.data ?? [])
    );
  }

  setPaymentType(
    paymentTypeCode: B2BPaymentTypeEnum,
    purchaseOrderNumber?: string
  ): Observable<unknown> {
    return this.setPaymentTypeCommand.execute({
      paymentTypeCode,
      purchaseOrderNumber,
    });
  }

  getSelectedPaymentTypeState(): Observable<
    QueryState<PaymentType | undefined>
  > {
    return this.checkoutQueryFacade
      .getCheckoutDetailsState()
      .pipe(
        map((state) => ({ ...state, data: (state.data as any)?.paymentType }))
      );
  }

  isAccountPayment(): Observable<boolean> {
    return this.getSelectedPaymentTypeState().pipe(
      filter((state) => !state.loading),
      map((state) => state.data?.code === B2BPaymentTypeEnum.ACCOUNT_PAYMENT)
    );
  }

  getPurchaseOrderNumberState(): Observable<QueryState<string | undefined>> {
    return this.checkoutQueryFacade.getCheckoutDetailsState().pipe(
      map((state) => ({
        ...state,
        data: (state.data as any)?.purchaseOrderNumber,
      }))
    );
  }

  getCreditLimit(b2bUnitUid: string): Observable<ICreditLimit> {
    let url = this.OccEndpointsService.buildUrl('getCreditLimit');
    return this.http.post<ICreditLimit>(url, { b2bUnitUid });
  }

  getCurrentUser(): Observable<ICurrentUser> {
    let url = this.OccEndpointsService.buildUrl('getCurrentUser');
    return this.http.get<ICurrentUser>(url);
  }

  setPaymentTypeAndCreditLimit(payload: IUrlParams): Observable<ICartwsDTO> {
    let url = this.OccEndpointsService.buildUrl(
      'setPaymentTypeAndCreditLimit',
      {
        urlParams: {
          cartId: payload.cartId,
          isCreditLimitUsed: payload.isCreditLimitUsed,
          paymentType: payload.paymentType,
          purchaseOrderNumber: payload.purchaseOrderNumber,
          creditLimitValue: payload.creditLimitValue,
        },
      }
    );
    return this.http.post<ICartwsDTO>(url, payload.poDocument);
  }

  intitatePaymentCheckout(
    payload: IPaymentCheckoutParams
  ): Observable<ISessionData> {
    let url = this.OccEndpointsService.buildUrl('intitatePaymentCheckout', {
      urlParams: {
        cartId: payload.cartId,
        paymentType: payload.paymentType,
        paymentAmount: payload.paymentAmount,
      },
    });
    return this.http.post<ISessionData>(url, null);
  }

  finalizeBankTransfer(payload: IBankPaymentParams): Observable<IBankFinalize> {
    let url = this.OccEndpointsService.buildUrl('bankTransferValidation', {
      urlParams: {
        cartId: payload.cartId,
        transactionID: payload.transactionId,
      },
    });
    return this.http.post<IBankFinalize>(url, null);
  }

  retriveOrderStatus(payload: IOrderStatusParams): Observable<IRetriveStatus> {
    let url = this.OccEndpointsService.buildUrl('retriveOrderStatus', {
      urlParams: {
        cartId: payload.cartId,
        resultIndicator: payload.resultIndicator,
        sessionVersion: payload.sessionVersion,
      },
    });
    return this.http.get<IRetriveStatus>(url);
  }

  creditSimulationCheck(payer: string): Observable<IOverdue> {
    const url = this.OccEndpointsService.buildUrl('creditSimulationCheck', {
      queryParams: { payerId: payer },
    });
    return this.http.get<IOverdue>(url);
  }

  getBankCardUrl(): Observable<IadnocConfigs> {
    const url = this.OccEndpointsService.buildUrl('adnocConfig', {
      urlParams: { configKey: 'adnocPaymentCardUrl' },
    });
    return this.http.get<IadnocConfigs>(url);
  }
}
