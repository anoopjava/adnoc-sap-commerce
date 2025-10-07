/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Component, inject, ViewEncapsulation } from '@angular/core';
import { AbstractOrderType, CartOutlets } from '@spartacus/cart/base/root';
import { ICON_TYPE } from '@spartacus/storefront';
import { Observable } from 'rxjs';
import {
  AdnocQuoteItemsComponentService,
  QuoteItemsData,
} from './adnoc-quote-items.component.service';
import { AdnocCartOutlets } from '../../../cart/base/root/models/cart-outlets.model';
import { GlobalMessageType } from '@spartacus/core';
import { AdnocGlobalMessageService } from '../../../../core/global-message/facade/adnoc-global-message.service';

/**
 * Renders quote items. These items are either taken from the actual quote,
 * or from the attached quote cart.
 * Specifically if the quote is editable, changes to the entries will be
 * done by changing the attached quote cart's entries.
 *
 * Note that the component makes use of outlet CART_ITEM_LIST in order to
 * render the quote entries. The default implementation of this outlet is
 * in feature lib 'cartBase'. This lib is always loaded, because
 * quoteFacade.getQuoteDetails() always triggers activeCartFacade for checking
 * on the quote/cart link.
 */

@Component({
  selector: 'adnoc-quote-items',
  templateUrl: './adnoc-quote-items.component.html',
  styleUrls: ['./adnoc-quote-items.component.scss'],
  encapsulation: ViewEncapsulation.None,
  standalone: false,
})
export class AdnocQuoteItemsComponent {
  quoteItemsData$: Observable<QuoteItemsData> | undefined;

  showCart$: Observable<boolean> | undefined;
  iconTypes = ICON_TYPE;
  readonly cartOutlets = AdnocCartOutlets;
  readonly abstractOrderType = AbstractOrderType;
  protected globalMessageService = inject(AdnocGlobalMessageService);

  constructor(
    protected quoteItemsComponentService: AdnocQuoteItemsComponentService
  ) {
    this.globalMessageService.remove(GlobalMessageType.MSG_TYPE_ERROR);
  }

  ngOnInit(): void {
    this.quoteItemsData$ =
      this.quoteItemsComponentService.retrieveQuoteEntries();
    this.showCart$ = this.quoteItemsComponentService.getQuoteEntriesExpanded();
  }
  /**
   * Handler to toggle expanded state of quote entries section.
   *
   * @param showCart - current expanded state, will be inverted
   */
  onToggleShowOrHideCart(showCart: boolean) {
    this.quoteItemsComponentService.setQuoteEntriesExpanded(!showCart);
  }
}
