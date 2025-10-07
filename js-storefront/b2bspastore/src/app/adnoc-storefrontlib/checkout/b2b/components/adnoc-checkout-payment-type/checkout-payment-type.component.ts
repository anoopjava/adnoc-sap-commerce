/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  inject,
  OnInit,
  OnDestroy,
  ViewChild,
  ViewEncapsulation,
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { B2BPaymentTypeEnum } from '@spartacus/checkout/b2b/root';
import { CheckoutStepType } from '@spartacus/checkout/base/root';
import {
  CurrencyService,
  getLastValueSync,
  GlobalMessageType,
  HttpErrorModel,
  isNotUndefined,
  OccHttpErrorType,
} from '@spartacus/core';
import { BehaviorSubject, combineLatest, Observable, of, Subject } from 'rxjs';
import {
  catchError,
  distinctUntilChanged,
  filter,
  map,
  switchMap,
  takeUntil,
  tap,
} from 'rxjs/operators';
import { CheckoutStepService } from '../../../base/components/services';
import { AdnocActiveCartService } from '../../../../cart/base/core/facade/adnoc-active-cart.service';
import { CheckoutPaymentTypeFacade } from '../../root/facade/checkout-payment-type.facade';
import { CheckoutPaymentTypeService } from '../../core/facade/checkout-payment-type.service';
import {
  AdnocCart,
  B2BcreditLimit,
  IPaymentPayload,
  IPaymentPayloadExtended,
  IUrlParams,
} from '../../assets/checkout/checkout-model';
import { PAYMENT_TYPE } from '../../../../shared/constants';
import { saveCreditLimit } from '../../b2b-store/actions/creditLimit.actions';
import { Store } from '@ngrx/store';
import { PaymentType } from '../../../../../core/model/adnoc-cart.model';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

type PaymentTypeCode = 'PAYMENT_24' | 'BANK_TRANSFER' | 'CARD';

