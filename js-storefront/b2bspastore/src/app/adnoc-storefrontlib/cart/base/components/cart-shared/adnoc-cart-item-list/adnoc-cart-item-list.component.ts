/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Input,
  OnChanges,
  OnDestroy,
  OnInit,
  Optional,
  SimpleChanges,
  ViewEncapsulation,
  inject,
  signal,
} from '@angular/core';
import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';
import {
  CartItemComponentOptions,
  ConsignmentEntry,
  PromotionLocation,
  SelectiveCartFacade,
  OrderEntry,
} from '@spartacus/cart/base/root';
import { FeatureConfigService, UserIdService } from '@spartacus/core';
import {
  ICON_TYPE,
  OutletContextData,
  PageLayoutService,
} from '@spartacus/storefront';
import _ from 'lodash';
import {
  BehaviorSubject,
  forkJoin,
  Observable,
  of,
  Subject,
  Subscription,
} from 'rxjs';
import {
  distinctUntilChanged,
  map,
  startWith,
  takeUntil,
  tap,
} from 'rxjs/operators';
import {
  AdnocCartConfig,
  AdnocOrderEntry,
  AddressInfo,
  AdnocConfig,
  IncoTermList,
  IncoTerm,
  pointOfServices,
  IpointOfServices,
} from '../../../../../../core/model/adnoc-cart.model';
import { AdnocActiveCartFacade } from '../../../root/facade/adnoc-active-cart.facade';
import { Store } from '@ngrx/store';
import { MultiCartFacade } from '../../../root/facade/adnoc-multi-cart.facade';
import { EntryState } from '../../../core/adnoc-store/adnoc-cart-state/adnoc-cart.state';
import {
  setConfig,
  storeEntries,
  updateQuantity,
} from '../../../core/adnoc-store/adnoc-cart-state/adnoc-cart.action';
import { AdnocActiveCartService } from '../../../core/facade/adnoc-active-cart.service';
import { selectEntries } from '../../../core/adnoc-store/adnoc-cart-state/adnoc-cart.selector';
import { ICalendar } from '../adnoc-date/adnoc-calendar.component';
import { AdnocCartOutlets } from '../../../root/models/cart-outlets.model';
import moment from 'moment-timezone';
import { AdnocGlobalMessageService } from '../../../../../../core/global-message/facade/adnoc-global-message.service';

interface ItemListContext {
  readonly?: boolean;
  hasHeader?: boolean;
  options?: CartItemComponentOptions;
  cartId?: string;
  items?: AdnocOrderEntry[];
  promotionLocation?: PromotionLocation;
  cartIsLoading?: boolean;
}

