/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

export enum B2BPaymentTypeEnum {
  ACCOUNT_PAYMENT = 'ACCOUNT',
  CARD_PAYMENT = 'CARD',
}

export interface ICurrentUser {
  type: string
  name: string
  uid: string
  active: boolean
  approvers: any[]
  currency: Currency
  customerId: string
  designation: string
  displayUid: string
  email: string
  firstName: string
  lastName: string
  orgUnit: OrgUnit
  roles: string[]
  selected: boolean
  title: string
  titleCode: string
  userRole: string
}

export interface Currency {
  active: boolean
  isocode: string
  name: string
  symbol: string
}

export interface OrgUnit {
  active: boolean
  name: string
  uid: string
}
