/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AtMessageModule,
  defaultViewConfig,
  IconModule,
  ListNavigationModule,
  MediaModule,
  OutletModule,
  PageComponentModule,
  SpinnerModule,
  StarRatingModule,
  ViewConfig,
} from '@spartacus/storefront';
import {
  CmsConfig,
  FeaturesConfigModule,
  I18nModule,
  provideDefaultConfig,
  UrlModule,
} from '@spartacus/core';
import { AdnocProductListService } from './adnoc-product-list.service';
import { AdnocProductListComponent } from './container/adnoc-product-list.component';
import { RouterModule } from '@angular/router';
import { AdnocProductListItemComponent } from './adnoc-product-list-item/adnoc-product-list-item.component';
import { AdnocProductGridItemComponent } from './adnoc-product-grid-item/adnoc-product-grid-item.component';
import { AdnocProductViewComponent } from './adnoc-product-view/adnoc-product-view.component';
import { AdnocProductScrollComponent } from './container/adnoc-product-scroll/adnoc-product-scroll.component';
import { InfiniteScrollModule } from 'ngx-infinite-scroll';
import { ItemCounterModule } from '../../shared/components/item-counter/item-counter.module';

@NgModule({
  imports: [
    AtMessageModule,
    CommonModule,
    I18nModule,
    IconModule,
    InfiniteScrollModule,
    ItemCounterModule,
    ListNavigationModule,
    MediaModule,
    OutletModule,
    PageComponentModule,
    RouterModule,
    SpinnerModule,
    StarRatingModule,
    UrlModule,
    FeaturesConfigModule,
  ],
  providers: [
    AdnocProductListService,
    provideDefaultConfig(<ViewConfig>defaultViewConfig),
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        CMSProductListComponent: {
          component: AdnocProductListComponent,
          data: {
            composition: {
              inner: ['ProductAddToCartComponent'],
            },
          },
        },
        ProductGridComponent: {
          component: AdnocProductListComponent,
          data: {
            composition: {
              inner: ['ProductAddToCartComponent'],
            },
          },
        },
        SearchResultsListComponent: {
          component: AdnocProductListComponent,
          data: {
            composition: {
              inner: ['ProductAddToCartComponent'],
            },
          },
        },
      },
    }),
  ],
  declarations: [
    AdnocProductListComponent,
    AdnocProductListItemComponent,
    AdnocProductGridItemComponent,
    AdnocProductViewComponent,
    AdnocProductScrollComponent,
  ],
  exports: [
    AdnocProductListComponent,
    AdnocProductListItemComponent,
    AdnocProductGridItemComponent,
    AdnocProductViewComponent,
    AdnocProductScrollComponent,
  ],
})
export class AdnocProductListModule {}
