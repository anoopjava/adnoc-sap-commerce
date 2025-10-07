/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
    ChangeDetectionStrategy,
    Component,
  } from '@angular/core';
  import { ProductViewComponent } from '@spartacus/storefront';
  
  @Component({
    selector: 'cx-product-view',
    templateUrl: './adnoc-product-view.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'adnoc-product-view' },
    standalone: false
})
  export class AdnocProductViewComponent extends ProductViewComponent {}