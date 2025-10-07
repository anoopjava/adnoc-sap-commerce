/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdnocProductIntroComponent } from './adnoc-product-intro.component';
import { CmsConfig, I18nModule, provideDefaultConfig } from '@spartacus/core';
import { StarRatingModule } from '@spartacus/storefront';

@NgModule({
  declarations: [AdnocProductIntroComponent],
  imports: [CommonModule, I18nModule, StarRatingModule, ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        ProductIntroComponent: {
          component: AdnocProductIntroComponent,
        },
      },
    }),
  ],
  exports: [AdnocProductIntroComponent],
})
export class AdnocProductIntroModule {}
