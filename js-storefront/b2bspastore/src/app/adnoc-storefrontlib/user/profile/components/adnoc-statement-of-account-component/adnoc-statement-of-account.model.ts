export interface AdnocStatementOfAccount {
  b2bUnitUid: string;
  currency: string;
}

export interface AdnocPdfStatement {
  output: Output[];
}

export interface Output {
  statementOfAccountB64: string[];
  statusCode: string[];
  message: string[];
}
