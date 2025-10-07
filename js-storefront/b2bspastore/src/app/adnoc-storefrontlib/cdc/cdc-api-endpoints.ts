export const CdcApiEndpoints = {
  login: 'https://accounts.${dataCenter}.gigya.com/accounts.login',
  getTfaProviders:
    'https://accounts.${dataCenter}.gigya.com/accounts.tfa.getProviders',
  initTfa: 'https://accounts.${dataCenter}.gigya.com/accounts.tfa.initTFA',
  getTfaEmails:
    'https://accounts.${dataCenter}.gigya.com/accounts.tfa.email.getEmails',
  sendTfaVerificationCode:
    'https://accounts.${dataCenter}.gigya.com/accounts.tfa.email.sendVerificationCode',
  completeTfaVerification:
    'https://accounts.${dataCenter}.gigya.com/accounts.tfa.email.completeVerification',
  finalizeTfa:
    'https://accounts.${dataCenter}.gigya.com/accounts.tfa.finalizeTFA',
  finalizeRegistration:
    'https://accounts.${dataCenter}.gigya.com/accounts.finalizeRegistration',
  resetPassword:
    'https://accounts.${dataCenter}.gigya.com/accounts.resetPassword',
  setAccountInfo:
    'https://accounts.${dataCenter}.gigya.com/accounts.setAccountInfo',
};
