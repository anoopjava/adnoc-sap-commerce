/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
    ChangeDetectionStrategy,
    Component,
    ViewEncapsulation,
  } from '@angular/core';
  import { ProductListComponent } from '@spartacus/storefront';
  
  @Component({
    selector: 'cx-product-list',
    templateUrl: './adnoc-product-list.component.html',
    styleUrl: './adnoc-product-list.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
    encapsulation: ViewEncapsulation.None,
    host: { class: 'adnoc-product-list' },
    standalone: false
})
  export class AdnocProductListComponent extends ProductListComponent {}