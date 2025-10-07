interface BaseResponse {
  callId: string;
  errorCode: number;
  statusCode: number;
  statusReason: string;
  time: string;
}

export interface CdcFlowState {
  loginResponse: LoginResponse | null;
  regToken: string;
  tfaProvidersResponse: TfaProvidersResponse | null;
  initTfaResponse: InitTfaResponse | null;
  tfaEmailsResponse: TfaEmailsResponse | null;
  tfaSendCodeResponse: TfaSendCodeResponse | null;
  completeVerificationResponse: CompleteVerificationResponse | null;
  finalizeTfaResponse: FinalizeTfaResponse | null;
  finalizeRegistrationResponse: FinalizeRegistrationResponse | null;
}

export interface SessionInfo {
  expires_in?: string;
  cookieName?: string;
  cookieValue?: string;
}

export interface LoginResponse extends BaseResponse {
  errorDetails: string;
  errorMessage: string;
  UID: string;
  UIDSignature: string;
  signatureTimestamp: string;
  regToken: string;
  sessionInfo: SessionInfo;
}

export interface Provider {
  name: string;
}

export interface TfaProvidersResponse extends BaseResponse {
  activeProviders: Provider[];
  inactiveProviders: Provider[];
  pendingOptin: Provider[];
}

export interface InitTfaResponse extends BaseResponse {
  gigyaAssertion?: string;
}

export interface Email {
  id: string;
  obfuscated: string;
  lastVerification?: string;
}

export interface TfaEmailsResponse extends BaseResponse {
  emails?: Email[];
}

export interface TfaSendCodeResponse extends BaseResponse {
  phvToken?: string;
}

export interface CompleteVerificationResponse extends BaseResponse {
  providerAssertion?: string;
}

export interface FinalizeTfaResponse extends BaseResponse {}

export interface FinalizeRegistrationResponse extends BaseResponse {
  sessionInfo?: SessionInfo;
  UID?: string;
  UIDSignature?: string;
  signatureTimestamp?: string;
}

export interface RequestPasswordReset extends BaseResponse {}

export interface ResetPassword extends BaseResponse {
    UID: string;
}

export interface UpdatePassword extends BaseResponse {}