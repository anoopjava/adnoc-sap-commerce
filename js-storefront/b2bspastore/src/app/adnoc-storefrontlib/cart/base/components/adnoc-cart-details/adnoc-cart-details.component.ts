/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { CartConfigService } from '@spartacus/cart/base/core';
import {
  Cart,
  OrderEntry,
  PromotionLocation,
  SelectiveCartFacade,
} from '@spartacus/cart/base/root';
import { RoutingService, UserIdService } from '@spartacus/core';
import { combineLatest, Observable, of, Subject } from 'rxjs';
import {
  distinctUntilChanged,
  filter,
  map,
  takeUntil,
  tap,
} from 'rxjs/operators';
import * as _ from 'lodash';
import {
  AddressInfo,
  AdnocOrderEntry,
  pointOfServices,
} from '../../../../../core/model/adnoc-cart.model';
import { AdnocActiveCartFacade } from '../../root/facade/adnoc-active-cart.facade';
import { Store } from '@ngrx/store';
import { EntryState } from '../../core/adnoc-store/adnoc-cart-state/adnoc-cart.state';
import {
  deleteEntry,
  storeEntries,
  updateEntry,
} from '../../core/adnoc-store/adnoc-cart-state/adnoc-cart.action';
import { selectEntries } from '../../core/adnoc-store/adnoc-cart-state/adnoc-cart.selector';
import {
  IBasePrice,
  TotalPrice,
} from '../../../../checkout/b2b/assets/checkout/checkout-model';
import { AdnocAuthService } from '../../../../../core/src/auth/user-auth/facade/adnoc-auth.service';

