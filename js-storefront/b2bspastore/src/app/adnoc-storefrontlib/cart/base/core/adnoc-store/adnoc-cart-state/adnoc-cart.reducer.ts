import { createReducer, on } from '@ngrx/store';
import {
  deleteEntry,
  storeEntries,
  updateAllDeliveryDate,
  updateEntry,
  updateQuantity,
  clearEntries,
  setConfig, 
} from './adnoc-cart.action';
import { initialConfigState, initialEntryState } from './adnoc-cart.state';

export const entryReducer = createReducer(
  initialEntryState,
  // Store entries
  on(storeEntries, (state, { entries }) => ({
    ...state,
    entries,
  })),
  // Update a specific entry
  on(updateEntry, (state, { updatedEntry }) => ({
    ...state,
    entries: state.entries.map((entry) =>
      entry.entryCode === updatedEntry.entryCode
        ? { ...entry, ...updatedEntry }
        : entry
    ),
  })),
  // Update the date for all entries
  on(updateAllDeliveryDate, (state, { namedDeliveryDate }) => ({
    ...state,
    entries: state.entries.map((entry) => ({
      ...entry,
      namedDeliveryDate, 
    })),
  })), 
  on(deleteEntry, (state, { entryCode }) => ({
    ...state,
    entries: state.entries.filter((entry) => entry.entryCode !== entryCode),  // Remove the entry
  })),
  on(updateQuantity, (state, { entryCode, newQuantity }) => ({
    ...state,
    entries: state.entries.map((entry) =>
      entry.entryCode === entryCode
        ? { ...entry, quantity: newQuantity }    // update Quantity for the entry
        : entry
    ),
  })),
  // Clear all entries
  on(clearEntries, (state) => ({
    ...state,
    entries: [],
  }))
);
// For adnoc config
export const configReducer = createReducer(
  initialConfigState,
  on(setConfig, (state, { config }) => ({
    ...state,
    config: { ...config },
  }))
);