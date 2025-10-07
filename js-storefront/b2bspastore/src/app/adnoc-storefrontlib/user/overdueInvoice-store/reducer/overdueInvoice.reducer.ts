import { createReducer, on } from '@ngrx/store';
import {
  clearOverdueInvoicePaymentResult,
  saveOverdueInvoicePaymentResult,
} from '../actions/overdueInvoice.actions';
import { OverdueInvoiceResultInfo } from '../../profile/components/adnoc-payer-overdue-invoices/adnoc-payer-overdue-invoices.model';

export interface InvoicePaymentState {
  overdueInvoiceResultInfo: OverdueInvoiceResultInfo | null;
}

export const initialState: InvoicePaymentState = {
  overdueInvoiceResultInfo: null,
};

export const overdueInvoicePaymentReducer = createReducer(
  initialState,
  on(
    saveOverdueInvoicePaymentResult,
    (state, { overdueInvoiceResultInfo }) => ({
      ...state,
      overdueInvoiceResultInfo,
    })
  ),
  on(clearOverdueInvoicePaymentResult, (state) => ({
    ...state,
    overdueInvoiceResultInfo: null,
  }))
);
