/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { OrgUnitService } from '@spartacus/organization/administration/core';
import { AdnocUnitTreeService } from '../services/adnoc-unit-tree.service';

@Component({
    selector: 'cx-org-unit-list',
    templateUrl: './adnoc-unit-list.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'adnoc-org-unit-list' },
    standalone: false
})
export class AdnocUnitListComponent {
  isUpdatingUnitAllowed: boolean = true;
  constructor(
    protected unitTreeService: AdnocUnitTreeService,
    protected orgUnitService?: OrgUnitService
  ) {}

  ngOnInit(): void {
    if (this.orgUnitService) {
      this.isUpdatingUnitAllowed = this.orgUnitService.isUpdatingUnitAllowed();
    }
  }
  expandAll() {
    this.unitTreeService.expandAll();
  }

  collapseAll() {
    this.unitTreeService.collapseAll();
  }
}
