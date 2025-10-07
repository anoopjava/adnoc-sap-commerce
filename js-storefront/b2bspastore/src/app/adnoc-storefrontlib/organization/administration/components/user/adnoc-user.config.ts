/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { inject } from '@angular/core';
import { AuthGuard, CmsConfig, FeatureToggles } from '@spartacus/core';
import {
  AdminGuard,
  UserGuard,
} from '@spartacus/organization/administration/core';
import { ROUTE_PARAMS } from '@spartacus/organization/administration/root';
import { TableConfig } from '../../../../shared/table/config/table.config';
import { MAX_OCC_INTEGER_VALUE } from '../constants';
import { ItemService } from '../shared/item.service';
import { AdnocListComponent } from '../shared/list/adnoc-list.component';
import { AdnocListService } from '../shared/list/adnoc-list.service';
import {
  UserListService,
  UserItemService,
  UserAssignedUserGroupListComponent,
  UserUserGroupListComponent,
  UserAssignedApproverListComponent,
  UserApproverListComponent,
  UserAssignedPermissionListComponent,
  UserPermissionListComponent,
  AssignCellComponent,
  OrganizationTableType,
  UnitCellComponent,
  UserDetailsCellComponent,
  UserGroupDetailsCellComponent,
  PermissionDetailsCellComponent,
} from '@spartacus/organization/administration/components';
import { AdnocUserRoutePageMetaResolver } from './services/adnoc-user-route-page-meta.resolver';
import { CellComponent } from '../shared/table/cell.component';
import { ActiveLinkCellComponent } from '../shared/table/active-link/active-link-cell.component';
import { StatusCellComponent } from '../shared/table/status/status-cell.component';
import { AdnocUserFormComponent } from './form';
import { AdnocUserChangePasswordFormComponent } from './adnoc-change-password-form/adnoc-user-change-password-form.component';
import { AdnocUserDetailsComponent } from './details/adnoc-user-details.component';
import { RolesCellComponent } from '../shared/table/roles/roles-cell.component';
import { ParnterIdCellComponent } from '../shared/table/unit/partner-id.component';

export const adnocUserCmsConfig: CmsConfig = {
  cmsComponents: {
    ManageUsersListComponent: {
      component: AdnocListComponent,
      providers: [
        {
          provide: AdnocListService,
          useExisting: UserListService,
        },
        {
          provide: ItemService,
          useExisting: UserItemService,
        },
      ],
      childRoutes: {
        parent: {
          data: {
            cxPageMeta: {
              breadcrumb: 'orgUser.breadcrumbs.list',
              resolver: AdnocUserRoutePageMetaResolver,
            },
          },
        },
        children: [
          {
            path: 'create',
            component: AdnocUserFormComponent,
            canActivate: [UserGuard],
          },
          {
            path: `:${ROUTE_PARAMS.userCode}`,
            component: AdnocUserDetailsComponent,
            data: {
              cxPageMeta: { breadcrumb: 'orgUser.breadcrumbs.details' },
            },
            children: [
              {
                path: `edit`,
                component: AdnocUserFormComponent,
                canActivate: [UserGuard],
              },
              {
                path: `change-password`,
                component: AdnocUserChangePasswordFormComponent,
                canActivate: [UserGuard],
              },
              {
                path: 'user-groups',
                data: {
                  cxPageMeta: { breadcrumb: 'orgUser.breadcrumbs.userGroups' },
                },
                children: [
                  {
                    path: '',
                    component: UserAssignedUserGroupListComponent,
                  },
                  {
                    path: 'assign',
                    component: UserUserGroupListComponent,
                  },
                ],
              },
              {
                path: 'approvers',
                data: {
                  cxPageMeta: { breadcrumb: 'orgUser.breadcrumbs.approvers' },
                },
                children: [
                  {
                    path: '',
                    component: UserAssignedApproverListComponent,
                  },
                  {
                    path: 'assign',
                    component: UserApproverListComponent,
                  },
                ],
              },
              {
                path: 'purchase-limits',
                data: {
                  cxPageMeta: { breadcrumb: 'orgUser.breadcrumbs.permissions' },
                },
                children: [
                  {
                    path: '',
                    component: UserAssignedPermissionListComponent,
                  },
                  {
                    path: 'assign',
                    component: UserPermissionListComponent,
                  },
                ],
              },
            ],
          },
        ],
      },
      guards: [AuthGuard, AdminGuard],
    },
  },
};

export function userTableConfigFactory(): TableConfig {
  // TODO: (CXSPA-7155) - Remove feature flag and legacy config next major release
  const featureToggles = inject(FeatureToggles);
  if (featureToggles.a11yOrganizationLinkableCells) {
    return newUserTableConfig;
  }
  return userTableConfig;
}

