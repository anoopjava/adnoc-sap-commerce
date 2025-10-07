/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpRequest } from '@angular/common/http';
import {
  ErrorModel,
  GlobalMessageType,
  HttpErrorHandler,
  HttpResponseStatus,
  Priority,
} from '@spartacus/core';
import { ResponseError } from './bad-cost-center-request.model';
import { AdnocGlobalMessageService } from '../../../../../../core/global-message/facade/adnoc-global-message.service';

@Injectable({
  providedIn: 'root',
})
export class BadCostCenterRequestHandler extends HttpErrorHandler {
  override responseStatus = HttpResponseStatus.BAD_REQUEST;

  constructor(
    protected override globalMessageService: AdnocGlobalMessageService
  ) {
    super(globalMessageService);
  }

  getPriority(): Priority {
    return Priority.NORMAL;
  }

  override hasMatch(errorResponse: HttpErrorResponse): boolean {
    return (
      super.hasMatch(errorResponse) &&
      this.getErrors(errorResponse).some(this.isEntityValidationError) &&
      this.isCostCenterRequest(errorResponse)
    );
  }

  handleError(_request: HttpRequest<any>, response: HttpErrorResponse): void {
    if (this.getErrors(response).some((e) => this.isEntityValidationError(e))) {
      this.globalMessageService.add(
        { key: 'checkoutB2B.invalidCostCenter' },
        GlobalMessageType.MSG_TYPE_ERROR
      );
    }
  }

  protected getErrors(response: HttpErrorResponse): ErrorModel[] {
    return (response.error?.errors || []).filter(
      (error: any) => error.type !== ResponseError.NO_LONGER_VALID
    );
  }

  protected isCostCenterRequest(errorResponse: HttpErrorResponse): boolean {
    if (errorResponse?.url) {
      const url = new URL(errorResponse.url);
      return (
        url.pathname.endsWith('costcenter') &&
        new URLSearchParams(url.search).has('costCenterId')
      );
    }

    return false;
  }

  protected isEntityValidationError(error: ErrorModel): boolean {
    return error.type === ResponseError.INVALID_ENTITY;
  }
}