type PaymentTypeImages = {
  [key in PaymentTypeCode]: string[];
};
@Component({
  selector: 'adnoc-payment-type',
  templateUrl: './checkout-payment-type.component.html',
  styleUrls: ['./checkout-payment-type.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  standalone: false,
})
export class CheckoutPaymentTypeComponent implements OnInit, OnDestroy {
  // Implement OnInit and OnDestroy
  @ViewChild('poNumber', { static: false })
  private poNumberInputElement!: ElementRef<HTMLInputElement>;
  paymentTypeImages: PaymentTypeImages = {
    PAYMENT_24: ['images/payment/direct.png'],
    BANK_TRANSFER: ['images/payment/bank.png'],
    CARD: [
      'images/payment/mastro.png',
      'images/payment/amex.png',
      'images/payment/visa.png',
    ],
  };
  protected busy$ = new BehaviorSubject<boolean>(false);
  protected anocActiveCartService = inject(AdnocActiveCartService);
  protected checkoutPaymentTypeService = inject(CheckoutPaymentTypeService);
  protected store = inject(Store);

  cartId = this.anocActiveCartService
    .getActiveCartId()
    .pipe(map((data) => data));
  typeSelected = '';
  paymentTypesError = false;

  isUpdating$: Observable<any>;

  paymentTypes$: Observable<PaymentType[]>;

  // Core observables
  readonly cart$: Observable<AdnocCart> =
    this.anocActiveCartService.getActive();

  typeSelected$: Observable<PaymentType>;
  cartPoNumber$: Observable<string> = this.cart$.pipe(
    map((data) => data.purchaseOrderNumber ?? '')
  );
  cartTotal = 0;
  CreditLimitValue = '';
  protected readonly destroy$ = new Subject<void>();
  poNumber = '';
  loader = true;
  fileFormData!: FormData;
  poNumberValue = '';
  isCreditLimit = false;
  creditLimitDebitValue = 0;
  selectedPaymentType = '';
  payload: IPaymentPayloadExtended = {
    cartId: '',
    isCreditLimitUsed: false,
    paymentType: '',
    purchaseOrderNumber: '',
    poDocument: new FormData(),
    creditLimitValue: 0,
  };
  amountToBePaid = 0;

  b2bUnitUid$: Observable<string>;
  checkRule: string | undefined;

  protected currencyService = inject(CurrencyService);
  currencyIsocode = '';
  creditLimit$!: Observable<B2BcreditLimit>;
  fileSizeError = false;
  paymentTypeBank = PAYMENT_TYPE.bankTransfer;
  constructor(
    protected checkoutPaymentTypeFacade: CheckoutPaymentTypeFacade,
    protected checkoutStepService: CheckoutStepService,
    protected activatedRoute: ActivatedRoute,
    protected globalMessageService: AdnocGlobalMessageService,
    protected cd?: ChangeDetectorRef
  ) {
    this.b2bUnitUid$ = this.checkoutPaymentTypeFacade.getCurrentUser().pipe(
      filter((user) => !!user?.orgUnit?.uid),
      map((user) => user.orgUnit.uid),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    );

    this.paymentTypes$ = this.checkoutPaymentTypeFacade.getPaymentTypes().pipe(
      map((paymentTypes) =>
        paymentTypes.sort((a, b) =>
          a.code === PAYMENT_TYPE.creditLimit
            ? -1
            : b.code === PAYMENT_TYPE.creditLimit
            ? 1
            : 0
        )
      ),
      tap(() => (this.paymentTypesError = false)),
      catchError((error: HttpErrorModel) => {
        if (
          error.details?.[0]?.type === OccHttpErrorType.CLASS_MISMATCH_ERROR
        ) {
          this.globalMessageService.add(
            { key: 'httpHandlers.forbidden' },
            GlobalMessageType.MSG_TYPE_ERROR
          );
          this.paymentTypesError = true;
        }
        return of([]);
      })
    );

    this.typeSelected$ = combineLatest([
      this.checkoutPaymentTypeFacade.getSelectedPaymentTypeState().pipe(
        filter((state) => !state.loading),
        map((state) => state.data)
      ),
      this.paymentTypes$,
    ]).pipe(
      map(
        ([selectedPaymentType, availablePaymentTypes]: [
          PaymentType | undefined,
          PaymentType[]
        ]) => {
          if (
            selectedPaymentType &&
            availablePaymentTypes.find((availablePaymentType) => {
              return availablePaymentType.code === selectedPaymentType.code;
            })
          ) {
            return selectedPaymentType;
          }
          if (availablePaymentTypes.length) {
            this.busy$.next(true);
            this.checkoutPaymentTypeFacade
              .setPaymentType(
                availablePaymentTypes[0].code as string,
                this.poNumberInputElement?.nativeElement?.value
              )
              .subscribe({
                complete: () => this.onSuccess(),
                error: () => this.onError(),
              });
            return availablePaymentTypes[0];
          }
          return undefined;
        }
      ),
      filter(isNotUndefined),
      distinctUntilChanged(),
      tap((selected) => {
        this.typeSelected = selected?.code ?? '';
        this.checkoutStepService.disableEnableStep(
          CheckoutStepType.PAYMENT_DETAILS,
          selected?.code === B2BPaymentTypeEnum.ACCOUNT_PAYMENT
        );
      })
    );

    this.isUpdating$ = combineLatest([
      this.busy$,
      this.checkoutPaymentTypeFacade
        .getSelectedPaymentTypeState()
        .pipe(map((state) => state.loading)),
    ]).pipe(
      map(([busy, loading]) => busy || loading),
      distinctUntilChanged()
    );
  }

  sanitizePoNumber(input: HTMLInputElement) {
    // Remove all non-alphanumeric characters
    const sanitized = input.value.replace(/[^a-zA-Z0-9]/g, '');
    if (input.value !== sanitized) {
      input.value = sanitized;
      const formControl = this.poNumberInputElement?.nativeElement;
      if (formControl) {
        formControl.value = sanitized;
      }
      this.poNumberValue = sanitized;
    }
  }
  ngOnInit(): void {
    this.checkoutPaymentTypeService.proceedToNext$
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => {
        if (data) {
          this.next();
        }
      });

    this.checkoutPaymentTypeService.proceedToBack$
      .pipe(takeUntil(this.destroy$))
      .subscribe((data) => {
        if (data) {
          this.back();
        }
      });

    // Combine b2bUnitUid$ and currencyService.getActive()
    this.creditLimit$ = combineLatest([
      this.b2bUnitUid$.pipe(filter((b2bUnitUid) => !!b2bUnitUid)),
      this.currencyService.getActive().pipe(filter((data) => !!data)),
    ]).pipe(
      switchMap(([b2bUnitUid, currency]) => {
        this.currencyIsocode = currency;
        return this.checkoutPaymentTypeFacade.getCreditLimit(b2bUnitUid).pipe(
          tap(() => (this.loader = true)),
          map((data) => data.b2BCreditLimit),
          distinctUntilChanged()
        );
      })
    );

    const activeCart$ = this.anocActiveCartService.getActive();

    // Combine credit limit and cart observables
    combineLatest([this.creditLimit$, activeCart$])
      .pipe(
        tap(() => (this.loader = true)),
        map(([creditLimit, cart]) => ({
          creditLimit,
          cart,
        })),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: ({ creditLimit, cart }) => {
          if (creditLimit?.availableCl) {
            this.store.dispatch(saveCreditLimit({ creditLimit }));
          }
          this.CreditLimitValue = creditLimit.availableCl;
          this.checkRule = creditLimit.checkRule;
          this.typeSelected =
            this.checkRule !== 'Z2'
              ? this.currencyIsocode !== 'USD'
                ? cart.paymentType?.code ?? ''
                : ''
              : 'CREDIT_LIMIT';
          this.selectedPaymentType = this.typeSelected;
          this.poNumber = cart?.purchaseOrderNumber ?? '';
          this.cartTotal = cart?.totalPriceWithTax?.value ?? 0;
          this.loader = false;
          this.cd?.detectChanges();
          if (
            this.typeSelected === 'CREDIT_LIMIT' &&
            this.CreditLimitValue &&
            this.checkRule !== 'Z2'
          ) {
            if (Number(this.CreditLimitValue) < this.cartTotal) {
              this.selectedPaymentType = '';
            }
            this.anocActiveCartService.creditLimitFlow$.next({
              isChecked: true,
              creditLimitValue: Number(this.CreditLimitValue),
            });
            this.isCreditLimit = true;
            this.cd?.detectChanges();
          }
          this.updatePayload();
        },
        error: (error) => {
          const cartData = getLastValueSync(this.cart$);
          this.cartTotal = cartData?.totalPriceWithTax?.value ?? 0;
          this.typeSelected = cartData?.paymentType?.code ?? '';
          this.selectedPaymentType = this.typeSelected;
          this.poNumber = getLastValueSync(this.cartPoNumber$) ?? '';
          this.updatePayload();
          this.globalMessageService.remove(GlobalMessageType.MSG_TYPE_ERROR);
          this.loader = false;
          if (error && error.error?.errors?.length > 0) {
            const errorMessage = error.error.errors[0]?.message;

            this.globalMessageService.add(
              errorMessage ??
                'An unknown error occurred fetching credit limit.', // Provide default message
              GlobalMessageType.MSG_TYPE_ERROR
            );
            this.cd?.detectChanges();
          } else {
            // Handle cases where the error structure might be different or missing
            this.globalMessageService.add(
              'An unknown error occurred fetching credit limit.',
              GlobalMessageType.MSG_TYPE_ERROR
            );
            this.cd?.detectChanges();
          }
        },
      });
  }

  creditLimitHandler(event: Event) {
    const checkbox = event.target as HTMLInputElement;
    const isChecked = checkbox.checked;
    const value = checkbox.value;

    this.anocActiveCartService.creditLimitFlow$.next({
      isChecked,
      creditLimitValue: +value,
    });
  }

  changeType(code: string, event?: Event): void {
    const checkbox = event?.target as HTMLInputElement; // Access the checkbox element

    if (this.cartTotal <= Number(this.CreditLimitValue)) {
      this.typeSelected = code;
      this.selectedPaymentType = checkbox?.checked ? code : '';
      this.isCreditLimit = false;
      this.cd?.detectChanges();
    }
    if (code === 'CREDIT_LIMIT' && event) {
      this.isCreditLimit = checkbox?.checked ?? false; // Ensure boolean value
      this.creditLimitDebitValue =
        this.cartTotal <= Number(this.CreditLimitValue)
          ? this.cartTotal
          : Number(this.CreditLimitValue);

      this.amountToBePaid =
        this.cartTotal > Number(this.CreditLimitValue)
          ? this.cartTotal - Number(this.CreditLimitValue)
          : 0;
      this.creditLimitHandler(event);
    } else {
      this.selectedPaymentType = code;
    }

    this.updatePayload();
  }

  updateFileName(event: Event): void {
    const input = event.target as HTMLInputElement;
    const label = input.nextElementSibling as HTMLLabelElement;

    this.fileSizeError = false;
    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      const maxSize = 5 * 1024 * 1024;

      if (file.size > maxSize) {
        this.fileSizeError = true;
        this.fileFormData = new FormData();
        if (label) label.innerText = '';
      } else {
        const formData = new FormData();
        formData.append('poDocument', file, file.name);
        this.fileFormData = formData;
        if (label) label.innerText = file.name;
      }
      this.updatePayload();
    }
  }

  poNumberChange(value: string) {
    this.poNumberValue = value;
    this.updatePayload();
  }

  validatePayload(payload: IPaymentPayload): {
    isValid: boolean;
    error?: string;
  } {
    // Check required fields
    if (
      !payload.purchaseOrderNumber ||
      payload.purchaseOrderNumber.trim() === ''
    ) {
      return {
        isValid: false,
        error: 'Please enter the PO number*.',
      };
    }
    // Check if poDocument FormData has the 'poDocument' key
    if (!payload.poDocument || !payload.poDocument.has('poDocument')) {
      return {
        isValid: false,
        error: 'Please Upload the PO document*.',
      };
    }
    if (
      payload.paymentType === undefined ||
      payload.paymentType.trim() === ''
    ) {
      return { isValid: false, error: 'Please Select the Payment type*.' };
    }
    return { isValid: true };
  }

  next(): void {
    this.updatePayload();

    const validation = this.validatePayload(this.payload);
    if (!validation.isValid) {
      // Optionally show a user-facing message here using GlobalMessageService
      if (validation.error) {
        this.globalMessageService.add(
          validation.error,
          GlobalMessageType.MSG_TYPE_ERROR
        );
      }
      return;
    } else {
      this.busy$.next(true); // Indicate loading state
      this.checkoutPaymentTypeFacade
        .setPaymentTypeAndCreditLimit(this.payload)
        .pipe(
          takeUntil(this.destroy$),
          catchError((error) => {
            this.globalMessageService.add(
              'Failed to set payment type. Please try again.',
              GlobalMessageType.MSG_TYPE_ERROR
            );
            this.busy$.next(false); // Reset loading state on error
            return of(null); // Prevent observable from completing on error, return null or handle differently
          })
        )
        .subscribe({
          next: (data) => {
            if (data) {
              // Check if the operation was successful (might depend on API response)
              this.anocActiveCartService.reloadActiveCart();
              this.checkoutStepService.next(this.activatedRoute);
              this.resetPayload();
            }
            this.busy$.next(false); // Reset loading state on success/completion
          },
          // Error handled by catchError now
          // complete: () => this.busy$.next(false) // Reset loading state on completion if not handled elsewhere
        });
    }
  }

  updatePayload(): void {
    const poNumberInput = this.poNumberInputElement?.nativeElement?.value;
    this.payload = {
      cartId: getLastValueSync(this.cartId) ?? '',
      isCreditLimitUsed:
        this.checkRule !== 'Z2' ? this.isCreditLimit || false : true,
      paymentType: this.selectedPaymentType,
      purchaseOrderNumber: this.poNumberValue || poNumberInput || '',
      poDocument: this.fileFormData || new FormData(), // Ensure poDocument is always a FormData object
      creditLimitValue:
        this.checkRule !== 'Z2'
          ? this.isCreditLimit
            ? this.creditLimitDebitValue
            : 0
          : this.cartTotal,
    };

    const validation = this.validatePayload(this.payload);
    this.checkoutPaymentTypeService.validatePaymentTypes$.next({
      isValid: validation.isValid,
      error: validation.error ?? '',
    });
  }

  resetPayload(): void {
    this.payload = {
      cartId: '',
      isCreditLimitUsed: false,
      paymentType: '',
      purchaseOrderNumber: '',
      poDocument: new FormData(),
      creditLimitValue: 0,
    };
    // Also reset related component state if needed
    this.poNumberValue = '';
    this.fileFormData = new FormData();
    // Reset file input visually if possible (might require ViewChild access to the input element)
  }
  back(): void {
    this.checkoutStepService.back(this.activatedRoute);
  }

  protected onSuccess(): void {
    this.busy$.next(false);
    this.anocActiveCartService.reloadActiveCart();
  }

  protected onError(): void {
    this.busy$.next(false);
    // Consider adding a global message here as well
    this.globalMessageService.add(
      'An error occurred while processing the payment type.',
      GlobalMessageType.MSG_TYPE_ERROR
    );
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
