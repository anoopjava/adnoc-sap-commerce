/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { B2BUnit, PaginationModel } from '@spartacus/core';
import { OrgUnitService } from '@spartacus/organization/administration/core';
import { Observable, Subject } from 'rxjs';
import {
  distinctUntilChanged,
  startWith,
  switchMap,
  takeUntil,
} from 'rxjs/operators';
import { ItemService } from '../../shared/item.service';
import { AdnocListService } from '../../shared/list/adnoc-list.service';
import { TableStructure } from '../../../../../shared/table/table.model';
import { AdnocUnitItemService } from '../services/adnoc-unit-item.service';
import { AdnocCustomFormService } from '../../shared/adnoc-form/adnoc-custom-form.service';

export interface AdnocB2BUnit extends B2BUnit {
  salesOrg?: boolean;
}

@Component({
  selector: 'cx-org-unit-details',
  templateUrl: './adnoc-unit-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: ItemService,
      useExisting: AdnocUnitItemService,
    },
  ],
  host: { class: 'content-wrapper adnoc-org-unit-details' },
  standalone: false,
})
export class AdnocUnitDetailsComponent<T = any> {
  structure$: Observable<TableStructure>;
  model$: Observable<AdnocB2BUnit>;
  isInEditMode$;
  partnerFunction: string = '';
  protected destroy$ = new Subject<void>();
  isUpdatingUnitAllowed: boolean = true;

  constructor(
    protected itemService: ItemService<AdnocB2BUnit>,
    protected service: AdnocListService<T, PaginationModel>,
    protected adnocCustomFormService: AdnocCustomFormService,
    protected orgUnitService?: OrgUnitService
  ) {
    this.structure$ = this.service.getStructure();
    this.model$ = this.itemService.key$.pipe(
      switchMap((code) => this.itemService.load(code)),
      startWith({})
    );
    this.isInEditMode$ = this.itemService.isInEditMode$;
  }

  ngOnInit(): void {
    if (this.orgUnitService) {
      this.isUpdatingUnitAllowed = this.orgUnitService.isUpdatingUnitAllowed();
    }
    this.model$
      .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe((data: any) => {
        if (data.partnerFunction) {
          this.partnerFunction = data.partnerFunction;
          const unitParentInfo = {
            uid: data.uid,
            partnerFunction: data.partnerFunction,
            parentOrgUnitName: data.parentOrgUnit?.name,
          };
          this.adnocCustomFormService.unitParentInfo$.next(unitParentInfo);
        }
      });
  }

  get showLink(): boolean {
    return this.partnerFunction === 'SP' || this.partnerFunction === 'PY';
  }

  onCreateButtonClick(): void {
    this.service.onCreateButtonClick();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
