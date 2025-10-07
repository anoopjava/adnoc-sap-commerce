/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  ComponentRef,
  inject,
  OnDestroy,
  ViewContainerRef,
  ViewEncapsulation,
} from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import {
  GlobalMessageType,
  RoutingService,
  WindowRef,
  getLastValueSync,
} from '@spartacus/core';
import { OrderFacade } from '@spartacus/order/root';
import { LaunchDialogService, LAUNCH_CALLER } from '@spartacus/storefront';
import { combineLatest, Observable, Subject, of } from 'rxjs';
import { CheckoutPaymentTypeFacade } from '../../../b2b/root/facade/checkout-payment-type.facade';
import { Cart } from '@spartacus/cart/base/root';
import { AdnocActiveCartFacade } from '../../../../cart/base/root/facade/adnoc-active-cart.facade';
import {
  distinctUntilChanged,
  map,
  switchMap,
  takeUntil,
  tap,
} from 'rxjs/operators';
import { ActivatedRoute, Router } from '@angular/router';
import { PAYMENT_TYPE } from '../../../../shared/constants';
import { CheckoutPaymentTypeService } from '../../../b2b/core/facade/checkout-payment-type.service';
import {
  AdnocCart,
  IBankPaymentParams,
  IBankTransaction,
  ICurrentUser,
  IPaymentCheckoutParams,
  Transaction,
} from '../../../b2b/assets/checkout/checkout-model';
import { Store } from '@ngrx/store';
import { selectCreditLimit } from '../../../b2b/b2b-store/selector/creditLimit.selector';
import { saveCreditLimit } from '../../../b2b/b2b-store/actions/creditLimit.actions';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

@Component({
  selector: 'cx-place-order',
  templateUrl: './adnoc-checkout-place-order.component.html',
  styleUrl: './adnoc-checkout-place-order.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  standalone: false,
})
export class AdnocCheckoutPlaceOrderComponent implements OnDestroy {
  protected checkoutPaymentTypeFacade = inject(CheckoutPaymentTypeFacade);
  protected checkoutPaymentTypeService = inject(CheckoutPaymentTypeService);
  protected activeCartFacade = inject(AdnocActiveCartFacade);
  protected activatedRoute = inject(ActivatedRoute);
  protected store = inject(Store);
  protected fb = inject(UntypedFormBuilder);
  cart$: Observable<AdnocCart> = this.activeCartFacade.getActive();

  cartId = this.cart$.pipe(map((data: Cart) => data.code));
  paymentType$ = this.cart$.pipe(map((cart: Cart) => cart.paymentType ?? ''));
  placedOrder!: void | Observable<ComponentRef<any> | undefined>;

  checkoutSubmitForm: UntypedFormGroup;

  checkoutSubmit: UntypedFormGroup;
  cartTotal = 0;
  CreditLimitValue!: string;
  amoutToBePaid = 0;
  isCreditLimitUsed = false;

  get termsAndConditionInvalid(): boolean {
    return this.checkoutSubmitForm.invalid;
  }
  protected readonly destroy$ = new Subject<void>();
  isProcessing = false;
  paymentInitialized = false;
  private scriptElement: HTMLScriptElement | null = null;
  resultIndicator = '';
  sessionVersion = '';
  transactionId = '';
  cartItems = getLastValueSync(this.cart$);
  readonly currentUser$ = this.createCurrentUserObservable();
  readonly b2bUnitUid$ = this.createB2bUnitUidObservable();
  constructor(
    protected orderFacade: OrderFacade,
    protected routingService: RoutingService,
    protected launchDialogService: LaunchDialogService,
    protected vcr: ViewContainerRef,
    protected globalMessageService: AdnocGlobalMessageService,
    protected router: Router,
    protected winRef: WindowRef
  ) {
    this.checkoutSubmitForm = this.fb.group({
      termsAndConditions: [false, Validators.requiredTrue],
    });

    this.checkoutSubmit = this.fb.group({
      termsAndConditions: [true, Validators.requiredTrue],
    });
  }

  ngOnInit(): void {
    const paymentType = getLastValueSync(this.paymentType$);
    const paymentTypCode = typeof paymentType === 'object' && paymentType?.code;
    if (paymentTypCode === PAYMENT_TYPE.card) {
      this.loadMastercardScript();
    }
  }
  private createCurrentUserObservable(): Observable<ICurrentUser> {
    return this.checkoutPaymentTypeFacade.getCurrentUser();
  }

  private createB2bUnitUidObservable(): Observable<string> {
    return this.currentUser$.pipe(map((user) => user.orgUnit.uid));
  }
  ngAfterViewInit(): void {
    this.getQueryParams();
  }

  initializeCheckout(sessionId: string): void {
    const Checkout = (window as any).Checkout;
    if (Checkout) {
      Checkout.configure({
        session: {
          id: sessionId,
        },
      });
    }
  }

