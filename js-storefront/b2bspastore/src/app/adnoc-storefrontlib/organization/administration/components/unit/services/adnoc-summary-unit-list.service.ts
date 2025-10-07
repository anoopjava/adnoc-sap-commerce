/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { AdnocListService } from '../../shared/list/adnoc-list.service';
import { OrganizationTableType } from '../../shared/organization.model';
import {
  B2BUnitTreeNode,
  OrgUnitService,
} from '@spartacus/organization/administration/core';
import { TableService } from '../../../../../shared/table/table.service';
import { map, Observable, switchMap } from 'rxjs';
import { EntitiesModel } from '@spartacus/core';
import { AdnocUnitItemService } from './adnoc-unit-item.service';
import { AdnocUnitTreeService } from './adnoc-unit-tree.service';
import { B2BUnitNode } from '../../../core/model/unit-node.model';

/**
 * Service to populate Unit data to `Table` data. Unit
 * data is driven by the table configuration, using the `OrganizationTables.UNIT`.
 */
@Injectable({
  providedIn: 'root',
})
export class AdnocSummaryUnitListService extends AdnocListService<B2BUnitTreeNode> {
  protected tableType = OrganizationTableType.UNIT;

  constructor(
    protected override tableService: TableService,
    protected unitService: OrgUnitService,
    protected unitItemService: AdnocUnitItemService,
    protected unitTreeService: AdnocUnitTreeService
  ) {
    super(tableService);
  }

  protected load(): Observable<EntitiesModel<B2BUnitTreeNode> | undefined> {
    return this.unitService.getTree().pipe(
      switchMap((node) =>
        this.unitItemService.key$.pipe(
          map((key) => {
            if (node) {
              this.unitTreeService.initialize(node, key);
            }
            return node;
          })
        )
      ),
      switchMap((tree) =>
        this.unitTreeService.treeToggle$.pipe(map(() => tree))
      ),
      map((tree: B2BUnitNode | undefined) => this.convertListItem(tree))
    );
  }

  protected convertListItem(
    unit: B2BUnitNode | undefined,
    depthLevel = 0,
    pagination = { totalResults: 0 }
  ): EntitiesModel<B2BUnitTreeNode> | undefined {
    let values: B2BUnitTreeNode[] = [];
    if (!unit) {
      return undefined;
    }

    const node: B2BUnitTreeNode = {
      ...unit,
      count: unit.children?.length ?? 0,
      expanded: this.unitTreeService.isExpanded(unit.id ?? '', depthLevel),
      depthLevel,
      uid: unit.id ?? '',
      children: [...(unit.children ?? [])].sort((unitA, unitB) =>
        (unitA.name ?? '').localeCompare(unitB.name ?? '')
      ),
    };

    values.push(node);
    pagination.totalResults++;

    node.children?.forEach((childUnit) => {
      const childList = this.convertListItem(
        childUnit,
        depthLevel + 1,
        pagination
      )?.values;
      if (node.expanded && childList && childList.length > 0) {
        values = values.concat(childList);
      }
    });

    return { values, pagination };
  }

  override key(): string {
    return 'uid';
  }
}
