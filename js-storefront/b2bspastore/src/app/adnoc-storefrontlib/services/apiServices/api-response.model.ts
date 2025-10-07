export interface Icommon {
  code: string;
  name: string;
}

export interface PrimaryProducts {
  primaryProducts: Icommon[];
}

export interface IdentityTypes {
  identityTypes: never[];
  companyTypes: Icommon[];
}

export interface IcommonIso {
  isocode: string;
  name: string;
}
export interface Countries {
  countries: IcommonIso[];
}

export interface Regions {
  regions: IcommonIso[];
}

export interface CommunicationChannel {
  preferredCommunicationChannels: Icommon[];
}

export interface Geneders {
  genders: Icommon[];
}

export interface Nationalities {
  nationalities: Icommon[];
}

export interface AdnocConfig {
  configKey: string;
  configValue: string;
}

export interface AdnocConfigRoot {
  adnocConfigs: AdnocConfig[];
}

export interface TradeLicenseAuthority {
  tradeLicenseAuthorityTypes: Icommon[];
}

export interface Designations {
  designationTypes: Icommon[];
}

export interface ICurrentUser {
  type: string;
  name: string;
  uid: string;
  active: boolean;
  approvers: any[];
  currency: Currency;
  customerId: string;
  designation: string;
  displayUid: string;
  email: string;
  firstName: string;
  lastName: string;
  orgUnit: OrgUnit;
  roles: string[];
  selected: boolean;
  title: string;
  titleCode: string;
  userRole: string;
  lastlogin?: any;
  isocode?: string;
}

export interface Currency {
  active: boolean;
  isocode: string;
  name: string;
  symbol: string;
}

export interface OrgUnit {
  active: boolean;
  name: string;
  uid: string;
}

export interface ICreditLimit {
  b2BCreditLimit: B2BcreditLimit;
  updatedOn: string;
}

export interface B2BcreditLimit {
  availableCl: string;
  checkRule?: string;
  bankGuarantee: string;
  creditExposure: string;
  currency: string;
  letterOfCredit: string;
  message: string;
  messageV1: string;
  messageV2: string;
  msgId: string;
  msgNumber: string;
  msgType: string;
  payer: string;
  totalCl: string;
  unsecureCl: string;
  utilization: string;
}

export interface userOrdersSummary {
  configValue: string;
  ordersPlacedCount: string;
  returnOrdersCount: string;
  totalOrdersValue: string;
}

export interface IncoTerms {
  incoTerms: Icommon[];
}
