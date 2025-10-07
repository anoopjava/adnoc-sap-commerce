/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  inject,
  OnDestroy,
  ViewEncapsulation,
} from '@angular/core';
import { GlobalMessageType, RoutingService } from '@spartacus/core';
import { Observable, Subject, Subscription } from 'rxjs';
import { takeUntil, tap } from 'rxjs/operators';
import { AdnocActiveCartService } from '../../../cart/base/core/facade/adnoc-active-cart.service';
import _ from 'lodash';
import { AdnocOrderEntry } from '../../../../core/model/adnoc-cart.model';
import { AdnocQuoteFacade } from '../../root/facade/AdnocQuote.facade';
import { AdnocAuthService } from '../../../../core/src/auth/user-auth/facade/adnoc-auth.service';
import { AdnocGlobalMessageService } from '../../../../core/global-message/facade/adnoc-global-message.service';

@Component({
  selector: 'cx-quote-request-button',
  templateUrl: './adnoc-quote-request-button.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ['./adnoc-quote-request-button.component.scss'],
  encapsulation: ViewEncapsulation.None,
  standalone: false,
})
export class AdnocQuoteRequestButtonComponent implements OnDestroy {
  protected quoteFacade = inject(AdnocQuoteFacade);
  protected routingService = inject(RoutingService);
  protected authService = inject(AdnocAuthService);
  protected anocActiveCartService = inject(AdnocActiveCartService);
  protected globalMessageService = inject(AdnocGlobalMessageService);
  protected destroy$ = new Subject<void>();
  protected subscription = new Subscription();
  protected cd = inject(ChangeDetectorRef);
  cart$: Observable<AdnocOrderEntry[]> =
    this.anocActiveCartService.getEntries();
  /**
   * Quote handling requires a logged-in user. We cannot enforce that via an authGuard here
   * because otheriwise the entire cart page would need an authenticated user. So we check on
   * the view and don't render the button if the user is not logged in.
   */
  isLoggedIn$ = this.authService.isUserLoggedIn();
  validDateAndAddress!: boolean;
  flatgroup!: AdnocOrderEntry[];
  activeCartId = '';
  iscStoreEnable = false;
  disableProceedToQuote = true;
  loader = false;
  /**
   * Creates a new quote and triggers the navigation according to route 'quoteDetails',
   * in order to land on the quote details page.
   */
  ngOnInit(): void {
    this.cart$.pipe(takeUntil(this.destroy$)).subscribe((entries) => {
      const allHaveDeliveryAddress = _.every(
        entries,
        (entry) => !!entry.deliveryAddress
      );
      this.disableProceedToQuote = !allHaveDeliveryAddress;
      this.cd?.detectChanges();
    });
    this.anocActiveCartService
      .getActiveCartId()
      .pipe(takeUntil(this.destroy$))
      .subscribe((cartId: string) => (this.activeCartId = cartId));

    this.anocActiveCartService.cartDateAndAddress$
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (entries) => {
          this.validDateAndAddress = entries.btnDisable;
          this.flatgroup = entries.data;
          if (this.flatgroup) {
            this.iscStoreEnable = this.flatgroup.some(
              (item) => item.division === '23'
            );
          }
          this.cd?.detectChanges();
        },
      });
  }

  goToQuoteDetails(): void {
    this.loader = true;
    this.disableProceedToQuote = true;
    if (this.flatgroup) {
      const newEntries = _.sortBy(
        _.map(this.flatgroup, (item, i) => ({
          entryNumber: item.entryNumber ?? 0,
          deliveryAddress: {
            id: item.requestedShippingAddress ?? '',
          },
          namedDeliveryDate: item.namedDeliveryDate
            ? this.dateFormat(item.namedDeliveryDate)
            : '',
          incoTerms: {
            code: item.incoTermsCode || item.incoTerms?.code,
          },
        }))
      );

      const payload = {
        cartId: this.activeCartId,
        orderEntryList: {
          orderEntries: newEntries,
        },
      };

      this.quoteFacade
        .cartToQuote(payload, this.activeCartId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (data) => {
            this.routingService.go({
              cxRoute: 'quoteDetails',
              params: { quoteId: data.code },
            });
            this.loader = false;
            this.disableProceedToQuote = false;
            this.anocActiveCartService.reloadActiveCart();
          },
          error: (error) => {
            let errorMessage = error.error.errors[0]?.message;
            if (errorMessage) {
              this.loader = false;
              this.disableProceedToQuote = false;
              this.cd?.detectChanges();
              errorMessage = errorMessage
                .replace('CommerceCartModificationException:', '')
                .trim();
            }
            this.globalMessageService.add(
              errorMessage ??
                'There are cross division products in your cart, quote can be created for single division only.', // Provide default message
              GlobalMessageType.MSG_TYPE_ERROR
            );
          },
        });
    }
  }
  dateFormat(date: Date) {
    const data = new Date(date);
    const offset = data.getTimezoneOffset();
    const offsetSign = offset <= 0 ? '+' : '-';
    const absOffset = Math.abs(offset);
    const offsetHours = String(Math.floor(absOffset / 60)).padStart(2, '0');
    const offsetMinutes = String(absOffset % 60).padStart(2, '0');

    const year = data.getFullYear();
    const month = String(data.getMonth() + 1).padStart(2, '0');
    const day = String(data.getDate()).padStart(2, '0');
    const hours = String(data.getHours()).padStart(2, '0');
    const minutes = String(data.getMinutes()).padStart(2, '0');
    const seconds = String(data.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}${offsetSign}${offsetHours}:${offsetMinutes}`;
  }
  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    this.destroy$.next();
    this.destroy$.complete();
  }
}
