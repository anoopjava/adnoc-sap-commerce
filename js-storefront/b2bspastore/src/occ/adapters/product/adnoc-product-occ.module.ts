/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import {
  provideDefaultConfig,
  ProductAdapter,
  OccProductAdapter,
  PRODUCT_NORMALIZER,
  ProductImageNormalizer,
  ProductNameNormalizer,
  ProductReferencesAdapter,
  OccProductReferencesAdapter,
  PRODUCT_REFERENCES_NORMALIZER,
  OccProductReferencesListNormalizer,
  ProductSearchAdapter,
  OccProductSearchAdapter,
  PRODUCT_SEARCH_PAGE_NORMALIZER,
  OccProductSearchPageNormalizer,
  ProductReviewsAdapter,
  OccProductReviewsAdapter,
  ProductAvailabilityAdapter,
  OccProductAvailabilityAdapter,
} from '@spartacus/core';
import './adnoc-product-occ-config';
import { adnocDefaultOccProductConfig } from './adnoc-default-occ-product-config';

@NgModule({
  imports: [CommonModule],
  providers: [
    provideDefaultConfig(adnocDefaultOccProductConfig),
    {
      provide: ProductAdapter,
      useClass: OccProductAdapter,
    },
    {
      provide: ProductAvailabilityAdapter,
      useClass: OccProductAvailabilityAdapter,
    },
    {
      provide: PRODUCT_NORMALIZER,
      useExisting: ProductImageNormalizer,
      multi: true,
    },
    {
      provide: PRODUCT_NORMALIZER,
      useExisting: ProductNameNormalizer,
      multi: true,
    },
    {
      provide: ProductReferencesAdapter,
      useClass: OccProductReferencesAdapter,
    },
    {
      provide: PRODUCT_REFERENCES_NORMALIZER,
      useExisting: OccProductReferencesListNormalizer,
      multi: true,
    },
    {
      provide: ProductSearchAdapter,
      useClass: OccProductSearchAdapter,
    },
    {
      provide: PRODUCT_SEARCH_PAGE_NORMALIZER,
      useExisting: OccProductSearchPageNormalizer,
      multi: true,
    },
    {
      provide: ProductReviewsAdapter,
      useClass: OccProductReviewsAdapter,
    },
  ],
})
export class AdnocProductOccModule {}
