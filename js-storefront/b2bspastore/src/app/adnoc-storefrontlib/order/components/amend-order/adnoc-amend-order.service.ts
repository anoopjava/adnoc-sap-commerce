/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { formatCurrency, getCurrencySymbol } from '@angular/common';
import { ChangeDetectorRef, inject, Injectable } from '@angular/core';
import {
  AbstractControl,
  UntypedFormControl,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { OrderEntry } from '@spartacus/cart/base/root';
import { Price, UserIdService, OccEndpointsService } from '@spartacus/core';
import { Order } from '@spartacus/order/root';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { AmendOrderType } from './amend-order.model';
import { OrderDetailsService } from '../order-details/order-details.service';
import _ from 'lodash';
import { HttpClient } from '@angular/common/http';
import {
  AdnocOrder,
  ICancelReasons,
  IReturnReasons,
  ICancellationRequestEntryInputs,
} from '../../../../core/model/adnoc-users.model';
import { AdnocApiEndpoints } from '../../../services/apiServices/adnoc-api-endpoints';
import { AdnocConfigRoot } from '../../../services/apiServices/api-response.model';

@Injectable()
export abstract class AdnocOrderAmendService {
  protected amendType!: AmendOrderType;
  protected cd?: ChangeDetectorRef;
  protected form!: UntypedFormGroup;
  protected OccEndpointsService = inject(OccEndpointsService);
  protected http = inject(HttpClient);
  protected userIdService = inject(UserIdService);
  userId = '';
  fileSubject = new BehaviorSubject<File | null>(null);
  constructor(protected orderDetailsService: OrderDetailsService) {}

  /**
   * Returns entries for the given order.
   */
  abstract getEntries(): Observable<OrderEntry[]>;

  /**
   * Returns entries with an amended quantity.
   */
  getAmendedEntries(): Observable<OrderEntry[]> {
    return this.getForm().pipe(
      switchMap((form) => {
        return this.getEntries().pipe(
          map((entries) =>
            entries.filter(
              (entry) => this.getFormControl(form, entry).value > 0
            )
          )
        );
      })
    );
  }

  /**
   * Submits the amended order.
   */
  abstract save(): void;

  getOrder(): Observable<Order> {
    return this.orderDetailsService.getOrderDetails();
  }

  /**
   * returns the form with form data at runtime
   */
  getForm(): Observable<UntypedFormGroup> {
    return this.getOrder().pipe(
      tap((order) => {
        if (!this.form || this.form.get('orderCode')?.value !== order.code) {
          this.buildForm(order);
        }
      }),
      map(() => this.form)
    );
  }

  ValidateQuantityToCancel() {
    return (control: AbstractControl) => {
      if (!control.value) {
        return null;
      }

      const quantity = Object.values(control.value as number).reduce(
        (acc: number, val: number) => acc + val,
        0
      );

      return quantity > 0 ? null : { cxNoSelectedItemToCancel: true };
    };
  }

  ValidateReturnReason() {
    return (control: AbstractControl) => {
      if (!control.value) {
        return { cxNoSelectedReturnReason: true };
      }
      return null;
    };
  }

  ValidateCancelReason() {
    return (control: AbstractControl) => {
      if (!control.value) {
        return null;
      }
      this.cd?.detectChanges();
      const cancelReason = Object.values(control.value as string).reduce(
        (acc: string, val: string) => acc + val,
        ''
      );
      return cancelReason ? null : { cxNoSelectedCancelReason: true };
    };
  }

  ValidateReturnDocuemnt() {
    return (control: AbstractControl) => {
      if (!control.value) {
        return { cxNoSelectedReturnDocument: true };
      }
      return null;
    };
  }

  validateObjects(formData: UntypedFormGroup): boolean {
    if (!formData) {
      return false;
    }
    const cancelReasonData = formData.get('cancelReason')?.value ?? {};
    const entriesData = formData.get('entries')?.value ?? {};

    // Normalize entriesData by replacing 0 with an empty string
    const transformedEntries = Object.fromEntries(
      Object.entries(entriesData).map(([key, value]) => [
        key,
        value === 0 ? '' : value,
      ])
    );

    // Get unique keys from both objects
    const allKeys = Array.from(
      new Set([
        ...Object.keys(cancelReasonData),
        ...Object.keys(transformedEntries),
      ])
    );

    let hasMatch = false; // At least one valid match
    let hasMismatch = false; // Any mismatch found

    for (let key of allKeys) {
      const value1 = cancelReasonData[key];
      const value2 = transformedEntries[key];

      // Check if both values are empty or both are not empty
      if ((value1 && !value2) || (!value1 && value2)) {
        hasMismatch = true;
      }

      if (value1 && value2) {
        hasMatch = true;
      }
    }
    if (hasMismatch) {
      return false; // If any mismatch exists, return false
    }
    return hasMatch;
  }

  private buildForm(order: AdnocOrder): void {
    this.form = new UntypedFormGroup({});
    const entryGroup = new UntypedFormGroup({}, [
      this.ValidateQuantityToCancel(),
    ]);
    const returnRequestDocument = new UntypedFormControl('');
    const returnReasonGroup = new UntypedFormControl('', [
      this.ValidateReturnReason(),
    ]);
    const cancelReasonGroup = new UntypedFormGroup({}, [
      this.ValidateCancelReason(),
    ]);

    this.form.addControl('orderCode', new UntypedFormControl(order.code));
    this.form.addControl('entries', entryGroup);
    this.form.addControl('returnReason', returnReasonGroup);
    this.form.addControl('returnRequestDocument', returnRequestDocument);
    this.form.addControl('cancelReason', cancelReasonGroup);

    (order.entries || []).forEach((entry) => {
      const key = entry?.entryNumber?.toString() ?? '';
      if (key) {
        entryGroup.addControl(
          key,
          new UntypedFormControl(0, [
            Validators.min(0),
            Validators.max(this.getMaxAmendQuantity(entry)),
          ])
        );
        cancelReasonGroup.addControl(key, new UntypedFormControl(''));
      }
    });
  }

  protected getFormControl(
    form: UntypedFormGroup,
    entry: OrderEntry
  ): UntypedFormControl {
    return <UntypedFormControl>(
      form.get('entries')?.get(entry.entryNumber?.toString() ?? '')
    );
  }

  /**
   * As discussed, this calculation is moved to SPA side.
   * The calculation and validation should be in backend facade layer.
   */
  getAmendedPrice(entry: OrderEntry): Price {
    const amendedQuantity = this.getFormControl(this.form, entry).value;

    const amendedPrice = Object.assign({}, entry.basePrice);
    amendedPrice.value =
      Math.round((entry.basePrice?.value ?? 0) * amendedQuantity * 100) / 100;

    amendedPrice.formattedValue = formatCurrency(
      amendedPrice.value,
      // TODO: user current language
      'en',
      getCurrencySymbol(amendedPrice.currencyIso ?? '', 'narrow'),
      amendedPrice.currencyIso
    );

    return amendedPrice;
  }

  getMaxAmendQuantity(entry: OrderEntry): number {
    return (
      (this.isCancellation()
        ? entry.cancellableQuantity
        : entry.returnableQuantity) ||
      entry.quantity ||
      0
    );
  }

  isCancellation(): boolean {
    return this.amendType === AmendOrderType.CANCEL;
  }

  getReturnReson(): Observable<IReturnReasons> {
    return this.userIdService.getUserId().pipe(
      switchMap((userId) => {
        this.userId = userId;
        const url = this.OccEndpointsService.buildUrl('returnReason', {
          urlParams: { userId: this.userId },
        });
        return this.http.get<IReturnReasons>(url);
      })
    );
  }

  getCancelReason(): Observable<ICancelReasons> {
    return this.userIdService.getUserId().pipe(
      switchMap((userId) => {
        this.userId = userId;
        const url = this.OccEndpointsService.buildUrl('cancelReason', {
          urlParams: { userId: this.userId },
        });
        return this.http.get<ICancelReasons>(url);
      })
    );
  }

  getFileUploadConfigvalue(key: string) {
    let url = this.OccEndpointsService.buildUrl(AdnocApiEndpoints.configKeys, {
      urlParams: {
        key,
      },
    });
    return this.http.get<AdnocConfigRoot>(url);
  }

  setFile(file: File): void {
    this.fileSubject.next(file);
  }

  submitCancellationRequest(
    payload: any,
    code: string
  ): Observable<ICancellationRequestEntryInputs> {
    const url = this.OccEndpointsService.buildUrl(
      AdnocApiEndpoints.cancelConfirmation,
      {
        urlParams: {
          code,
        },
      }
    );
    return this.http.post<ICancellationRequestEntryInputs>(url, payload);
  }
}