  getCreditLimit(): void {
    const creditLimit$ = this.store.select(selectCreditLimit).pipe(
      switchMap((creditLimit) => {
        if (creditLimit) {
          return of(creditLimit);
        } else {
          return this.b2bUnitUid$.pipe(
            switchMap((b2bUnitUid) =>
              this.checkoutPaymentTypeFacade.getCreditLimit(b2bUnitUid).pipe(
                map((data) => data.b2BCreditLimit),
                tap((fetchedCreditLimit) => {
                  this.store.dispatch(
                    saveCreditLimit({ creditLimit: fetchedCreditLimit })
                  );
                })
              )
            )
          );
        }
      }),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    );

    combineLatest([creditLimit$, this.cart$])
      .pipe(
        map(([creditLimit, cart]) => ({
          creditLimit,
          cart,
        })),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: ({ creditLimit, cart }) => {
          this.cartTotal = cart?.totalPriceWithTax?.value ?? 0;
          this.CreditLimitValue = creditLimit.availableCl;
          if (cart.creditLimitUsed) {
            this.amoutToBePaid =
              this.cartTotal > Number(this.CreditLimitValue)
                ? this.cartTotal - Number(this.CreditLimitValue)
                : this.cartTotal;
          } else {
            this.amoutToBePaid = this.cartTotal;
          }
        },
        error: (error) => {
          const cartData = getLastValueSync(this.cart$);
          this.cartTotal = cartData?.totalPriceWithTax?.value ?? 0;
          this.amoutToBePaid = this.cartTotal;
        },
      });
  }
  getQueryParams(): void {
    if (this.cartItems?.creditLimitUsed) {
      this.getCreditLimit();
    } else {
      this.amoutToBePaid = this.cartItems?.totalPriceWithTax?.value ?? 0;
    }

    this.activatedRoute.queryParams
      .pipe(
        distinctUntilChanged(),
        takeUntil(this.destroy$),
        switchMap((params) => {
          this.resultIndicator = params['resultIndicator'] || null;
          this.sessionVersion = params['sessionVersion'] || null;
          this.transactionId = params['transactionID'] || null;

          if (this.resultIndicator && this.sessionVersion) {
            //For bank card
            const payload = {
              cartId: getLastValueSync(this.cartId),
              resultIndicator: this.resultIndicator,
              sessionVersion: this.sessionVersion,
            };
            return this.checkoutPaymentTypeFacade
              .retriveOrderStatus(payload)
              .pipe(
                map((data) => ({ type: 'card', transaction: data.transaction }))
              );
          } else if (this.transactionId) {
            // For bank transfer
            this.placedOrder = this.launchDialogService.launch(
              LAUNCH_CALLER.PLACE_ORDER_SPINNER,
              this.vcr
            );
            const payload = {
              cartId: getLastValueSync(this.cartId),
              transactionId: this.transactionId,
            } as IBankPaymentParams;
            return this.checkoutPaymentTypeFacade
              .finalizeBankTransfer(payload)
              .pipe(
                map((data) => ({
                  type: 'bank',
                  transaction: data?.transaction || [],
                }))
              );
          }
          return of(null);
        })
      )
      .subscribe({
        next: (result) => {
          if (!result) return;

          if (result.type === 'card' && result.transaction) {
            this.placedOrder = this.launchDialogService.launch(
              LAUNCH_CALLER.PLACE_ORDER_SPINNER,
              this.vcr
            );
            const data = result.transaction as Transaction[];
            if (data[0].result === 'SUCCESS') {
              this.orderFacade.placeOrder(this.checkoutSubmit.valid).subscribe({
                next: () => this.onSuccess(),
                error: () => this.clearSpinnerDialog(),
              });
            }
          } else if (result.type === 'bank' && result.transaction) {
            const data = result.transaction as IBankTransaction;
            if (data.responseCode === '0') {
              this.orderFacade.placeOrder(this.checkoutSubmit.valid).subscribe({
                next: () => this.onSuccess(),
                error: () => this.clearSpinnerDialog(),
              });
            } else {
              this.clearSpinnerDialog();
              if (data.responseDescription) {
                this.globalMessageService.add(
                  data.responseDescription,
                  GlobalMessageType.MSG_TYPE_ERROR
                );
              }
            }
          }
        },
        error: (err) => {
          this.clearSpinnerDialog();
          const errorMessage = err.error.errors[0]?.message;
          this.globalMessageService.add(
            errorMessage,
            GlobalMessageType.MSG_TYPE_ERROR
          );
        },
      });
  }

  // For clear spinner
  private clearSpinnerDialog(): void {
    if (!this.placedOrder) return;
    this.placedOrder
      .subscribe((component) => {
        this.launchDialogService.clear(LAUNCH_CALLER.PLACE_ORDER_SPINNER);
        if (component) {
          component.destroy();
        }
      })
      .unsubscribe();
  }

  triggerPayment(): void {
    const Checkout = (window as any).Checkout;

    if (Checkout) {
      Checkout.showPaymentPage(); // For payment page
    }
  }

  loadMastercardScript(): void {
    this.checkoutPaymentTypeFacade
      .getBankCardUrl()
      .pipe(
        map(
          (data) =>
            data.adnocConfigs?.find(
              (config) => config.configKey === 'adnocPaymentCardUrl'
            )?.configValue
        ),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: (bankCardUrl) => {
          if (!bankCardUrl) {
            this.router.navigate(['/checkout/payment-type']).then(() => {
              this.globalMessageService.add(
                { key: 'payments.mastercardConfigMissing' },
                GlobalMessageType.MSG_TYPE_ERROR
              );
            });
            return;
          }
          const script = document.createElement('script');
          script.src = `${bankCardUrl}/static/checkout/checkout.min.js`;
          script.setAttribute('data-error', 'errorCallback');
          script.setAttribute('data-cancel', 'cancelCallback');
          script.setAttribute('data-complete', 'complete');
          script.onload = () => {
            this.paymentInitialized = true;
          };
          this.scriptElement = script;
          document.head.appendChild(script);

          // Add callback functions to window object
          (window as any).errorCallback = this.errorCallback.bind(this);
          (window as any).cancelCallback = this.cancelCallback.bind(this);
        },
        error: (err) => {
          const errorMessage = err?.error?.errors?.[0]?.message;
          this.globalMessageService.add(
            { raw: errorMessage },
            GlobalMessageType.MSG_TYPE_ERROR
          );
        },
      });
  }

  errorCallback(error: any): void {
    console.log(JSON.stringify(error));
    prompt(JSON.stringify(error));
    this.isProcessing = false;
    // Handle error in your application, potentially navigate to an error page
  }

  cancelCallback(): void {
    prompt('Payment cancelled');
    this.isProcessing = false;
    // Handle cancellation in your application
  }

  submitForm(): void {
    if (this.checkoutSubmitForm.valid) {
      const paymentType = getLastValueSync(this.paymentType$);
      const paymentTypCode =
        typeof paymentType === 'object' && paymentType?.code;
      if (paymentTypCode !== PAYMENT_TYPE.creditLimit) {
        this.placedOrder = this.launchDialogService.launch(
          LAUNCH_CALLER.PLACE_ORDER_SPINNER,
          this.vcr
        );
        const payload = {
          cartId: getLastValueSync(this.cartId),
          paymentType: paymentTypCode,
          paymentAmount: Math.round(this.amoutToBePaid * 100) / 100, // Round to 2 decimal places
        } as IPaymentCheckoutParams;

        this.checkoutPaymentTypeFacade
          .intitatePaymentCheckout(payload)
          .subscribe({
            next: (data) => {
              const window = this.winRef.nativeWindow;
              if (paymentTypCode === PAYMENT_TYPE.card) {
                this.initializeCheckout(data.session.id);
                this.triggerPayment();
              } else if (
                paymentTypCode === PAYMENT_TYPE.bankTransfer &&
                data.transaction?.paymentPortal
              ) {
                if (window) {
                  window.location.href = data.transaction.paymentPortal;
                }
              }
            },
            error: () => {
              this.globalMessageService.add(
                {
                  key: 'payments.sessionError',
                },
                GlobalMessageType.MSG_TYPE_ERROR
              );
              if (!this.placedOrder) {
                return;
              }
              this.placedOrder
                .subscribe((component) => {
                  this.launchDialogService.clear(
                    LAUNCH_CALLER.PLACE_ORDER_SPINNER
                  );
                  if (component) {
                    component.destroy();
                  }
                })
                .unsubscribe();
            },
          });
      } else {
        this.placedOrder = this.launchDialogService.launch(
          LAUNCH_CALLER.PLACE_ORDER_SPINNER,
          this.vcr
        );
        this.orderFacade.placeOrder(this.checkoutSubmitForm.valid).subscribe({
          error: () => {
            if (!this.placedOrder) {
              return;
            }

            this.placedOrder
              .subscribe((component) => {
                this.launchDialogService.clear(
                  LAUNCH_CALLER.PLACE_ORDER_SPINNER
                );
                if (component) {
                  component.destroy();
                }
              })
              .unsubscribe();
          },
          next: () => this.onSuccess(),
        });
      }
    } else {
      this.checkoutSubmitForm.markAllAsTouched();
    }
  }

  onSuccess(): void {
    this.routingService.go({ cxRoute: 'orderConfirmation' });
    this.store.dispatch({ type: '[Entry] Clear Entries' });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();

    this.launchDialogService.clear(LAUNCH_CALLER.PLACE_ORDER_SPINNER);
    // Remove the script element from the DOM
    if (this.scriptElement && this.scriptElement.parentNode) {
      this.scriptElement.parentNode.removeChild(this.scriptElement);
    }

    // Clean up global callbacks
    if (window) {
      (window as any).errorCallback = null;
      (window as any).cancelCallback = null;
    }
  }
}
