/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  Optional,
  ViewEncapsulation,
  inject,
} from '@angular/core';
import { FeatureConfigService, I18nModule, UrlModule } from '@spartacus/core';
import {
  TableDataOutletContext,
  TableFieldOptions,
} from '../../../../../shared/table/table.model';
import { IconModule, OutletContextData } from '@spartacus/storefront';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'cx-org-cell',
  templateUrl: './cell.component.html',
  styleUrls: ['./cell.component.scss'],
  imports: [CommonModule, RouterModule, UrlModule, I18nModule, IconModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
})
export class CellComponent {
  @Optional() featuteConfigService = inject(FeatureConfigService, {
    optional: true,
  });

  constructor(protected outlet: OutletContextData<TableDataOutletContext>) {}

  getDisplayName(firstName: string, lastName: string): string {
    let displayName = '';

    if (firstName && firstName.trim()) {
      displayName += firstName.charAt(0).toUpperCase();
    }

    if (lastName && lastName.trim()) {
      displayName += lastName.charAt(0).toUpperCase();
    }

    return displayName.trim(); // To remove any leading or trailing spaces
  }

  get tabIndex(): number {
    return -1;
  }

  get model(): TableDataOutletContext {
    return this.outlet.context;
  }

  get property(): string | undefined {
    return this.model?.[this.outlet?.context?._field];
  }

  /**
   * Indicates wether the cell is linkable.
   *
   * If the cells is linkable, an anchor link is created to the detailed route
   * of the given `_type`.
   *
   * Defaults to `true. If 'a11yOrganizationLinkableCells' feature flag is enabled, it will default to `false`.
   */
  get linkable(): boolean {
    // TODO: (CXSPA-7155) - Remove feature flag and update JSDoc next major release
    if (this.featuteConfigService?.isEnabled('a11yOrganizationLinkableCells')) {
      return (
        this.property !== undefined && (this.cellOptions.linkable ?? false)
      );
    }
    return this.property !== undefined && (this.cellOptions.linkable ?? true);
  }

  /**
   * Helper method to access the cell options.
   */
  get cellOptions(): TableFieldOptions {
    return (
      this.outlet.context?._options?.cells?.[this.outlet.context?._field] ?? {}
    );
  }

  /**
   * Generates the configurable route to the detail page of the given context item.
   */
  get route(): string {
    return this.outlet.context._type + 'Details';
  }

  get routeModel(): any {
    return this.outlet.context;
  }

  get type(): string {
    return this.model._type;
  }

  /**
   * Indicates whether the item is loaded.
   */
  get hasItem(): boolean {
    return !!this.item && Object.keys(this.item).length > 0;
  }

  get item(): any {
    if (!this.outlet.context) {
      return null;
    }
    const { _field, _options, _type, _i18nRoot, ...all } = this.outlet.context;
    return all;
  }
}
