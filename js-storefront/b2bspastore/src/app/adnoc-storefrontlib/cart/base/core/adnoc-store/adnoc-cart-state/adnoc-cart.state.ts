import { AdnocConfig, AdnocOrderEntry } from "../../../../../../core/model/adnoc-cart.model";

export interface EntryState {
  entries: AdnocOrderEntry[];
}

export const initialEntryState: EntryState = {
  entries: [],
};

export interface ConfigState {
  config: AdnocConfig | {};
}

export const initialConfigState: ConfigState = {
  config: {},
};