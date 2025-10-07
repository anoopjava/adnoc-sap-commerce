/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { STATUS, TicketStarter } from '@spartacus/customer-ticketing/root';

export interface IAdnocCategory {
  adnocCsTicketCategoryMapList: Adnoccsticketcategorymaplist[];
}

export interface Adnoccsticketcategorymaplist {
  csTicketCategoryMapId?: string;
  requestType?: RequestType;
  requestFor?: RequestType;
  subCategory?: RequestType;
}

export interface RequestType {
  code?: string;
  name?: string;
}

export interface CodeType {
  code?: string;
}

export interface AdnocTicketStarter extends TicketStarter {
  requestType?: CodeType;
  requestFor?: CodeType;
  subCategory?: CodeType;
  csTicketCategoryMapId?: string;
}

export const enum CustomerTicketStaus {
  OPEN = 'OPEN',
  CLOSED = 'CLOSED',
  INPROCESS = 'INPROCESS',
  INPROGRESS = 'INPROGRESS',
  AWAITINGCUSTOMERINPUT = 'AWAITINGCUSTOMERINPUT',
  RESOLUTIONINPROGRESS = 'RESOLUTIONINPROGRESS',
  RESOLVED = 'RESOLVED',
}
