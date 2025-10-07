/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ConsignmentEntry,
  DeliveryMode,
  DeliveryOrderEntryGroup,
  OrderEntry,
  PickupOrderEntryGroup,
  PromotionResult,
  Voucher,
} from '@spartacus/cart/base/root';
import {
  B2BUser,
  CostCenter,
  PaginationModel,
  PointOfService,
  Price,
  Principal,
  SortModel,
} from '@spartacus/core';
import { AdnocOrderEntry } from '../../../../core/model/adnoc-cart.model';
import { Address } from '../../../../core/src/model/address.model';
import { B2BUnit } from '../../../../core/src/model/org-unit.model';
import { PaymentDetails } from '../../../../core/src/model/payment.model';

export interface CancelOrReturnRequestEntryInput {
  orderEntryNumber?: number;
  quantity?: number;
}

export interface ReturnRequestEntryInputList {
  orderCode?: string;
  returnRequestEntryInputs?: CancelOrReturnRequestEntryInput[];
}

export interface CancellationRequestEntryInputList {
  cancellationRequestEntryInputs?: CancelOrReturnRequestEntryInput[];
}

export interface Unit {
  name: string;
}
export interface AdnocReturnOrderEntry extends OrderEntry {
  unit?: Unit;
  namedDeliveryDate?: string;
}
export interface ReturnRequestEntry {
  orderEntry?: AdnocReturnOrderEntry;
  expectedQuantity?: number;
  refundAmount?: Price;
}

export interface ReturnRequest {
  cancellable?: boolean;
  code?: string;
  creationTime?: Date;
  deliveryCost?: Price;
  order?: Order;
  refundDeliveryCost?: boolean;
  returnEntries?: ReturnRequestEntry[];
  returnLabelDownloadUrl?: string;
  rma?: string;
  status?: string;
  subTotal?: Price;
  totalPrice?: Price;
  comment?: string;
}

export interface ReturnRequestList {
  returnRequests?: ReturnRequest[];
  pagination?: PaginationModel;
  sorts?: SortModel[];
}

export interface ReturnRequestModification {
  status?: string;
}

export interface Consignment {
  code?: string;
  deliveryPointOfService?: PointOfService;
  entries?: ConsignmentEntry[];
  shippingAddress?: Address;
  status?: string;
  statusDate?: Date;
  trackingID?: string;
}

export interface Category {
  code: string;
  name: string;
  url: string;
}
export interface OrderHistory {
  code?: string;
  deliveryAddress?: IbillingAddress;
  guid?: string;
  placed?: Date;
  status?: string;
  statusDisplay?: string;
  total?: Price;
  costCenter?: CostCenter;
  purchaseOrderNumber?: string;
  orgUnit?: B2BUnit;
  orgCustomer?: B2BUser;
  category?: any;
  categories?: Category[];
}
export interface IbillingAddress {
  billingAddress: boolean
  defaultAddress: boolean
  editable: boolean
  sapCustomerID: string
  shippingAddress: boolean
  visibleInAddressBook: boolean
}
export interface OrderHistoryList {
  orders?: OrderHistory[];
  pagination?: PaginationModel;
  sorts?: SortModel[];
}

export interface Order {
  appliedOrderPromotions?: PromotionResult[];
  appliedProductPromotions?: PromotionResult[];
  appliedVouchers?: Voucher[];
  calculated?: boolean;
  code?: string;
  consignments?: Consignment[];
  costCenter?: CostCenter;
  created?: Date;
  deliveryAddress?: Address;
  deliveryCost?: Price;
  deliveryItemsQuantity?: number;
  deliveryMode?: DeliveryMode;
  deliveryOrderGroups?: DeliveryOrderEntryGroup[];
  deliveryStatus?: string;
  deliveryStatusDisplay?: string;
  entries?: AdnocReturnOrderEntry[];
  guestCustomer?: boolean;
  guid?: string;
  net?: boolean;
  orderDiscounts?: Price;
  orgCustomer?: B2BUser;
  orgUnit?: B2BUnit;
  paymentInfo?: PaymentDetails;
  pickupItemsQuantity?: number;
  pickupOrderGroups?: PickupOrderEntryGroup[];
  productDiscounts?: Price;
  purchaseOrderNumber?: string;
  site?: string;
  status?: string;
  statusDisplay?: string;
  store?: string;
  subTotal?: Price;
  totalDiscounts?: Price;
  totalItems?: number;
  totalPrice?: Price;
  totalPriceWithTax?: Price;
  totalTax?: Price;
  unconsignedEntries?: AdnocOrderEntry[];
  user?: Principal;
  returnable?: boolean;
  cancellable?: boolean;
  replenishmentOrderCode?: any;
  sapOrderCode?: string;
  shippingCondition?: shippingInfo;
}

export interface orderPaymentDetails extends PaymentDetails {
  creditLimitAmount?: number;
}

export interface shippingInfo {
  name?: string;
}

export interface PaymentInfo {
  amount?: number;
  cardNumber?: string;
  creditLimitAmount?: number;
  defaultPayment?: boolean;
  saved?: boolean;
  account?: string;
}

export interface PaymentTransaction {
  paymentInfo?: PaymentInfo;
  plannedAmount?: number;
  rrnNumber?: string;
}
