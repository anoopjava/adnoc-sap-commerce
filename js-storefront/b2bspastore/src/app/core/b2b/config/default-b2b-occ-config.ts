/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

// We need this import for augmentation of OccEndpoints to pick up
import { CartOccEndpoints } from '@spartacus/cart/base/occ';
import { OrderOccEndpoints } from '@spartacus/order/occ';
import { UserAccountOccEndpoints } from '@spartacus/user/account/occ';
import { UserProfileOccEndpoints } from '@spartacus/user/profile/occ';
import { AdnocOccConfig } from '../../occ/config/adnoc-occ-config';

// While it is not strictly required to define checkout endpoints in a separate `UserAccountOccEndpoints`
// variable, type augmentation does require that this file imports `UserAccountOccEndpoints`.
// A good way to make sure the `UserAccountOccEndpoints` import is not removed by mistake is to use
// `UserAccountOccEndpoints` in the code.
const defaultB2bUserAccountOccEndpoints: UserAccountOccEndpoints = {
  user: 'orgUsers/${userId}',
};

const defaultB2bUserProfileOccEndpoints: UserProfileOccEndpoints = {
  userUpdateProfile: 'users/${userId}',
  userCloseAccount: 'users/${userId}',
};

const defaultB2bCartOccEndpoints: CartOccEndpoints = {
  addEntries:
    'orgUsers/${userId}/carts/${cartId}/entries/addToCartEntry?quantity=${quantity}',
};

const defaultB2bOrderOccEndpoints: OrderOccEndpoints = {
  placeOrder: 'orgUsers/${userId}/adnocOrders?fields=FULL',
  scheduleReplenishmentOrder:
    'orgUsers/${userId}/replenishmentOrders?fields=FULL,costCenter(FULL),purchaseOrderNumber,paymentType',
  reorder: 'orgUsers/${userId}/cartFromOrder?orderCode=${orderCode}',
};

export const adnocDefaultB2bOccConfig: AdnocOccConfig = {
  backend: {
    occ: {
      endpoints: {
        ...defaultB2bUserAccountOccEndpoints,
        ...defaultB2bUserProfileOccEndpoints,
        ...defaultB2bCartOccEndpoints,
        ...defaultB2bOrderOccEndpoints,
        cart: 'users/${userId}/carts/${cartId}?fields=DEFAULT,potentialProductPromotions,appliedProductPromotions,potentialOrderPromotions,appliedOrderPromotions,entries(totalPrice(formattedValue),product(images(FULL),stock(FULL)),arrivalSlots,basePrice(formattedValue,value),updateable),totalPrice(formattedValue),totalItems,totalPriceWithTax(formattedValue),totalDiscounts(value,formattedValue),subTotal(formattedValue),totalUnitCount,deliveryItemsQuantity,deliveryCost(formattedValue),totalTax(formattedValue,%20value),pickupItemsQuantity,net,appliedVouchers,productDiscounts(formattedValue),user,saveTime,name,description',
        adnocConfig: '/adnocConfigs?configKeys=${configKey}',
        getPayerList:
          'users/${customerId}/getB2BUnits?fields=DEFAULT&partnerFunction=PY',
        getAddressForEntries:
          'users/current/getShippingAddresses?division=${divisionId}&incoTerms=${incoTerms}',
        updateforcheckout:
          'orgUsers/current/carts/${cartId}/entries/updateforcheckout',
        getPayersList: 'users/current/getCurrentB2BUnits',
        createPayer: 'users/current/setCurrentB2BUnit?b2BUnitUid=${payerId}',
        statementOfAccount: 'users/current/getDownloadStatement?fields=DEFAULT',
        cartToQuotes: '/orgUsers/current/carts/${cartId}/adnocQuotes',
        incoTerms:
          '/users/current/getIncoTerms?division=${divisionId}&pickup=${pickup}',
        pickupAddress: '/pickup-store/${productCode}?fields=DEFAULT',
        getPayerOverdueInvoiceList:
          'users/current/overdue/invoices?fields=DEFAULT',
        getInvoicePaymentType: 'users/current/overdue/payment-types',
        getInvoicePaymentSessionId: 'users/current/overdue/initiate-payment',
        getRetrieveOverdueInvoicePayment:
          'users/current/overdue/retrieve-payment?resultIndicator=${resultIndicator}',
        getFinalizeOverdueInvoicePayment:
          'users/current/overdue/adnoc-payment-bank-overdue-finalize-transaction?transactionID=${transactionID}',
        productAttachments: '/products/${productCode}/documentAttachment',
      },
    },
  },
};
