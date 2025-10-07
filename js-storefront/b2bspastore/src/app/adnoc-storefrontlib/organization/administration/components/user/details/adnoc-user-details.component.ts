/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { B2BUserRole, B2BUserRight } from '@spartacus/core';
import { Observable, Subscription } from 'rxjs';
import { startWith, switchMap } from 'rxjs/operators';
import { ItemService } from '../../shared/item.service';
import { B2BUserService } from '@spartacus/organization/administration/core';
import { AdnocUserItemService } from '../services/adnoc-user-item.service';
import { B2BUser } from '../../../../../../core/src/model/org-unit.model';

@Component({
  selector: 'cx-org-user-details',
  templateUrl: './adnoc-user-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    {
      provide: ItemService,
      useExisting: AdnocUserItemService,
    },
  ],
  host: { class: 'content-wrapper' },
  standalone: false,
})
export class AdnocUserDetailsComponent {
  userGuardSubscription!: Subscription;
  model$!: Observable<B2BUser>;
  isInEditMode$;

  isUpdatingUserAllowed;

  availableRoles: string[];
  availableRights: string[];

  constructor(
    protected itemService: ItemService<B2BUser>,
    protected b2bUserService: B2BUserService
  ) {
    this.model$ = this.itemService.key$.pipe(
      switchMap((code) => this.itemService.load(code)),
      startWith({})
    );
    this.isInEditMode$ = this.itemService.isInEditMode$;
    this.isUpdatingUserAllowed = this.b2bUserService.isUpdatingUserAllowed();
    this.availableRoles = this.b2bUserService
      .getAllRoles()
      .map((role: B2BUserRole) => role.toString());
    this.availableRights = this.b2bUserService
      .getAllRights()
      .map((right: B2BUserRight) => right.toString());
  }

  hasRight(model: B2BUser): boolean {
    return (model.roles ?? []).some((role: string) =>
      this.availableRights.includes(role)
    );
  }
}
