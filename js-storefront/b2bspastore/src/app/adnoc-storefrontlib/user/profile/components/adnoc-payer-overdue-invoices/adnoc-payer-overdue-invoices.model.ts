export interface AdnocOverdueInvoicePayer {
  payer: string;
}
export interface AdnocOverdueInvoice {
  adnocOverdueInvoices: IAdnocOverdueInvoice[];
}
export interface IAdnocOverdueInvoice {
  selected?: boolean;
  documentNumber?: string;
  payer?: string;
  companyCode?: string;
  fiscalYear?: string;
  invoiceAmount?: string;
  dueAmount?: string;
  currency?: string;
  status?: string;
  netDueDate?: string;
  documentDate?: string;
  division?: string;
}
export interface IAdnocInvoicePaymentType {
  paymentTypes: InvoicePaymentType[];
}
export interface InvoicePaymentType {
  code?: string;
  displayName?: string;
}
export interface IAdnocPayerPaidInvoiceList {
  payerId: string;
  totalAmount: number;
  paymentType: string;
  currency: string | null;
  invoiceDetails: InvoicePayloadInfo[];
}
export interface IAdnocPayerPaidSessionResponse {
  checkoutMode?: string;
  merchant?: string;
  result?: string;
  session: ISession;
  successIndicator?: string;
  transaction?: IBankTransaction;
}
export interface ISession {
  id: string;
  updateStatus?: string;
  version?: string;
}
export interface InvoicePayloadInfo {
  companyCode?: string;
  invoiceNumber?: string;
  fiscalYear?: string;
}
export interface IAdnocRetrieveInvoiceResultInfo {
  resultIndicator: string;
}
export interface IAdnocFinalizeInvoiceResultInfo {
  transactionID: string;
}
export interface IAdnocRetrieveInvoicePaidResponse {
  result?: string;
  status?: string;
  currency?: string;
  transaction?: Transaction1[];
}
export interface IAdnocFinalizeInvoicePaidResponse {
  currency?: string;
  transaction?: IBankTransactionResponse;
}
export interface Transaction1 {
  result: string;
  transaction?: Transaction2;
}
export interface Transaction2 {
  authenticationStatus?: string;
  type?: string;
}
export interface OverdueInvoiceResultInfo {
  cardNumber: string;
  receipt: string;
  amount: number;
  currency: string;
  referenceNumber: string;
}
export interface IBankTransaction {
  amount: Amount;
  balance: Balance;
  fees: Fees;
  paymentPage: string;
  paymentPortal: string;
  responseClass: string;
  responseClassDescription: string;
  responseCode: string;
  responseDescription: string;
  transactionId: string;
  uniqueID: string;
}
export interface IBankTransactionResponse {
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
export interface IAdnocCardConfigInfo {
  adnocConfigs: IAdnocCardConfigResponse[];
}
export interface IAdnocCardConfigResponse {
  configKey: string;
  configValue: string;
}
