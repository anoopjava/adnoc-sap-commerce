/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { inject, Injectable } from '@angular/core';
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
import { AdnocUpdatedUnitTreeService } from './adnoc-unit-item-update.service';

@Injectable({
  providedIn: 'root',
})
export class AdnocUnitListService extends AdnocListService<B2BUnitTreeNode> {
  protected tableType = OrganizationTableType.UNIT;
  protected adnocUpdateListService = inject(AdnocUpdatedUnitTreeService);

  constructor(
    protected override tableService: TableService,
    protected unitService: OrgUnitService,
    protected unitItemService: AdnocUnitItemService,
    protected unitTreeService: AdnocUnitTreeService
  ) {
    super(tableService);
  }

  protected load(): Observable<EntitiesModel<B2BUnitTreeNode> | undefined> {
    return this.adnocUpdateListService.getUpdatedOrgUnitsTreeEndpoint().pipe(
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
      map((tree: B2BUnitNode | undefined) =>
        this.convertListItem(tree?.unitNodes as B2BUnitTreeNode[] | undefined)
      )
    );
  }

  protected convertListItem(
    unitNodes: B2BUnitTreeNode[] | undefined,
    depthLevel = 0,
    pagination = { totalResults: 0 }
  ): EntitiesModel<B2BUnitTreeNode> {
    let values: B2BUnitTreeNode[] = [];
    if (unitNodes) {
      for (const unit of unitNodes) {
        const node: B2BUnitTreeNode = {
          ...unit,
          uid: unit.uid || unit.id || '',
          count: unit.children?.length ?? 0,
          expanded: this.unitTreeService.isExpanded(unit.id ?? '', depthLevel),
          depthLevel,
          children: [...(unit.children ?? [])].sort((a, b) =>
            (a.name ?? '').localeCompare(b.name ?? '')
          ),
        };

        values.push(node);
        pagination.totalResults++;

        if (node.expanded && (node.children?.length ?? 0) > 0) {
          const childTreeNodes: B2BUnitTreeNode[] = (node.children ?? []).map(
            (child) => ({
              ...child,
              uid: child.id || '',
              count: child.children?.length ?? 0,
              expanded: this.unitTreeService.isExpanded(
                child.id ?? '',
                depthLevel + 1
              ),
              depthLevel: depthLevel + 1,
              children: [...(child.children ?? [])].sort((a, b) =>
                (a.name ?? '').localeCompare(b.name ?? '')
              ),
            })
          );
          const childResult = this.convertListItem(
            childTreeNodes,
            depthLevel + 1,
            pagination
          );
          values = values.concat(childResult.values);
        }
      }
    }

    return { values, pagination };
  }

  override key(): string {
    return 'uid';
  }
}
