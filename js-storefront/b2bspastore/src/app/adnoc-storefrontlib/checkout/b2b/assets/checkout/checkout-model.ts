import { Cart } from '@spartacus/cart/base/root';

export interface AdnocCart extends Cart {
  creditLimitUsed?: boolean;
  isCreditLimitUsed?: boolean;
}
export interface IOrderStatusParams {
  cartId: string | undefined;
  resultIndicator: string;
  sessionVersion: string;
}

export interface IPaymentCheckoutParams {
  cartId: string | undefined;
  paymentType: string;
  paymentAmount: number;
}

export interface IBankPaymentParams {
  cartId: string | undefined;
  transactionId: string;
}

export interface IadnocConfigs {
  adnocConfigs: AdnocConfig[]
}

export interface AdnocConfig {
  configKey: string
  configValue: string
}
export interface IUrlParams {
  cartId: string;
  isCreditLimitUsed: boolean;
  paymentType: string;
  purchaseOrderNumber: string;
  creditLimitValue: number;
  poDocument: FormData;
}
export interface IOverdue {
  creditSimulation: boolean;
}
export interface IRetriveStatus {
  transaction: Transaction[];
}

export interface Transaction {
  authentication: Authentication;
  response: Response;
  result: string;
  transaction: Transaction2;
}

export interface Authentication {
  transactionId: string;
}

export interface Response {
  gatewayCode: string;
}

export interface Transaction2 {
  authorizationCode: string;
  receipt: string;
  type: string;
}

export interface ISessionData {
  checkoutMode: string;
  merchant: string;
  result: string;
  session: Session;
  successIndicator: string;
  transaction?: ITransaction;
}

export interface ITransaction {
  amount: Amount;
  balance: Balance;
  fees: Fees;
  paymentPage: string;
  paymentPortal: string;
  responseClass: string;
  responseClassDescription: string;
  responseCode: string;
  responseDescription: string;
  transactionID: string;
  uniqueID: string;
}

export interface Session {
  id: string;
  updateStatus: string;
  version: string;
}
export interface ICreditLimit {
  b2BCreditLimit: B2BcreditLimit;
  updatedOn: string;
}

export interface B2BcreditLimit {
  availableCl: string;
  checkRule?: string;
  bankGuarantee: string;
  creditExposure: string;
  currency: string;
  letterOfCredit: string;
  message: string;
  messageV1: string;
  messageV2: string;
  msgId: string;
  msgNumber: string;
  msgType: string;
  payer: string;
  totalCl: string;
  unsecureCl: string;
  utilization: string;
}

export interface ICurrentUser {
  type: string;
  name: string;
  uid: string;
  active: boolean;
  approvers: any[];
  currency: Currency;
  customerId: string;
  designation: string;
  displayUid: string;
  email: string;
  firstName: string;
  lastName: string;
  orgUnit: OrgUnit;
  roles: string[];
  selected: boolean;
  title: string;
  titleCode: string;
  userRole: string;
}

export interface Currency {
  active: boolean;
  isocode: string;
  name: string;
  symbol: string;
}

export interface OrgUnit {
  active: boolean;
  name: string;
  uid: string;
}

export interface IPaymentPayload {
  isCreditLimitUsed: boolean;
  paymentType: string;
  purchaseOrderNumber: string;
  poDocument: FormData;
  creditLimitValue: number;
}

export interface IPaymentPayloadExtended extends IPaymentPayload {
  cartId: string;
}

export interface ICartwsDTO {
  type: string;
  code: string;
  entries: Entry[];
  guid: string;
  subTotal: SubTotal;
  totalDiscounts: TotalDiscounts;
  totalItems: number;
  totalPrice: TotalPrice2;
  totalPriceWithTax: TotalPriceWithTax;
  totalTax: TotalTax;
  isCreditLimitUsed: boolean;
  paymentType: PaymentType;
  purchaseOrderNumber: string;
  totalUnitCount: number;
}

export interface Entry {
  cancellableQuantity: number;
  configurationInfos: any[];
  deliveryAddress: DeliveryAddress;
  division: string;
  entryCode: number;
  entryNumber: number;
  incoTerms: IncoTerms;
  namedDeliveryDate: string;
  product: Product;
  quantity: number;
  returnableQuantity: number;
  statusSummaryList: any[];
  totalPrice: TotalPrice;
  unit: Unit2;
}

export interface DeliveryAddress {
  companyName: string;
  country: Country;
  defaultAddress: boolean;
  email: string;
  firstName: string;
  formattedAddress: string;
  id: string;
  lastName: string;
  line1: string;
  postalCode: string;
  region: Region;
  sapCustomerID: string;
  titleCode: string;
  town: string;
}

export interface Country {
  isocode: string;
}

export interface Region {
  isocode: string;
}

export interface IncoTerms {
  code: string;
  name: string;
}

export interface Product {
  availableForPickup: boolean;
  baseOptions: any[];
  categories: Category[];
  code: string;
  configurable: boolean;
  division: string;
  name: string;
  purchasable: boolean;
  stock: Stock;
  unit: Unit;
  url: string;
}

export interface Category {
  code: string;
  name: string;
}

export interface Stock {
  isValueRounded: boolean;
  stockLevel: number;
  stockLevelStatus: string;
}

export interface Unit {
  code: string;
  name: string;
}

export interface TotalPrice {
  currencyIso: string;
  formattedValue: string;
  priceType: string;
  value: number;
}

export interface IBasePrice {
  formattedValue: string
  value: number
}

export interface Unit2 {
  code: string;
  name: string;
}

export interface SubTotal {
  currencyIso: string;
  formattedValue: string;
  priceType: string;
  value: number;
}

export interface TotalDiscounts {
  currencyIso: string;
  formattedValue: string;
  priceType: string;
  value: number;
}

export interface TotalPrice2 {
  currencyIso: string;
  formattedValue: string;
  priceType: string;
  value: number;
}

export interface TotalPriceWithTax {
  currencyIso: string;
  formattedValue: string;
  priceType: string;
  value: number;
}

export interface TotalTax {
  currencyIso: string;
  formattedValue: string;
  priceType: string;
  value: number;
}

export interface PaymentType {
  code: string;
  displayName: string;
}

export interface IBankFinalize {
  transaction: IBankTransaction;
}

export interface IBankTransaction {
  responseCode: string;
  responseClass: string;
  responseDescription: string;
  responseClassDescription: string;
  language: string;
  approvalCode: string;
  account: string;
  balance: Balance;
  orderID: string;
  amount: Amount;
  fees: Fees;
  cardBrand: string;
  isWalletUsed: string;
  isCaptured: string;
  captureID: string;
  uniqueID: string;
}

export interface Balance {
  value: string;
}

export interface Amount {
  value: string;
}

export interface Fees {
  value: string;
}
