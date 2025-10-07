import { createAction, props } from '@ngrx/store';
import { AdnocConfig, AdnocOrderEntry } from '../../../../../../core/model/adnoc-cart.model';

// Action to store entries
export const storeEntries = createAction(
  '[Entry] Store Entries',
  props<{ entries: AdnocOrderEntry[] }>()
);

// Action to update an entry
export const updateEntry = createAction(
  '[Entry] Update Entry',
  props<{ updatedEntry: AdnocOrderEntry }>()
);

export const updateAllDeliveryDate = createAction(
  '[Entry] update all date',
  props<{ namedDeliveryDate: Date }>()
);

export const deleteEntry = createAction(
  '[Entry] Delete Entry',
  props<{ entryCode: number }>()
);

export const updateQuantity = createAction(
  '[Cart] Update Quantity',
  props<{ entryCode: number; newQuantity: number; }>()
);

export const clearEntries = createAction(
  '[Entry] Clear Entries'
);

export const setConfig = createAction(
  '[Config] Set Config',
  props<{ config: AdnocConfig }>()
);