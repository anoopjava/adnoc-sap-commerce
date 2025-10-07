/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { QUOTE_NORMALIZER, QuoteCoreModule } from '@spartacus/quote/core';
import { AdnocQuoteComponentsModule } from './components/adnoc-quote-components.module';
import { QuoteOccModule } from '@spartacus/quote/occ';
import { AdnocQuoteFacade } from './root/facade/AdnocQuote.facade';
import { AdnocQuoteService } from './core/facade/quote.service';
import { AdnocQuoteConnector } from './core/connectors/adnocQuote.connector';
import { AdnocQuoteAdapter } from './core/connectors/adnocQuote.adapter';
import { OccQuoteAdapter } from './occ/adapters/occ-quote.adapter';
import { provideDefaultConfig } from '@spartacus/core';
import { defaultOccQuoteConfig } from './occ/config/default-occ-quote-config';
import { OccQuoteActionNormalizer } from './occ/converters/occ-quote-action-normalizer';

@NgModule({
  providers: [
    provideDefaultConfig(defaultOccQuoteConfig),
    AdnocQuoteService,
    {
      provide: AdnocQuoteFacade,
      useExisting: AdnocQuoteService,
    },
    {
      provide: AdnocQuoteAdapter,
      useClass: OccQuoteAdapter,
    },
       {
          provide: QUOTE_NORMALIZER,
          useExisting: OccQuoteActionNormalizer,
          multi: true,
        },
    AdnocQuoteConnector,
  ],
  imports: [AdnocQuoteComponentsModule, QuoteCoreModule, QuoteOccModule],
})
export class AdnocQuoteModule {}
