/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

export interface OrganizationUserRegistration {
  primaryProduct: string;
  companyType: string;
  companyName: string;
  companyEmail: string;
  companyWebsite?: string;
  companyPhoneNumber: string;
  companyMobileNumber?: string;
  taxRegistrationNumber: string;
  tradeLicenseNumber: string;
  vatNumber: string;
  supportedDocument?: File | null;
  faxNumber?: string;
  companyAddressStreet: string;
  companyAddressStreetLine2?: string;
  streetName1: string;
  streetName2: string;
  companyAddressCountryIso?: string;
  companyAddressRegion?: string;
  companyAddressPostalCode: string;
  preferredCommunicationChannel?: string;
  firstName: string;
  lastName: string;
  titleCode: string;
  gender: string;
  nationality: string;
  identityType: string;
  identificationNumber: string;
  designation: string;
  email: string;
  telephone: string;
  mobileNumber?: string;
}
