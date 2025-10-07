/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { AuthGuard, CmsConfig, FeatureToggles } from '@spartacus/core';
import { AdnocUnitListComponent } from './list/adnoc-unit-list.component';
import { AdnocListService } from '../shared/list/adnoc-list.service';
import {
  AssignCellComponent,
  CostCenterDetailsCellComponent,
  LinkCellComponent,
  ToggleLinkCellComponent,
  UnitCellComponent,
  UnitDetailsCellComponent,
  UserDetailsCellComponent,
} from '@spartacus/organization/administration/components';
import { ROUTE_PARAMS } from '@spartacus/organization/administration/root';
import { BREAKPOINT, TableLayout } from '@spartacus/storefront';
import {
  OrgUnitGuard,
  AdminGuard,
  //UserGuard,
} from '@spartacus/organization/administration/core';
import { CellComponent } from '../shared/table/cell.component';
import { StatusCellComponent } from '../shared/table/status/status-cell.component';
import { MAX_OCC_INTEGER_VALUE } from '../constants';
import { AdnocUnitRoutePageMetaResolver } from './services/adnoc-unit-route-page-meta.resolver';
//import { AdnocUnitAddressRoutePageMetaResolver } from './services/adnoc-unit-address-route-page-meta.resolver';
import { inject } from '@angular/core';
import { TableConfig } from '../../../../shared/table/config/table.config';
import { ItemService } from '../shared/item.service';
import { AdnocUnitFormComponent } from './form/adnoc-unit-form.component';
import { AdnocUnitItemService } from './services/adnoc-unit-item.service';
import { AdnocUnitDetailsComponent } from './adnoc-details';
import { AdnocToggleLinkCellComponent } from './list/toggle-link/toggle-link-cell.component';
import { UnitUserRolesCellComponent } from './links/users/list/unit-user-link-cell.component';
import { AdnocUnitListService } from './services/adnoc-unit-list.service';
// import { UnitChildrenComponent } from './links/children/unit-children.component';
// import { UnitChildCreateComponent } from './links/children/create/unit-child-create.component';
// import { UnitAssignedApproverListComponent } from './links/approvers/assigned/unit-assigned-approver-list.component';
// import { UnitApproverListComponent } from './links/approvers/unit-approver-list.component';
// import { UnitUserListComponent } from './links/users/list/unit-user-list.component';
// import { UnitUserCreateComponent } from './links/users/create/unit-user-create.component';
// import { UnitUserRolesFormComponent } from './links/users/roles/unit-user-roles.component';
// import { UnitCostCenterListComponent } from './links/cost-centers/unit-cost-centers.component';
// import { UnitCostCenterCreateComponent } from './links/cost-centers/create/unit-cost-center-create.component';
// import { UnitAddressFormComponent } from './links/addresses/form/unit-address-form.component';
// import { UnitAddressDetailsComponent } from './links/addresses/details/unit-address-details.component';
// import { UnitAddressListComponent } from './links/addresses/list/unit-address-list.component';
import { OrganizationTableType } from '../shared/organization.model';

