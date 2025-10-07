import { createAction, props } from '@ngrx/store';
import { OverdueInvoiceResultInfo } from '../../profile/components/adnoc-payer-overdue-invoices/adnoc-payer-overdue-invoices.model';

export const saveOverdueInvoicePaymentResult = createAction(
  '[OverdueInvoicePayment] Save OVerdue Invoice Payment Result',
  props<{ overdueInvoiceResultInfo: OverdueInvoiceResultInfo }>()
);

export const clearOverdueInvoicePaymentResult = createAction(
  '[OverdueInvoicePayment] Clear OVerdue Invoice Payment Result'
);