export interface INestedData {
  [key: string]: { entrie: AdnocOrderEntry[]; count: number };
}
@Component({
  selector: 'adnoc-cart-item-list',
  templateUrl: './adnoc-cart-item-list.component.html',
  styleUrls: ['./adnoc-cart-item-list.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  standalone: false,
})
export class CartItemListComponent
  implements OnInit, OnDestroy, OnChanges, AfterViewInit
{
  subscription = new Subscription();
  protected userId = '';
  iconTypes = ICON_TYPE;
  @Input() readonly: boolean = false;

  @Input() hasHeader: boolean = true;

  @Input() options: CartItemComponentOptions = {
    isSaveForLater: false,
    optionalBtn: null,
    displayAddToCart: false,
  };

  @Input() cartId!: string;

  protected _items: AdnocOrderEntry[] = [];
  form: UntypedFormGroup = new UntypedFormGroup({});
  reqDeliveryDate: { groupKey: string; requestedDate: Date }[] = [];
  addressSelection = false;
  selectedAddress: AddressInfo | undefined;
  selectedShippingAddress!: string;
  addressesMap$ = signal<Record<string, AddressInfo[]>>({});
  incoTermsMap$ = signal<Record<string, IncoTerm[]>>({});
  isPickupStore = false;
  selectedPickupStore!: any;
  isPickupDivision = false;

  @Input('items')
  set items(items: AdnocOrderEntry[]) {
    this._setItems(items);
    this.updateGroupedEntries(this._items);
  }
  get items(): AdnocOrderEntry[] {
    return this._items;
  }

  @Input() promotionLocation: PromotionLocation = PromotionLocation.ActiveCart;

  @Input('cartIsLoading') set setLoading(value: boolean) {
    if (!this.readonly) {
      // Whenever the cart is loading, we disable the complete form
      // to avoid any user interaction with the cart.
      value
        ? this.form.disable({ emitEvent: false })
        : this.form.enable({ emitEvent: false });
      this.cd.markForCheck();
    }
  }
  readonly CartOutlets = AdnocCartOutlets;
  protected destroy$ = new Subject<void>();
  selectedDeliveryDate!: Date;
  configObject!: any;
  minRequestedDelieveryDays$!: Observable<number | undefined>;

  groupedEntries!: INestedData;
  isDateSelected = false;
  private featureConfigService = inject(FeatureConfigService);
  selectAddressDisable = true;
  existingEntries$: Observable<any>;
  templateName = '';
  private groupedEntriesCache = new BehaviorSubject<INestedData>({});
  private previousItems: AdnocOrderEntry[] | null = null;
  selectedDeliveryMode: string = 'delivery';

  constructor(
    protected activeCartService: AdnocActiveCartFacade,
    protected selectiveCartService: SelectiveCartFacade,
    protected userIdService: UserIdService,
    protected multiCartService: MultiCartFacade,
    protected anocActiveCartService: AdnocActiveCartService,
    protected cd: ChangeDetectorRef,
    protected store: Store<{ entriesState: EntryState }>,
    protected globalMessage: AdnocGlobalMessageService,
    protected pageLayoutService: PageLayoutService,
    @Optional() protected outlet?: OutletContextData<ItemListContext>
  ) {
    this.existingEntries$ = this.store.select(selectEntries);
  }

  ngOnInit(): void {
    this.subscription.add(this.getInputsFromContext());
    this.updateGroupedEntries(this.items);
    this.initializeSubscriptions();
    this.initializeCartConfig();
    this.resetCheckboxes();
    this.subscribeToCartEntries();
    this.updateSerialNumber();
    this.getItems(this.items);
  }

  private initializeSubscriptions(): void {
    this.subscription.add(
      this.userIdService
        .getUserId()
        .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
        .subscribe((userId: string) => {
          this.userId = userId;
          this.cd.markForCheck();
        })
    );

    this.subscription.add(
      this.pageLayoutService.templateName$
        .pipe(takeUntil(this.destroy$))
        .subscribe((templateName) => {
          this.templateName = templateName;
          this.cd.markForCheck();
        })
    );
  }

  private initializeCartConfig(): void {
    if (this.isCartOrQuotePage()) {
      this.subscription.add(
        this.activeCartService
          .cartConfig()
          .pipe(
            distinctUntilChanged(),
            takeUntil(this.destroy$),
            map((data) => data.adnocConfigs)
          )
          .subscribe((data: AdnocConfig[]) => {
            if (data) {
              this.configObject = _.mapValues(
                _.keyBy(data, 'configKey'),
                'configValue'
              ) as any;
              this.store.dispatch(setConfig({ config: this.configObject }));
              this.minRequestedDelieveryDays$ = of(
                this.configObject.minRequestedDelieveryDays
              );
              this.cd.markForCheck();
            }
          })
      );
    } else {
      this.minRequestedDelieveryDays$ = this.store.select((state: any) => {
        const daysString = state.config?.config?.minRequestedDelieveryDays;
        const daysNum = Number(daysString);
        return isNaN(daysNum) ? undefined : daysNum;
      });
    }
  }
  
 isDateValid(cartItemDate: Date | null | undefined): boolean {
    if (!cartItemDate) return true;
    const today = new Date();
    today.setHours(0, 0, 0, 0); // Start of today
    const upper = new Date(today);
    const minDays = Number(
      (this.configObject as AdnocConfig)?.minRequestedDelieveryDays
    );
    upper.setDate(today.getDate() + minDays); // Start of "today + min requested delivery days"
    upper.setHours(0, 0, 0, 0); // Ensure time is reset for accurate comparison
    const d = new Date(cartItemDate);
    d.setHours(0, 0, 0, 0); // Normalize time to start of the day for date-only comparison
    if (!isNaN(d.getTime()) && d <= upper) {
      return false;
    }
    return true;
  }

  private isCartOrQuotePage(): boolean {
    return (
      this.templateName === 'CartPageTemplate' ||
      this.templateName === 'QuoteDetailsPageTemplate'
    );
  }

  private resetCheckboxes(): void {
    if (!this.groupedEntries) return;

    const updateEntries = { ...this.groupedEntries };
    _.forEach(updateEntries, (division) => {
      division.entrie = division.entrie.map((entry) => ({
        ...entry,
        isCheckboxChecked: false,
      }));
    });

    this.groupedEntries = updateEntries;
    this.groupedEntriesCache.next(updateEntries);
    this.cd.markForCheck();
  }

  private subscribeToCartEntries(): void {
    this.subscription.add(
      this.anocActiveCartService.cartEntriesStore$
        .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
        .subscribe((data: INestedData) => {
          if (data) {
            const flatgroup = _.flatMap(data, (group) => group?.entrie);
            if (this.isCartOrQuotePage()) {
              this.store.dispatch(storeEntries({ entries: flatgroup }));
              this.cd.markForCheck();
            }
          }
        })
    );
  }

  ngAfterViewInit(): void {
    const hasPickupStore = this.items.some((item) => item.pickupStore);
    const division = this.items.some((item) => item.division === '23');
    this.isPickupDivision = division;
    const incoTerms = this.items.some(
      (item) => item.incoTerms?.code === 'PICKUP'
    );
    if (incoTerms) {
      this.selectedDeliveryMode = 'PICKUP';
    }
    const updatedEntries = _.cloneDeep(this.groupedEntries);
    const foundItem = this.items.find((data) => data.pickupAddress);
    this.selectedPickupStore = foundItem
      ? _.cloneDeep(foundItem).pickupAddress
      : '';

    // Helper to update entry and notify
    const updateEntryAndNotify = (
      groupKey: string,
      entryIndex: number,
      currentEntry: AdnocOrderEntry,
      incoTermsAddressList: AddressInfo[] | null
    ) => {
      if (updatedEntries[groupKey]?.entrie[entryIndex]) {
        updatedEntries[groupKey].entrie[entryIndex] = {
          ...currentEntry,
          incoTermsAddressList,
        };
      }
      this.groupedEntries = updatedEntries;
      this.anocActiveCartService.cartEntriesStore$.next(this.groupedEntries);
      this.cd.markForCheck();
    };

    if (division && incoTerms) {
      _.forEach(updatedEntries, (groupData, groupKey) => {
        if (!groupData?.entrie) return;
        groupData.entrie.forEach((currentEntry, entryIndex) => {
          if (
            currentEntry.division === '23' &&
            currentEntry.incoTerms?.code === 'PICKUP'
          ) {
            this.activeCartService
              .getPickupAddress(currentEntry.product?.code || '')
              .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
              .subscribe({
                next: (response: IpointOfServices) => {
                  const sortedPickupServices = _.sortBy(
                    response.pointOfServices,
                    ['address']
                  );
                  const pickupAddressesAsAddressInfo: AddressInfo[] =
                    sortedPickupServices.map((pos) => {
                      const address = pos.address;
                      return {
                        id: pos.id,
                        companyName: pos.displayName || '',
                        formattedAddress:
                          pos.displayName || address?.formattedAddress || '',
                        line1: address?.line1 || '',
                        line2: address?.line2 || '',
                        town: address?.town || '',
                        postalCode: address?.postalCode || '',
                        country: { isocode: address?.country?.isocode || '' },
                        region: { isocode: address?.region?.isocode || '' },
                        defaultAddress: address?.defaultAddress || false,
                        selectedPickupStore: pos.id || '',
                      };
                    });
                  updateEntryAndNotify(
                    groupKey,
                    entryIndex,
                    currentEntry,
                    pickupAddressesAsAddressInfo
                  );
                },
                error: () => {
                  updateEntryAndNotify(
                    groupKey,
                    entryIndex,
                    currentEntry,
                    null
                  );
                },
              });
          }
        });
      });
    } else {
      _.forEach(updatedEntries, (groupData, groupKey) => {
        if (!groupData?.entrie) return;
        groupData.entrie.forEach((currentEntry, entryIndex) => {
          const incoTermCode = currentEntry.incoTerms?.code || '';
          if (
            (incoTermCode &&
              groupKey &&
              this.templateName === 'CartPageTemplate') ||
            this.templateName === 'QuoteDetailsPageTemplate'
          ) {
            this.activeCartService
              .getAddressForEntries(groupKey, incoTermCode)
              .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
              .subscribe({
                next: (response) => {
                  updateEntryAndNotify(
                    groupKey,
                    entryIndex,
                    currentEntry,
                    response.addresses || null
                  );
                },
                error: () => {
                  updateEntryAndNotify(
                    groupKey,
                    entryIndex,
                    currentEntry,
                    null
                  );
                },
              });
          }
        });
      });
    }

    this.isPickupStore = hasPickupStore;
    this.cd.markForCheck();
    this.fetchIncotermsForCartOrQuote(hasPickupStore);
  }

  onDeliveryModeChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target) {
      this.selectedDeliveryMode = target.value;

      const updatedGroupedEntries = _.cloneDeep(this.groupedEntries);

      _.forEach(updatedGroupedEntries, (groupData) => {
        if (groupData && groupData.entrie) {
          groupData.entrie = groupData.entrie.map((entry) => ({
            ...entry,
            isCheckboxChecked: false,
            incoTermsAddressList: null,
            incoTermsCode: undefined,
            deliveryAddress: undefined,
            requestedShippingAddress: null,
            incoTerms: undefined,
          }));
        }
      });
      this.incoTermsMap$.set({});
      this.groupedEntries = updatedGroupedEntries;
      this.anocActiveCartService.cartEntriesStore$.next(this.groupedEntries);

      // Fetch new incoterms based on the selected delivery mode
      this.fetchIncotermsForCartOrQuote(this.selectedDeliveryMode === 'PICKUP');
      this.cd.markForCheck(); // Ensure UI updates with cleared fields and new incoterm options
    }
  }

  private fetchIncotermsForCartOrQuote(pickup: boolean): void {
    if (!this.isCartOrQuotePage()) return;
    this.incoTermsMap$.set({});
    this.cd.markForCheck();
    const groupKeys = Object.keys(this.groupedEntries);
    const incoTermsRequests = groupKeys.map((groupKey) =>
      this.activeCartService.getIncotermsForEntries(groupKey, pickup)
    );

    if (incoTermsRequests.length) {
      forkJoin(incoTermsRequests).subscribe({
        next: (results) => {
          const incoTermsMap: Record<string, IncoTerm[]> = {};
          results.forEach((result, index) => {
            incoTermsMap[groupKeys[index]] = result.incoTerms;
          });
          this.incoTermsMap$.set(incoTermsMap);
          this.cd.markForCheck();
        },
        error: () => {
          this.incoTermsMap$.set({});
          this.cd.markForCheck();
        },
      });
    }
  }

  /**
   * Handles the selection of Incoterms and fetches corresponding addresses
   * @param incoTermCode The selected incoterm code
   * @param groupKey The group identifier
   * @param entryIndex The index of the entry in the group
   **/
  onIncoTermsHandler(
    incoTermCode: string,
    groupKey: string,
    entryIndex: number
  ): void {
    const updatedEntries = _.cloneDeep(this.groupedEntries);

    // Centralized function to apply updates and notify
    const finalizeIncoTermUpdate = (addresses?: AddressInfo[]) => {
      if (
        updatedEntries[groupKey] &&
        updatedEntries[groupKey].entrie &&
        updatedEntries[groupKey].entrie[entryIndex]
      ) {
        const entryToUpdate = updatedEntries[groupKey].entrie[entryIndex];

        entryToUpdate.incoTermsCode = incoTermCode || undefined;
        entryToUpdate.incoTermsAddressList = addresses || null;
        entryToUpdate.deliveryAddress = undefined; // Clear any previously selected specific delivery address object
        entryToUpdate.requestedShippingAddress = null; // Clear any previously selected shipping address ID from the list

        updatedEntries[groupKey].entrie.forEach((e) => {
          if (e.isCheckboxChecked) {
            e.isCheckboxChecked = false;
          }
        });
      }

      this.groupedEntries = updatedEntries;
      this.anocActiveCartService.cartEntriesStore$.next(this.groupedEntries);
      this.cd.markForCheck();
    };

    if (incoTermCode === 'PICKUP') {
      const productCode =
        updatedEntries[groupKey].entrie[entryIndex].product?.code ?? '';
      // Handle Pickup Point selection
      this.activeCartService
        .getPickupAddress(productCode)
        .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
        .subscribe({
          next: (response: IpointOfServices) => {
            const sortedPickupServices = _.sortBy(response.pointOfServices, [
              'address',
            ]);

            const pickupAddressesAsAddressInfo: AddressInfo[] =
              sortedPickupServices.map((pos) => {
                const address = pos.address;

                return {
                  id: pos.id,
                  companyName: pos.displayName || '',
                  formattedAddress:
                    pos.displayName || address?.formattedAddress || '',
                  line1: address?.line1 || '',
                  line2: address?.line2 || '',
                  town: address?.town || '',
                  postalCode: address?.postalCode || '',
                  country: { isocode: address?.country?.isocode || '' },
                  region: { isocode: address?.region?.isocode || '' },
                  defaultAddress: address?.defaultAddress || false,
                };
              });

            finalizeIncoTermUpdate(pickupAddressesAsAddressInfo);
          },
          error: (err) => {
            finalizeIncoTermUpdate(undefined);
          },
        });
    } else {
      // Handle Delivery address selection based on Incoterm
      this.activeCartService
        .getAddressForEntries(groupKey, incoTermCode)
        .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
        .subscribe({
          next: (response) => {
            finalizeIncoTermUpdate(response.addresses);
          },
          error: (err) => {
            finalizeIncoTermUpdate(undefined);
          },
        });
    }
  }

  getItems(items: AdnocOrderEntry[]): INestedData {
    return _.mapValues(_.groupBy(items, 'division'), (divisionItems) => {
      const entries = [...divisionItems];
      if (entries.length) {
        entries[0] = {
          ...entries[0],
          isCheckboxEnable: !!entries[0].isCheckboxEnable,
          isCheckboxChecked: !!entries[0].isCheckboxChecked,
          checkbox: entries.length > 1,
        };
      }
      return {
        entrie: entries,
        count: entries.length,
      };
    });
  }

  // for shipping address change
  onShippingAddressHandler(
    event: string,
    entry: AdnocOrderEntry,
    addresses: AddressInfo[],
    groupKey: string,
    i: number
  ) {
    const selectedAddress = addresses.find((addr) => addr.id === event);

    const updateEntries = _.cloneDeep(this.groupedEntries);
    if (updateEntries[groupKey] && updateEntries[groupKey].entrie) {
      let groupEntry = updateEntries[groupKey].entrie;
      _.forEach(groupEntry, (entry) => {
        if (entry.isCheckboxChecked) {
          entry.isCheckboxChecked = false;
        }
      });
    }
    updateEntries[groupKey].entrie[i].requestedShippingAddress = event;
    updateEntries[groupKey].entrie[i].deliveryAddress = selectedAddress;
    // Only set deliveryPointOfService for PICKUP and division '23'
    if (
      updateEntries[groupKey].entrie[i].incoTerms?.code === 'PICKUP' &&
      updateEntries[groupKey].entrie[i].division === '23'
    ) {
      updateEntries[groupKey].entrie[i].deliveryPointOfService =
        selectedAddress;
    }

    this.groupedEntries = updateEntries;
    this.cd.detectChanges();

    this.anocActiveCartService.cartEntriesStore$.next(this.groupedEntries);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (
      changes['items'] &&
      this.hasItemsChanged(changes['items'].currentValue)
    ) {
      this.previousItems = _.cloneDeep(changes['items'].currentValue); // Cache for comparison
      this.updateGroupedEntries(changes['items'].currentValue);
    }
  }

  private hasItemsChanged(currentItems: AdnocOrderEntry[]): boolean {
    if (!this.previousItems) return true;

    // Compare relevant properties to avoid unnecessary updates
    return !_.isEqual(
      this.previousItems.map((item) => ({
        division: item.division,
        isCheckboxEnable: item.isCheckboxEnable,
        isCheckboxChecked: item.isCheckboxChecked,
        totalPrice: item.totalPrice,
        quantity: item.quantity,
        entryNumber: item.entryNumber,
      })),
      currentItems.map((item) => ({
        division: item.division,
        isCheckboxEnable: item.isCheckboxEnable,
        isCheckboxChecked: item.isCheckboxChecked,
        totalPrice: item.totalPrice,
        entryNumber: item.entryNumber,
        quantity: item.quantity,
      }))
    );
  }

  private updateGroupedEntries(items: AdnocOrderEntry[]): void {
    if (!items?.length) {
      this.groupedEntries = {};
      this.groupedEntriesCache.next(this.groupedEntries);
      this.cd.markForCheck();
      return;
    }

    const newGroupedEntries = this.getItems(items);
    const flatgroup = _.flatMap(this.groupedEntries, (group) => group?.entrie);
    if (!_.isEqual(this.groupedEntriesCache.value, newGroupedEntries)) {
      this.groupedEntries = newGroupedEntries;
      this.groupedEntriesCache.next(newGroupedEntries);
      this.updateSerialNumber();
      this.cd.detectChanges();
    }
    this.checkDateandDeliveryData(flatgroup);
  }

  checkDateandDeliveryData(data: AdnocOrderEntry[]): void {
    const hasNullValues = data.some(
      (entry) =>
        entry.namedDeliveryDate === null ||
        entry.requestedShippingAddress === null
    );

    this.anocActiveCartService.cartDateAndAddress$.next({
      btnDisable: hasNullValues,
      data,
    });

    this.cd.detectChanges();
  }

  _setItems(items: AdnocOrderEntry[], options?: { forceRerender?: boolean }) {
    this.resolveItems(items, options);
    this.createForm();
  }
  // for requested delivery date handler
  onDateSelected(data: ICalendar, entryNumber: number | undefined, i: number) {
    if (data) {
      this.updateRequestDateForGroup(
        data.groupKey,
        entryNumber,
        data.event?.toDate(),
        i
      );
    }
  }

  // Function to update requestedDeliveryDate and enable isCheckboxEnable for a specific group
  updateRequestDateForGroup(
    groupKey: string,
    entryNumber: number | undefined,
    reqDate: Date,
    index: number
  ): void {
    const updateEntries = { ...this.groupedEntries };
    const currentSystemTime = new Date();
    const updatedReqDate = moment(reqDate)
      .set({
        hour: currentSystemTime.getHours(),
        minute: currentSystemTime.getMinutes(),
        second: currentSystemTime.getSeconds(),
        millisecond: currentSystemTime.getMilliseconds(),
      })
      .toDate();

    if (updateEntries[groupKey]?.entrie) {
      updateEntries[groupKey].entrie = updateEntries[groupKey].entrie.map(
        (entry, i) => ({
          ...entry,
          isCheckboxChecked: false,
          ...(i === index && { namedDeliveryDate: updatedReqDate }),
        })
      );
    }

    this.groupedEntries = updateEntries;
    this.groupedEntriesCache.next(updateEntries);
    this.anocActiveCartService.cartEntriesStore$.next(updateEntries);
    this.cd.markForCheck();
  }

  // for checkbox click to update shipping & delivery date
  updateDeliveryDateForEntries(
    event: Event,
    groupKey: string,
    entryNumber: number | undefined
  ): void {
    const isChecked = (event.target as HTMLInputElement).checked;
    if (isChecked && this.groupedEntries[groupKey]?.entrie) {
      const updateEntries = { ...this.groupedEntries };
      const firstEntry = updateEntries[groupKey].entrie[0];

      updateEntries[groupKey].entrie = updateEntries[groupKey].entrie.map(
        (entry, index) => ({
          ...entry,
          isCheckboxChecked: index === 0,
          requestedShippingAddress: firstEntry.requestedShippingAddress,
          deliveryAddress: firstEntry.deliveryAddress,
          deliveryPointOfService: firstEntry.deliveryPointOfService,
          namedDeliveryDate: firstEntry.namedDeliveryDate,
          incoTermsAddressList: firstEntry.incoTermsAddressList,
          incoTermsCode: firstEntry.incoTermsCode || firstEntry.incoTerms?.code,
        })
      );
      this.cd.detectChanges();
      this.groupedEntries = updateEntries;
      this.groupedEntriesCache.next(updateEntries);
      this.anocActiveCartService.cartEntriesStore$.next(updateEntries);
      this.cd.markForCheck();
    }
  }

  protected getInputsFromContext(): Subscription | undefined {
    return this.outlet?.context$.subscribe((context) => {
      let contextRequiresRerender = false;
      if (context.readonly !== undefined) {
        contextRequiresRerender = this.readonly !== context.readonly;
        this.readonly = context.readonly;
      }
      if (context.hasHeader !== undefined) {
        this.hasHeader = context.hasHeader;
      }
      if (context.options !== undefined) {
        this.options = context.options;
      }
      if (context.cartId !== undefined) {
        this.cartId = context.cartId;
      }
      if (context.promotionLocation !== undefined) {
        this.promotionLocation = context.promotionLocation;
      }
      if (context.cartIsLoading !== undefined) {
        this.setLoading = context.cartIsLoading;
      }
      this.updateItemsOnContextChange(context, contextRequiresRerender);
    });
  }

  protected updateItemsOnContextChange(
    context: ItemListContext,
    contextRequiresRerender: boolean
  ) {
    const preventRedundantRecreationEnabled =
      this.featureConfigService.isEnabled(
        'a11yPreventCartItemsFormRedundantRecreation'
      );
    if (
      context.items !== undefined &&
      (!preventRedundantRecreationEnabled ||
        contextRequiresRerender ||
        this.isItemsChanged(context.items))
    ) {
      this.items = context.items || [];
      this.cd.markForCheck();
      this._setItems(context.items, {
        forceRerender: contextRequiresRerender,
      });
    }
  }

  protected isItemsChanged(newItems: OrderEntry[]): boolean {
    return JSON.stringify(this.items) !== JSON.stringify(newItems);
  }

  /**
   * Resolves items passed to component input and updates 'items' field
   */
  resolveItems(
    items: AdnocOrderEntry[],
    options?: { forceRerender?: boolean }
  ): void {
    if (!items) {
      this._items = [];
      return;
    }

    // The items we're getting from the input do not have a consistent model.
    // In case of a `consignmentEntry`, we need to normalize the data from the orderEntry.
    if (items.every((item) => item.hasOwnProperty('orderEntry'))) {
      this.normalizeConsignmentEntries(items);
    } else {
      this.rerenderChangedItems(items, options);
    }
  }

  normalizeConsignmentEntries(items: AdnocOrderEntry[]) {
    this._items = items.map((consignmentEntry) => {
      const entry = Object.assign(
        {},
        (consignmentEntry as ConsignmentEntry).orderEntry
      );
      entry.quantity = consignmentEntry.quantity;
      return entry;
    });
  }

  /**
   * We'd like to avoid the unnecessary re-renders of unchanged cart items after the data reload.
   * OCC cart entries don't have any unique identifier that we could use in Angular `trackBy`.
   * So we update each array element to the new object only when it's any different to the previous one.
   */
  rerenderChangedItems(
    items: AdnocOrderEntry[],
    options?: { forceRerender?: boolean }
  ) {
    let offset = 0;
    for (
      let i = 0;
      i - offset < Math.max(items.length, this._items.length);
      i++
    ) {
      const index = i - offset;
      if (
        options?.forceRerender ||
        JSON.stringify(this._items?.[index]) !== JSON.stringify(items[index])
      ) {
        if (this._items[index]) {
          this.form?.removeControl(this.getControlName(this._items[index]));
        }
        if (!items[index]) {
          this._items.splice(index, 1);
          offset++;
        } else {
          this._items[index] = items[index];
        }
      }
    }
  }

  /**
   * Creates form models for list items
   */
  createForm(): void {
    this._items.forEach((item, i) => {
      const controlName = this.getControlName(item);
      const control = this.form.get(controlName);
      if (control) {
        if (control.get('quantity')?.value !== item.quantity) {
          control.patchValue({ quantity: item.quantity }, { emitEvent: false });
        }
      } else {
        const group = new UntypedFormGroup({
          entryNumber: new UntypedFormControl(item.entryNumber),
          quantity: new UntypedFormControl(item.quantity, { updateOn: 'blur' }),
          entryCode: new UntypedFormControl(item.entryCode),
        });

        this.form.addControl(controlName, group);
      }

      // If we disable form group before adding, disabled status will reset
      // Which forces us to disable control after including to form object
      if (!item.updateable || this.readonly) {
        this.form.controls[controlName].disable();
      }
    });
  }

  getControlName(item: OrderEntry): string {
    return item.entryNumber?.toString() || '';
  }

  removeEntry(item: AdnocOrderEntry): void {
    if (this.options.isSaveForLater) {
      this.selectiveCartService.removeEntry(item);
    } else if (this.cartId && this.userId) {
      this.multiCartService.removeEntry(
        this.userId,
        this.cartId,
        item.entryNumber as number
      );
    } else {
      this.activeCartService.removeEntry(item);
    }
    delete this.form.controls[this.getControlName(item)];
  }

  getControl(item: AdnocOrderEntry): Observable<UntypedFormGroup> | undefined {
    return this.form.get(this.getControlName(item))?.valueChanges.pipe(
      distinctUntilChanged(),
      takeUntil(this.destroy$),
      startWith(null),
      tap((value) => {
        if (item.updateable && value && !this.readonly) {
          const entryCode = value.entryCode;
          const newQuantity = value.quantity;
          this.store.dispatch(updateQuantity({ entryCode, newQuantity }));
          this.cd.detectChanges();
          if (this.options.isSaveForLater) {
            this.selectiveCartService.updateEntry(
              value.entryNumber,
              value.quantity
            );
          } else if (this.cartId && this.userId) {
            this.multiCartService.updateEntry(
              this.userId,
              this.cartId,
              value.entryNumber,
              value.quantity
            );
          } else {
            this.activeCartService.updateEntry(
              value.entryNumber,
              value.quantity
            );
          }
        }
      }),
      map(() => <UntypedFormGroup>this.form.get(this.getControlName(item)))
    );
  }
  trackByDivision(_: number, item: { key: string }): string {
    return item.key;
  }
  trackByEntry(_: number, entry: AdnocOrderEntry): string {
    return entry.entryNumber?.toString() || '';
  }
  updateSerialNumber(): void {
    let count = 0;
    _.forEach(this.groupedEntries, (division) => {
      division.entrie = division.entrie.map((entry) => ({
        ...entry,
        serialNumber: ++count,
      }));
    });
    this.groupedEntriesCache.next(this.groupedEntries);
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
    this.destroy$.next();
    this.destroy$.complete();
  }
}
