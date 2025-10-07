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
import { ProductIntroComponent } from '@spartacus/storefront';

@Component({
    selector: 'cx-product-intro',
    templateUrl: './adnoc-product-intro.component.html',
    styleUrl: './adnoc-product-intro.component.scss',
    changeDetection: ChangeDetectionStrategy.OnPush,
    encapsulation: ViewEncapsulation.None,
    host: { class: 'adnoc-product-intro' },
    standalone: false
})
export class AdnocProductIntroComponent extends ProductIntroComponent {}
