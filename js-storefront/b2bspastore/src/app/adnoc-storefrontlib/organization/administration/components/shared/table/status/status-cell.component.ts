/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { CellComponent } from '../cell.component';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { UrlModule, I18nModule } from '@spartacus/core';
import {
  IconModule,
  OutletContextData,
  PageLayoutService,
} from '@spartacus/storefront';
import { Subject, takeUntil } from 'rxjs';
import { TableDataOutletContext } from '../../../../../../shared/table/table.model';

@Component({
  selector: 'cx-org-status-cell',
  templateUrl: './status-cell.component.html',
  imports: [CommonModule, RouterModule, UrlModule, I18nModule, IconModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StatusCellComponent
  extends CellComponent
  implements OnInit, OnDestroy
{
  private destroy$ = new Subject<void>();
  templateName = '';

  constructor(
    protected override outlet: OutletContextData<TableDataOutletContext>,
    protected pageLayoutService: PageLayoutService,
    private router: Router
  ) {
    super(outlet);
  }

  ngOnInit(): void {
    this.pageLayoutService.templateName$
      .pipe(takeUntil(this.destroy$))
      .subscribe((templateName) => (this.templateName = templateName));
  }

  get showUserStatus(): boolean {
    return (
      this.templateName === 'CompanyPageTemplate' &&
      this.router.url.includes('/users')
    );
  }

  get label(): string | undefined {
    if (this.showUserStatus) {
      return this.model['status'];
    }
    if (this.isActive === undefined) {
      return;
    }
    return this.isActive
      ? 'organization.enabled'
      : 'organizationTranslation.disabled';
  }

  get isActive(): boolean {
    return this.model['active'];
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
