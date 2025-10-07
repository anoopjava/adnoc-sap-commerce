import { createAction, props } from '@ngrx/store';
import { B2BcreditLimit } from '../../assets/checkout/checkout-model';

export const saveCreditLimit = createAction(
  '[Credit Limit] Save Credit Limit',
  props<{ creditLimit: B2BcreditLimit }>()
);

export const clearCreditLimit = createAction('[Credit Limit] Clear Credit Limit');