const actions = {
  dataComponent: AssignCellComponent,
};

const pagination = {
  pageSize: MAX_OCC_INTEGER_VALUE,
};

export const newUserTableConfig: TableConfig = {
  table: {
    [OrganizationTableType.USER]: {
      cells: ['name', 'active', 'uid', 'roles', 'partnerId'],
      options: {
        cells: {
          name: {
            dataComponent: ActiveLinkCellComponent,
            linkable: true,
          },
          active: {
            dataComponent: StatusCellComponent,
          },
          uid: {
            dataComponent: CellComponent,
          },
          roles: {
            dataComponent: RolesCellComponent,
          },
          // unit: {
          //   dataComponent: UnitCellComponent,
          // },
          partnerId: {
            dataComponent: ParnterIdCellComponent,
          },
        },
      },
    },
    [OrganizationTableType.USER_APPROVERS]: {
      cells: ['name', 'actions'],
      options: {
        cells: {
          name: {
            dataComponent: UserDetailsCellComponent,
          },
          actions,
        },
      },
    },
    [OrganizationTableType.USER_ASSIGNED_APPROVERS]: {
      cells: ['name', 'actions'],
      options: {
        cells: {
          name: {
            dataComponent: UserDetailsCellComponent,
          },
          actions,
        },
        pagination,
      },
    },
    [OrganizationTableType.USER_USER_GROUPS]: {
      cells: ['name', 'actions'],
      options: {
        cells: {
          name: {
            dataComponent: UserGroupDetailsCellComponent,
          },
          actions,
        },
      },
    },
    [OrganizationTableType.USER_ASSIGNED_USER_GROUPS]: {
      cells: ['name', 'actions'],
      options: {
        cells: {
          name: {
            dataComponent: UserGroupDetailsCellComponent,
          },
          actions,
        },
        pagination,
      },
    },
    [OrganizationTableType.USER_PERMISSIONS]: {
      cells: ['code', 'actions'],
      options: {
        cells: {
          code: {
            dataComponent: PermissionDetailsCellComponent,
          },
          actions,
        },
      },
    },
    [OrganizationTableType.USER_ASSIGNED_PERMISSIONS]: {
      cells: ['code', 'actions'],
      options: {
        cells: {
          code: {
            dataComponent: PermissionDetailsCellComponent,
          },
          actions,
        },
        pagination,
      },
    },
  },
};

export const userTableConfig: TableConfig = {
  table: {
    [OrganizationTableType.USER]: {
      cells: ['name', 'active', 'uid', 'roles', 'unit'],
      options: {
        cells: {
          name: {
            dataComponent: ActiveLinkCellComponent,
          },
          active: {
            dataComponent: StatusCellComponent,
          },
          uid: {
            dataComponent: CellComponent,
          },
          roles: {
            dataComponent: RolesCellComponent,
          },
          unit: {
            dataComponent: UnitCellComponent,
          },
        },
      },
    },
    [OrganizationTableType.USER_APPROVERS]: {
      cells: ['name', 'actions'],
      options: {
        cells: {
          name: {
            dataComponent: UserDetailsCellComponent,
          },
          actions,
        },
      },
    },
    [OrganizationTableType.USER_ASSIGNED_APPROVERS]: {
      cells: ['name', 'actions'],
      options: {
        cells: {
          name: {
            dataComponent: UserDetailsCellComponent,
          },
          actions,
        },
        pagination,
      },
    },
    [OrganizationTableType.USER_USER_GROUPS]: {
      cells: ['name', 'actions'],
      options: {
        cells: {
          name: {
            dataComponent: UserGroupDetailsCellComponent,
          },
          actions,
        },
      },
    },
    [OrganizationTableType.USER_ASSIGNED_USER_GROUPS]: {
      cells: ['name', 'actions'],
      options: {
        cells: {
          name: {
            dataComponent: UserGroupDetailsCellComponent,
          },
          actions,
        },
        pagination,
      },
    },
    [OrganizationTableType.USER_PERMISSIONS]: {
      cells: ['code', 'actions'],
      options: {
        cells: {
          code: {
            dataComponent: PermissionDetailsCellComponent,
          },
          actions,
        },
      },
    },
    [OrganizationTableType.USER_ASSIGNED_PERMISSIONS]: {
      cells: ['code', 'actions'],
      options: {
        cells: {
          code: {
            dataComponent: PermissionDetailsCellComponent,
          },
          actions,
        },
        pagination,
      },
    },
  },
};