export const adnocUnitsCmsConfig: CmsConfig = {
  cmsComponents: {
    ManageUnitsListComponent: {
      component: AdnocUnitListComponent,
      providers: [
        {
          provide: AdnocListService,
          useExisting: AdnocUnitListService,
        },
        {
          provide: ItemService,
          useExisting: AdnocUnitItemService,
        },
      ],
      childRoutes: {
        parent: {
          data: {
            cxPageMeta: {
              breadcrumb: 'orgUnit.breadcrumbs.list',
              resolver: AdnocUnitRoutePageMetaResolver,
            },
          },
        },
        children: [
          {
            path: 'create',
            component: AdnocUnitFormComponent,
            canActivate: [OrgUnitGuard],
          },
          {
            path: `:${ROUTE_PARAMS.unitCode}`,
            component: AdnocUnitDetailsComponent,
            data: {
              cxPageMeta: { breadcrumb: 'orgUnit.breadcrumbs.details' },
            },
            children: [
              {
                path: 'edit',
                component: AdnocUnitFormComponent,
                canActivate: [OrgUnitGuard],
              },
              // As per requirements, we need to remove this code, Considering future changes and keeping it as commented code
              // {
              //   path: 'children',
              //   component: UnitChildrenComponent,
              //   canActivate: [OrgUnitGuard],
              //   data: {
              //     cxPageMeta: { breadcrumb: 'orgUnit.breadcrumbs.children' },
              //   },
              //   children: [
              //     {
              //       path: 'create',
              //       component: UnitChildCreateComponent,
              //     },
              //   ],
              // },
              // {
              //   path: 'approvers',
              //   data: {
              //     cxPageMeta: { breadcrumb: 'orgUnit.breadcrumbs.approvers' },
              //   },
              //   children: [
              //     {
              //       path: '',
              //       component: UnitAssignedApproverListComponent,
              //     },
              //     {
              //       path: 'assign',
              //       component: UnitApproverListComponent,
              //     },
              //   ],
              // },
              // {
              //   path: 'users',
              //   component: UnitUserListComponent,
              //   data: {
              //     cxPageMeta: { breadcrumb: 'orgUnit.breadcrumbs.users' },
              //   },
              //   children: [
              //     {
              //       path: 'create',
              //       component: UnitUserCreateComponent,
              //       canActivate: [UserGuard],
              //     },
              //     {
              //       path: `:${ROUTE_PARAMS.userCode}/roles`,
              //       component: UnitUserRolesFormComponent,
              //       canActivate: [UserGuard],
              //     },
              //   ],
              // },
              // {
              //   path: 'cost-centers',
              //   component: UnitCostCenterListComponent,
              //   data: {
              //     cxPageMeta: { breadcrumb: 'orgUnit.breadcrumbs.costCenters' },
              //   },
              //   children: [
              //     {
              //       path: 'create',
              //       component: UnitCostCenterCreateComponent,
              //     },
              //   ],
              // },
              // {
              //   path: 'addresses',
              //   component: UnitAddressListComponent,
              //   data: {
              //     cxPageMeta: {
              //       breadcrumb: 'orgUnit.breadcrumbs.addresses',
              //       resolver: AdnocUnitAddressRoutePageMetaResolver,
              //     },
              //   },
              //   children: [
              //     {
              //       path: 'create',
              //       component: UnitAddressFormComponent,
              //     },
              //     {
              //       path: `:${ROUTE_PARAMS.addressCode}`,
              //       data: {
              //         cxPageMeta: {
              //           breadcrumb: 'orgUnit.breadcrumbs.addressDetails',
              //         },
              //       },
              //       children: [
              //         {
              //           path: '',
              //           component: UnitAddressDetailsComponent,
              //         },
              //         {
              //           path: 'edit',
              //           component: UnitAddressFormComponent,
              //         },
              //       ],
              //     },
              //   ],
              // },
            ],
          },
        ],
      },
      guards: [AuthGuard, AdminGuard],
    },
  },
};

export function unitsTableConfigFactory(): TableConfig {
  const featureToggles = inject(FeatureToggles);
  if (featureToggles.a11yOrganizationLinkableCells) {
    return newUnitsTableConfig;
  }
  return unitsTableConfig;
}

export const newUnitsTableConfig: TableConfig = {
  table: {
    [OrganizationTableType.UNIT]: {
      cells: ['name'],
      options: {
        layout: TableLayout.VERTICAL,
        cells: {
          name: {
            dataComponent: AdnocToggleLinkCellComponent,
            linkable: true,
          },
          lob: {
            dataComponent: CellComponent,
          },
          active: {
            dataComponent: StatusCellComponent,
          },
          uid: {
            dataComponent: CellComponent,
          },
        },
      },
      [BREAKPOINT.lg]: {
        cells: ['name', 'lob', 'parent', 'active', 'detail'],
      },
    },
    [OrganizationTableType.UNIT_USERS]: {
      cells: ['name', 'roles'],
      options: {
        pagination: {
          pageSize: MAX_OCC_INTEGER_VALUE,
        },
        cells: {
          name: {
            dataComponent: UserDetailsCellComponent,
          },
          roles: {
            dataComponent: UnitUserRolesCellComponent,
          },
        },
      },
    },

    [OrganizationTableType.UNIT_CHILDREN]: {
      cells: ['name', 'active'],
      options: {
        pagination: {
          pageSize: MAX_OCC_INTEGER_VALUE,
        },
        cells: {
          name: {
            dataComponent: UnitDetailsCellComponent,
          },
          active: {
            dataComponent: StatusCellComponent,
          },
        },
      },
    },

    [OrganizationTableType.UNIT_APPROVERS]: {
      cells: ['name', 'orgUnit', 'actions'],
      options: {
        cells: {
          name: {
            dataComponent: UserDetailsCellComponent,
          },
          actions: {
            dataComponent: AssignCellComponent,
          },
          orgUnit: {
            dataComponent: UnitCellComponent,
          },
        },
      },
    },

    [OrganizationTableType.UNIT_ASSIGNED_APPROVERS]: {
      cells: ['name', 'orgUnit', 'actions'],
      options: {
        pagination: {
          pageSize: MAX_OCC_INTEGER_VALUE,
        },
        cells: {
          name: {
            dataComponent: UserDetailsCellComponent,
          },
          actions: {
            dataComponent: AssignCellComponent,
          },
          orgUnit: {
            dataComponent: UnitCellComponent,
          },
        },
      },
    },

    [OrganizationTableType.UNIT_COST_CENTERS]: {
      cells: ['name'],
      options: {
        cells: {
          name: {
            dataComponent: CostCenterDetailsCellComponent,
          },
        },
        pagination: {
          pageSize: MAX_OCC_INTEGER_VALUE,
        },
      },
    },

    [OrganizationTableType.UNIT_ADDRESS]: {
      cells: ['formattedAddress'],
      options: {
        pagination: {
          pageSize: MAX_OCC_INTEGER_VALUE,
        },
        cells: {
          formattedAddress: {
            dataComponent: LinkCellComponent,
            linkable: true,
          },
        },
      },
    },
  },
};

