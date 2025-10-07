/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CostCenterOccModule, provideDefaultConfig } from '@spartacus/core';
import {
  B2BUNIT_APPROVAL_PROCESSES_NORMALIZER,
  B2BUNIT_NODE_LIST_NORMALIZER,
  B2BUNIT_NODE_NORMALIZER,
  B2BUNIT_NORMALIZER,
  B2BUserAdapter,
  B2B_USERS_NORMALIZER,
  B2B_USER_NORMALIZER,
  B2B_USER_SERIALIZER,
  BudgetAdapter,
  BUDGETS_NORMALIZER,
  BUDGET_NORMALIZER,
  BUDGET_SERIALIZER,
  CostCenterAdapter,
  OrgUnitAdapter,
  PermissionAdapter,
  PERMISSIONS_NORMALIZER,
  PERMISSION_NORMALIZER,
  PERMISSION_TYPES_NORMALIZER,
  PERMISSION_TYPE_NORMALIZER,
  UserGroupAdapter,
  USER_GROUPS_NORMALIZER,
  USER_GROUP_NORMALIZER,
} from '@spartacus/organization/administration/core';

import {
  OccBudgetAdapter,
  OccBudgetNormalizer,
  OccBudgetSerializer,
  OccBudgetListNormalizer,
  OccOrgUnitAdapter,
  OccOrgUnitNormalizer,
  OccOrgUnitNodeNormalizer,
  OccOrgUnitNodeListNormalizer,
  OccOrgUnitApprovalProcessNormalizer,
  OccUserGroupAdapter,
  OccUserGroupNormalizer,
  OccUserGroupListNormalizer,
  OccPermissionAdapter,
  OccPermissionNormalizer,
  OccPermissionListNormalizer,
  OccPermissionTypeNormalizer,
  OccPermissionTypeListNormalizer,
  OccCostCenterAdapter,
  OccB2BUserAdapter,
  OccB2BUserNormalizer,
  OccB2bUserSerializer,
  OccUserListNormalizer,
} from '@spartacus/organization/administration/occ';
import { defaultOccOrganizationConfig } from './config/default-occ-organization-config';

@NgModule({
  imports: [CommonModule, CostCenterOccModule],
  providers: [
    provideDefaultConfig(defaultOccOrganizationConfig),
    {
      provide: BudgetAdapter,
      useClass: OccBudgetAdapter,
    },
    {
      provide: BUDGET_NORMALIZER,
      useExisting: OccBudgetNormalizer,
      multi: true,
    },
    {
      provide: BUDGET_SERIALIZER,
      useExisting: OccBudgetSerializer,
      multi: true,
    },
    {
      provide: BUDGETS_NORMALIZER,
      useExisting: OccBudgetListNormalizer,
      multi: true,
    },
    {
      provide: OrgUnitAdapter,
      useClass: OccOrgUnitAdapter,
    },
    {
      provide: B2BUNIT_NORMALIZER,
      useExisting: OccOrgUnitNormalizer,
      multi: true,
    },
    {
      provide: B2BUNIT_NODE_NORMALIZER,
      useExisting: OccOrgUnitNodeNormalizer,
      multi: true,
    },
    {
      provide: B2BUNIT_NODE_LIST_NORMALIZER,
      useExisting: OccOrgUnitNodeListNormalizer,
      multi: true,
    },
    {
      provide: B2BUNIT_APPROVAL_PROCESSES_NORMALIZER,
      useExisting: OccOrgUnitApprovalProcessNormalizer,
      multi: true,
    },
    {
      provide: UserGroupAdapter,
      useClass: OccUserGroupAdapter,
    },
    {
      provide: USER_GROUP_NORMALIZER,
      useExisting: OccUserGroupNormalizer,
      multi: true,
    },
    {
      provide: USER_GROUPS_NORMALIZER,
      useExisting: OccUserGroupListNormalizer,
      multi: true,
    },
    {
      provide: PermissionAdapter,
      useClass: OccPermissionAdapter,
    },
    {
      provide: PERMISSION_NORMALIZER,
      useExisting: OccPermissionNormalizer,
      multi: true,
    },
    {
      provide: PERMISSIONS_NORMALIZER,
      useExisting: OccPermissionListNormalizer,
      multi: true,
    },
    {
      provide: PERMISSION_TYPE_NORMALIZER,
      useExisting: OccPermissionTypeNormalizer,
      multi: true,
    },
    {
      provide: PERMISSION_TYPES_NORMALIZER,
      useExisting: OccPermissionTypeListNormalizer,
      multi: true,
    },
    {
      provide: CostCenterAdapter,
      useClass: OccCostCenterAdapter,
    },
    {
      provide: B2BUserAdapter,
      useClass: OccB2BUserAdapter,
    },
    {
      provide: B2B_USER_NORMALIZER,
      useExisting: OccB2BUserNormalizer,
      multi: true,
    },
    {
      provide: B2B_USER_SERIALIZER,
      useExisting: OccB2bUserSerializer,
      multi: true,
    },
    {
      provide: B2B_USERS_NORMALIZER,
      useExisting: OccUserListNormalizer,
      multi: true,
    },
  ],
})
export class AdnocAdministrationOccModule {}
