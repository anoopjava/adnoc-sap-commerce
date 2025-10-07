import { createFeatureSelector, createSelector } from '@ngrx/store';
import { AdnocOrderEntry } from '../../../../../../core/model/adnoc-cart.model';
import { ConfigState, EntryState } from './adnoc-cart.state';

// Feature selector
export const selectEntryState = createFeatureSelector<EntryState>('cartEntries');

// Selector for the entries
export const selectEntries = createSelector(
  selectEntryState,
  (state) => state.entries
);

export const selectConfigState = createFeatureSelector<ConfigState>('config');
export const selectConfig = createSelector(
  selectConfigState,
  (state: ConfigState) => state.config
);