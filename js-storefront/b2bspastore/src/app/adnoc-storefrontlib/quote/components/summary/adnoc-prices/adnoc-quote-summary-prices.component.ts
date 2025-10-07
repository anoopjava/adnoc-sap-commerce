/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Component, inject } from '@angular/core';
import { Price } from '@spartacus/core';
import { AdnocQuoteFacade } from '../../../root/facade/AdnocQuote.facade';

@Component({
    selector: 'adnoc-quote-summary-prices',
    templateUrl: 'adnoc-quote-summary-prices.component.html',
    standalone: false
})
export class AdnocQuoteSummaryPricesComponent {
  protected quoteFacade = inject(AdnocQuoteFacade);

  quoteDetails$ = this.quoteFacade.getQuoteDetails();

  /**
   * Checks whether the price has a non-zero value.
   *
   * @param price - Price to check
   * @returns true, only if the price has a non zero value
   */
  hasNonZeroPriceValue(price?: Price): boolean {
    return !!price && !!price.value && price.value > 0;
  }
}
