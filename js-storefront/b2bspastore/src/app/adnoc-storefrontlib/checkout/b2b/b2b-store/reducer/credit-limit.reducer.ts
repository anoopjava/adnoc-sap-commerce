import { createReducer, on } from '@ngrx/store';
import {
  saveCreditLimit,
  clearCreditLimit,
} from '../actions/creditLimit.actions';
import { B2BcreditLimit } from '../../assets/checkout/checkout-model';

export interface CreditLimitState {
  creditLimit: B2BcreditLimit | null;
}

export const initialState: CreditLimitState = {
  creditLimit: null,
};

export const creditLimitReducer = createReducer(
  initialState,
  on(saveCreditLimit, (state, { creditLimit }) => ({
    ...state,
    creditLimit,
  })),
  on(clearCreditLimit, (state) => ({
    ...state,
    creditLimit: null,
  }))
);
