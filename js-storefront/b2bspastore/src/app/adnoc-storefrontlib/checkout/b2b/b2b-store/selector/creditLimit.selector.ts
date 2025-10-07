import { createSelector } from '@ngrx/store';
import { B2BcreditLimit } from '../../assets/checkout/checkout-model';
interface ICreditLimit {
  creditLimit: B2BcreditLimit;
}

export const selectCreditLimitState = (state: { creditLimit: ICreditLimit }) =>
  state.creditLimit;

export const selectCreditLimit = createSelector(
  selectCreditLimitState,
  (state) => state.creditLimit
);