export const unitsTableConfig: TableConfig = {
  table: {
    [OrganizationTableType.UNIT]: {
      cells: ['name'],
      options: {
        layout: TableLayout.VERTICAL,
        cells: {
          name: {
            dataComponent: AdnocToggleLinkCellComponent,
          },
          lob: {
            dataComponent: CellComponent,
          },
          active: {
            dataComponent: StatusCellComponent,
          },
          uid: {
            dataComponent: CellComponent,
          },
        },
      },
      [BREAKPOINT.lg]: {
        cells: ['name', 'lob', 'parent', 'active', 'detail'],
      },
    },
    [OrganizationTableType.UNIT_USERS]: {
      cells: ['name', 'roles'],
      options: {
        pagination: {
          pageSize: MAX_OCC_INTEGER_VALUE,
        },
        cells: {
          name: {
            dataComponent: UserDetailsCellComponent,
          },
          roles: {
            dataComponent: UnitUserRolesCellComponent,
          },
        },
      },
    },

    [OrganizationTableType.UNIT_CHILDREN]: {
      cells: ['name', 'active'],
      options: {
        pagination: {
          pageSize: MAX_OCC_INTEGER_VALUE,
        },
        cells: {
          name: {
            dataComponent: UnitDetailsCellComponent,
          },
          active: {
            dataComponent: StatusCellComponent,
            linkable: false,
          },
        },
      },
    },

    [OrganizationTableType.UNIT_APPROVERS]: {
      cells: ['name', 'orgUnit', 'actions'],
      options: {
        cells: {
          name: {
            dataComponent: UserDetailsCellComponent,
          },
          actions: {
            dataComponent: AssignCellComponent,
          },
          orgUnit: {
            dataComponent: UnitCellComponent,
            linkable: false,
          },
        },
      },
    },

    [OrganizationTableType.UNIT_ASSIGNED_APPROVERS]: {
      cells: ['name', 'orgUnit', 'actions'],
      options: {
        pagination: {
          pageSize: MAX_OCC_INTEGER_VALUE,
        },
        cells: {
          name: {
            dataComponent: UserDetailsCellComponent,
          },
          actions: {
            dataComponent: AssignCellComponent,
          },
          orgUnit: {
            dataComponent: UnitCellComponent,
            linkable: false,
          },
        },
      },
    },

    [OrganizationTableType.UNIT_COST_CENTERS]: {
      cells: ['name'],
      options: {
        cells: {
          name: {
            dataComponent: CostCenterDetailsCellComponent,
          },
        },
        pagination: {
          pageSize: MAX_OCC_INTEGER_VALUE,
        },
      },
    },

    [OrganizationTableType.UNIT_ADDRESS]: {
      cells: ['formattedAddress'],
      options: {
        pagination: {
          pageSize: MAX_OCC_INTEGER_VALUE,
        },
        cells: {
          formattedAddress: {
            dataComponent: LinkCellComponent,
          },
        },
      },
    },
  },
};
