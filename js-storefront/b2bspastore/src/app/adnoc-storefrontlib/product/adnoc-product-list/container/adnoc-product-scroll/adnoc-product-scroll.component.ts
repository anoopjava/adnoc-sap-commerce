/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
    ChangeDetectionStrategy,
    Component,
  } from '@angular/core';
  import { ProductScrollComponent } from '@spartacus/storefront';
  
  @Component({
    selector: 'cx-product-scroll',
    templateUrl: './adnoc-product-scroll.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'adnoc-product-scroll' },
    standalone: false
})
  export class AdnocProductScrollComponent extends ProductScrollComponent {}