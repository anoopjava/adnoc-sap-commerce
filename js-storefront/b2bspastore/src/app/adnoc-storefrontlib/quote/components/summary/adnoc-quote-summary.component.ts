/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Component, inject } from '@angular/core';
import { AdnocQuoteFacade } from '../../root/facade/AdnocQuote.facade';

@Component({
    selector: 'adnoc-quote-summary',
    templateUrl: 'adnoc-quote-summary.component.html',
    standalone: false
})
export class AdnocQuoteSummaryComponent {
  protected quoteFacade = inject(AdnocQuoteFacade);

  quoteDetails$ = this.quoteFacade.getQuoteDetails();
}
