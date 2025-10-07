/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdnocTabParagraphContainerComponent } from './adnoc-tab-paragraph-container.component';
import {
  CmsConfig,
  FeaturesConfigModule,
  I18nModule,
  provideDefaultConfig,
} from '@spartacus/core';
import {
  OutletModule,
  PageComponentModule,
  TabModule,
} from '@spartacus/storefront';

@NgModule({
  declarations: [AdnocTabParagraphContainerComponent],
  imports: [
    CommonModule,
    I18nModule,
    PageComponentModule,
    OutletModule,
    TabModule,
    FeaturesConfigModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        CMSTabParagraphContainer: {
          component: AdnocTabParagraphContainerComponent,
        },
      },
    }),
  ],
  exports: [AdnocTabParagraphContainerComponent],
})
export class AdnocTabParagraphContainerModule {}
