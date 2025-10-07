import { createSelector } from '@ngrx/store';
import { OverdueInvoiceResultInfo } from '../../profile/components/adnoc-payer-overdue-invoices/adnoc-payer-overdue-invoices.model';

export interface InvoicePaymentStatus {
  overdueInvoiceResultInfo: OverdueInvoiceResultInfo;
}

export interface InvoicePaymentState {
  overdueInvoicePaymentStatus: InvoicePaymentStatus;
}

export const selectOverdueInvoicePaymentState = (state: {
  overdueInvoicePaymentStatus: InvoicePaymentStatus;
}) => state.overdueInvoicePaymentStatus;

export const selectPaymentResultInfo = createSelector(
  selectOverdueInvoicePaymentState,
  (state) => state?.overdueInvoiceResultInfo
);