@Component({
  selector: 'adnoc-cart-details',
  templateUrl: './adnoc-cart-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class AdnocCartDetailsComponent implements OnInit, OnDestroy {
  cart$!: Observable<Cart>;
  entries$!: Observable<AdnocOrderEntry[]>;
  cartLoaded$!: Observable<boolean>;
  loggedIn = false;
  promotionLocation: PromotionLocation = PromotionLocation.ActiveCart;
  selectiveCartEnabled: boolean | undefined;
  sortedEntries!: OrderEntry[];
  existingEntries$;
  protected destroy$ = new Subject<void>();
  updatedArray!: AdnocOrderEntry[] & OrderEntry[];

  constructor(
    protected activeCartService: AdnocActiveCartFacade,
    protected selectiveCartService: SelectiveCartFacade,
    protected authService: AdnocAuthService,
    protected userIdService: UserIdService,
    protected routingService: RoutingService,
    protected cartConfig: CartConfigService,
    protected cd: ChangeDetectorRef,
    protected store: Store<{ entriesState: EntryState }>
  ) {
    this.existingEntries$ = this.store.select(selectEntries);
  }

  ngOnInit() {
    this.cart$ = this.activeCartService.getActive();

    this.entries$ = this.activeCartService.getEntries().pipe(
      filter((entries: AdnocOrderEntry[]) => {
        if (!entries.length) {
          this.store.dispatch(storeEntries({ entries: [] }));
          return false;
        }

        this.existingEntries$
          .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
          .subscribe((data) => {
            if (data.length) {
              this.handleDeletedEntries(data, entries);
              const updatedEntries = this.updateEntryNumbers(data, entries);
              this.updatedArray = this.mergeAndSortEntries(
                updatedEntries,
                entries
              );

              const finalArray = this.filterFinalArray(
                this.updatedArray,
                entries
              );
              const priceMap = this.createPriceMap(entries);

              const addressMap = new Map<number, AddressInfo>();
              const pointOfServiceMap = new Map<number, pointOfServices>();
              this.sortedEntries = this.processFinalArray(
                finalArray,
                priceMap,
                addressMap,
                pointOfServiceMap
              );
            } else {
              this.sortedEntries = this.processEntriesWithoutData(entries);
            }

            this.cd.detectChanges();
          });

        return true;
      })
    );

    this.selectiveCartEnabled = this.cartConfig.isSelectiveCartEnabled();

    this.cartLoaded$ = combineLatest([
      this.activeCartService.isStable(),
      this.selectiveCartEnabled
        ? this.selectiveCartService.isStable()
        : of(false),
      this.authService.isUserLoggedIn(),
    ]).pipe(
      tap(([, , loggedIn]) => (this.loggedIn = loggedIn)),
      map(([cartLoaded, sflLoaded, loggedIn]) =>
        loggedIn && this.selectiveCartEnabled
          ? cartLoaded && sflLoaded
          : cartLoaded
      )
    );
  }

  handleDeletedEntries(
    data: AdnocOrderEntry[],
    entries: AdnocOrderEntry[]
  ): void {
    const deletedEntries = data.filter(
      (entry) =>
        !entries.some((entryData) => entry.entryCode === entryData.entryCode)
    );
    if (deletedEntries.length) {
      const entryCode = deletedEntries[0].entryCode || 0;
      this.store.dispatch(deleteEntry({ entryCode }));
    }
  }

  updateEntryNumbers(
    data: AdnocOrderEntry[],
    entries: AdnocOrderEntry[]
  ): AdnocOrderEntry[] {
    return _.map(_.cloneDeep(data), (entry) => {
      const entryData = _.find(
        entries,
        (item) => item.entryCode === entry.entryCode
      );
      entry.entryNumber = entryData?.entryNumber;
      return entry;
    });
  }

  mergeAndSortEntries(
    updatedEntries: AdnocOrderEntry[],
    entries: AdnocOrderEntry[]
  ): AdnocOrderEntry[] {
    const sortedArray = _.sortBy(updatedEntries, ['entryNumber']);
    return _.unionBy(sortedArray, entries, 'entryCode');
  }

  filterFinalArray(
    updatedArray: AdnocOrderEntry[],
    entries: AdnocOrderEntry[]
  ): AdnocOrderEntry[] {
    return updatedArray.filter((entry) =>
      entries.some((data) => entry.entryCode === data.entryCode)
    );
  }

  createPriceMap(
    entries: AdnocOrderEntry[]
  ): Map<
    number,
    { totalPrice: TotalPrice; quantity: number; basePrice: IBasePrice }
  > {
    const priceMap = new Map<
      number,
      { totalPrice: TotalPrice; quantity: number; basePrice: IBasePrice }
    >();
    _.forEach(entries, (entry) => {
      if (entry?.entryCode) {
        priceMap.set(entry.entryCode, {
          totalPrice: entry.totalPrice as TotalPrice,
          quantity: entry.quantity || 0,
          basePrice: entry.basePrice as IBasePrice,
        });
      }
    });
    return priceMap;
  }

  processFinalArray(
    finalArray: AdnocOrderEntry[],
    priceMap: Map<
      number,
      { totalPrice: TotalPrice; quantity: number; basePrice: IBasePrice }
    >,
    addressMap: Map<number, AddressInfo>,
    pointOfServiceMap: Map<number, pointOfServices>
  ): AdnocOrderEntry[] {
    return _.chain(finalArray)
      .map((item) => {
        const priceData = item.entryCode
          ? priceMap.get(item.entryCode)
          : undefined;
        const deliveryAddress = item.entryCode
          ? addressMap.get(item.entryCode)
          : undefined;
        const deliveryPointOfService = item.entryCode
          ? pointOfServiceMap.get(item.entryCode)
          : undefined;

        if (priceData || deliveryAddress || deliveryPointOfService) {
          return {
            ...item,
            totalPrice: priceData
              ? {
                  ...item.totalPrice,
                  formattedValue: priceData.totalPrice.formattedValue,
                  value: priceData.totalPrice.value,
                }
              : item.totalPrice,
            quantity: priceData ? priceData.quantity : item.quantity,
            deliveryAddress: deliveryAddress || item.deliveryAddress,
            deliveryPointOfService:
              deliveryPointOfService || item.deliveryPointOfService,
            basePrice: priceData ? priceData.basePrice : item.basePrice,
          };
        }
        return item;
      })
      .sortBy('division')
      .map((obj) => this.mapAdditionalFields(obj))
      .value() as AdnocOrderEntry[];
  }

  mapAdditionalFields(obj: AdnocOrderEntry): AdnocOrderEntry {
    const deliveryPointOfServiceCopy = obj?.deliveryPointOfService
      ? _.cloneDeep(obj.deliveryPointOfService)
      : undefined;

    return {
      ...obj,
      namedDeliveryDate: obj?.namedDeliveryDate || null,
      requestedShippingAddress:
        obj?.deliveryAddress?.id || obj.requestedShippingAddress || null,
      deliveryAddress: obj?.deliveryAddress || undefined,
      pickupAddress: deliveryPointOfServiceCopy || undefined, // Use the copied value
      incoTermsAddressList: obj?.incoTermsAddressList
        ? obj?.incoTermsAddressList
        : obj?.deliveryAddress
        ? [obj?.deliveryAddress]
        : undefined,
      incoTermsCode: obj?.incoTermsCode || undefined,
      incoTerms: obj?.incoTerms || undefined,
      pickupStore:
        obj.incoTerms?.code === 'PICKUP' || obj.incoTermsCode === 'PICKUP'
          ? true
          : false,
      quantity: obj?.quantity || 0,
    };
  }

  processEntriesWithoutData(entries: AdnocOrderEntry[]): AdnocOrderEntry[] {
    return _.chain(entries)
      .sortBy('division')
      .map((obj) => this.mapAdditionalFields(obj))
      .value() as AdnocOrderEntry[];
  }

  saveForLater(item: AdnocOrderEntry) {
    if (this.loggedIn) {
      this.activeCartService.removeEntry(item);
      this.selectiveCartService.addEntry(
        item.product?.code ?? '',
        item.quantity ?? 0
      );
    } else {
      this.routingService.go({ cxRoute: 'login' });
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
