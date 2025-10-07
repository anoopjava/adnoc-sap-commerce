/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { CmsConfig, I18nModule, provideDefaultConfig } from '@spartacus/core';
import { AdnocQuoteRequestButtonComponent } from './adnoc-quote-request-button.component';

@NgModule({
  imports: [CommonModule, I18nModule],
  declarations: [AdnocQuoteRequestButtonComponent],
  exports: [AdnocQuoteRequestButtonComponent],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        QuoteRequestComponent: {
          component: AdnocQuoteRequestButtonComponent,
        },
      },
    }),
  ],
})
export class AdnocQuoteRequestButtonModule {}
