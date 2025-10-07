/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { GlobalMessageType, SemanticPathService } from '@spartacus/core';
import { OrgUnitService } from '@spartacus/organization/administration/core';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

@Injectable()
export class OrgUnitGuard {
  constructor(
    protected globalMessageService: AdnocGlobalMessageService,
    protected orgUnitService: OrgUnitService,
    protected semanticPathService: SemanticPathService,
    protected router: Router
  ) {}

  canActivate(): boolean | UrlTree {
    const isUpdatingUnitAllowed = this.orgUnitService.isUpdatingUnitAllowed();
    if (!isUpdatingUnitAllowed) {
      this.globalMessageService.add(
        { key: 'organization.notification.notExist' },
        GlobalMessageType.MSG_TYPE_WARNING
      );
      return this.router.parseUrl(
        this.semanticPathService.get('organization') ?? ''
      );
    }
    return isUpdatingUnitAllowed;
  }
}
