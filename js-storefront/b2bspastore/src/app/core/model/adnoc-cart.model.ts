import { OrderEntry } from '@spartacus/cart/base/root';
import { Product } from '@spartacus/core';

export interface AdnocOrderEntry extends OrderEntry {
  entryCode?: number;
  division?: string;
  checkbox?: boolean;
  deliveryAddress?: AddressInfo;
  deliveryPointOfService?: any;
  pickupAddress?: any;
  namedDeliveryDate?: Date | null;
  requestedShippingAddress?: string | null;
  isCheckboxEnable?: boolean;
  isCheckboxChecked?: boolean;
  incoTermsAddressList?: AddressInfo[] | null;
  incoTermsCode?: string;
  pickupStore?: boolean;
  incoTerms?: IncoTerm;
  serialNumber?: number;
  quoteEntryDiscount?: QuoteEntryDiscount;
  unit?: Unit;
  quantityShipped?: number;
  quantityPending?: number;
  isCancellable?: boolean;
  quantityReturned?: number;
  sapLineItemStatus?: string;
  discounts?: DiscountTax[];
  tax?: DiscountTax;
}

export interface QuoteEntryDiscount {
  currencyIso?: string;
  value?: number;
}

export interface DiscountTax {
  code?: string;
  currencyIso?: string;
  value?: number;
  formattedValue?: string;
}
export interface IdeliveryAddress {
  country: isocode;
  defaultAddress: boolean;
  email: string;
  firstName: string;
  formattedAddress: string;
  id: string;
  lastName: string;
  line1: string;
  postalCode: string;
  region: isocode;
  titleCode: string;
  town: string;
}

export interface isocode {
  isocode: string;
}

export interface AdnocCartConfig {
  adnocConfigs: AdnocConfig[];
}
export interface AdnocConfig {
  minRequestedDelieveryDays: number;
  maxRequestedDelieveryDays: number;
}
export interface IPayer {
  b2bUnitListData: payerInfo[];
}
export interface payerInfo {
  active?: boolean;
  parentOrgUnit?: ParentOrgUnit;
  uid: string;
  name?: string;
}
export interface ParentOrgUnit {
  active: boolean;
  uid: string;
}
export interface IAddress {
  addresses: AddressInfo[];
}

export interface AddressInfo {
  selected?: boolean;
  country: IsoCode;
  defaultAddress: boolean;
  formattedAddress: string;
  id: string;
  line1: string;
  line2: string;
  postalCode: string;
  region: IsoCode;
  town: string;
  requestedShippingAddress?: string;
  companyName?: string;
  sapCustomerID?: string;
  selectedPickupStore?: string;
}

export interface IsoCode {
  isocode: string;
}

export interface ICartQuotes {
  cartId: string;
  orderEntryList: IOrderEntries;
}

export interface IOrderEntries {
  orderEntries: OrderEntries[];
}

export interface OrderEntries {
  entryNumber: number;
  deliveryAddress: DeliveryAddress;
  namedDeliveryDate: string;
}

export interface DeliveryAddress {
  id: string;
}

export interface ICartModification {
  cartModifications: CartModification[];
}

export interface CartModification {
  entry: NewEntry;
  quantity: number;
  quantityAdded: number;
  statusCode: string;
}

export interface NewEntry {
  cancellableQuantity: number;
  deliveryAddress: CheckoutDeliveryAddress;
  entryNumber: number;
  namedDeliveryDate: string;
  returnableQuantity: number;
}

export interface CheckoutDeliveryAddress {
  defaultAddress: boolean;
  id: string;
}

export interface IncoTermList {
  incoTerms: IncoTerm[];
}

export interface IncoTerm {
  code: string;
  name: string;
}

export interface Unit {
  name: string;
  code: string;
}

export interface IpointOfServices {
  pointOfServices: pointOfServices[];
}

export interface pointOfServices {
  address: Address;
  displayName: string;
  geoPoint: GeoPoint;
  name: string;
  id: string;
}

export interface Address {
  country: Country;
  defaultAddress: boolean;
  formattedAddress: string;
  id: string;
  line1: string;
  line2: string;
  phone: string;
  postalCode: string;
  region: Region;
  town: string;
}

export interface Country {
  isocode: string;
}

export interface Region {
  isocode: string;
}

export interface GeoPoint {
  latitude: number;
  longitude: number;
}

export interface PaymentType {
  code?: string;
  displayName?: string;
}

export interface adnocProduct extends Product {
  minOrderQuantity: number;
  maxOrderQuantity: number;
}
