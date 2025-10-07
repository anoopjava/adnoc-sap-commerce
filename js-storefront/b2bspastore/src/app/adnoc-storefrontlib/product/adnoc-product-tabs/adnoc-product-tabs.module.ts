/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { ProductAttributesModule } from './adnoc-product-attributes/adnoc-product-attributes.module';
import { ProductDetailsTabModule, ProductReviewsModule } from '@spartacus/storefront';


@NgModule({
  imports: [
    ProductAttributesModule,
    ProductDetailsTabModule,
    ProductReviewsModule,
  ],
})
export class AdnocProductTabsModule {}
