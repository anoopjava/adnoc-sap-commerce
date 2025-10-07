/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  HostBinding,
  inject,
  Input,
  OnDestroy,
  OnInit,
  ViewEncapsulation,
} from '@angular/core';
import {
  PaginationModel,
  Translatable,
  useFeatureStyles,
} from '@spartacus/core';
import { ICON_TYPE, PageLayoutService, TrapFocus } from '@spartacus/storefront';
import { Table, TableStructure } from '../../../../../shared/table/table.model';
import { Observable, Subject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ItemService } from '../item.service';
import { OrganizationTableType } from '../organization.model';
import { CreateButtonType, AdnocListService } from './adnoc-list.service';
import { EntitiesModel } from '../../../../../../core/src/model/misc.model';
import { AdnocUpdatedUnitTreeService } from '../../unit/services/adnoc-unit-item-update.service';
import { B2BUnitTreeNode } from '../../../core/model/unit-node.model';
import { AdnocUnitTreeService } from '../../unit/services/adnoc-unit-tree.service';

@Component({
  selector: 'cx-org-list',
  templateUrl: './adnoc-list.component.html',
  styleUrls: ['./adnoc-list.component.scss'],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'adnoc-org-list' },
  standalone: false,
})
export class AdnocListComponent<T = any, P = PaginationModel>
  implements OnInit, OnDestroy
{
  readonly trapFocus = TrapFocus;

  @HostBinding('class.ghost') hasGhostData = false;
  protected unitTreeService = inject(AdnocUnitTreeService);
  constructor(
    protected adnocListService: AdnocListService<T, P>,
    protected adnocUpdateListService: AdnocUpdatedUnitTreeService,
    protected organizationItemService: ItemService<T>,
    protected pageLayoutService: PageLayoutService
  ) {
    useFeatureStyles('a11yOrganizationListHeadingOrder');
    useFeatureStyles('a11yListOversizedFocus');
    useFeatureStyles('a11yOrganizationLinkableCells');
    this.currentKey$ = this.organizationItemService.key$;
    this.listData$ = this.adnocListService.getData().pipe(
      tap((data) => {
        this.sortCode = data?.pagination?.sort;
        this.hasGhostData = this.adnocListService.hasGhostData(data);
      })
    );
  }

  @HostBinding('class')
  viewType!: OrganizationTableType;

  domainType!: string;

  sortCode: string | undefined;

  iconTypes = ICON_TYPE;

  createButtonAllTypes = CreateButtonType;

  createButtonType!: CreateButtonType;

  finalUnitInfo: EntitiesModel<B2BUnitTreeNode> | undefined;

  readonly currentKey$!: Observable<string | undefined>;

  structure$!: Observable<TableStructure>;

  readonly listData$!: Observable<EntitiesModel<T> | undefined>;

  @Input() key!: string;

  @Input() hideAddButton = false;

  destroy$ = new Subject<void>();

  ngOnInit(): void {
    this.viewType = this.adnocListService?.viewType;
    this.domainType = this.adnocListService.domainType;
    this.createButtonType = this.adnocListService.getCreateButtonType();
    this.structure$ = this.adnocListService.getStructure();
    this.key = this.adnocListService.key();
  }

  /**
   * Returns the total number of items.
   */
  getListCount(dataTable: Table | EntitiesModel<T>): number | undefined {
    return dataTable.pagination?.totalResults;
  }

  /**
   * Browses to the given page number
   */
  browse(pagination: P | undefined, pageNumber: number) {
    if (pagination) {
      this.adnocListService.view(pagination, pageNumber);
    }
  }

  /**
   * Navigates to the detailed view of the selected list item.
   */
  launchItem(event: T): void {
    this.organizationItemService.launchDetails(event);
  }

  /**
   * Sorts the list.
   */
  sort(pagination: P | undefined): void {
    if (pagination) {
      this.adnocListService.sort({
        ...pagination,
        ...({ sort: this.sortCode } as PaginationModel),
      });
    }
  }

  /**
   * Function to call when 'Manage Users' button is clicked
   */
  onCreateButtonClick(): void {
    this.adnocListService.onCreateButtonClick();
  }

  /**
   * Returns the label for Create button
   */
  getCreateButtonLabel(): Translatable {
    return this.adnocListService.getCreateButtonLabel();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
