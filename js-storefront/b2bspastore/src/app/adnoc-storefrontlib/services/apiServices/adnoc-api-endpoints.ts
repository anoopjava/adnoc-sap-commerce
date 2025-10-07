export const AdnocApiEndpoints = {
  primaryProducts: '/primaryproducts?fields=DEFAULT',
  identityTypes: '/identityTypes?fields=DEFAULT',
  countries: '/countries?fields=DEFAULT',
  regions: '/countries/${countryCode}/regions?fields=DEFAULT',
  preferredCommunicationChannels:
    '/preferredCommunicationChannels?fields=DEFAULT',
  genders: '/genders?fields=DEFAULT',
  nationalities: '/nationalities?fields=DEFAULT',
  tradeLicenseAuthorityTypes: '/getTradeLicenseAuthorityTypes',
  designation: '/getDesignationTypes',
  exampleEndpoint: '/exampleEndpoint',
  createUser: '/adnocOrgUsers',
  configKeys: '/adnocConfigs?configKeys=${key}',
  adnocCsTicketCategoryMap: '/users/${userId}/getAdnocCsTicketCategoryMap',
  AdnocTicketAssociatedObjectsMap:
    '/users/current/getAdnocTicketAssociatedObjects?csTicketCategoryMapId=${mapId}',
  adnocSubUserCreation: '/users/current/orgCustomers',
  adnocUserOrdersInfo: '/users/current/getOrderSummary',
  adnocGetCurrentuser: '/orgUsers/current',
  adnocReturnOrder: 'users/current/returns',
  incoTerms: '/incoterms-shipto',
  cancelConfirmation:
    '/orgUsers/current/users/current/orders/${code}/adnocCancellation',
  verifyGigyaLoginToken: '/login/verifyGigyaLoginToken',
};
