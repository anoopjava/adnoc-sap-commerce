/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  OnDestroy,
  OnInit,
  ViewEncapsulation,
} from '@angular/core';
import {
  Address,
  CurrencyService,
  LanguageService,
  TranslationService,
} from '@spartacus/core';
import {
  AccountSummaryDetails,
  AccountSummaryFacade,
} from '@spartacus/organization/account-summary/root';
import { Card } from '@spartacus/storefront';
import { Observable, Subscription } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

@Component({
  selector: 'cx-account-summary-header',
  templateUrl: './adnoc-account-summary-header.component.html',
  styleUrl: './adnoc-account-summary-header.component.scss',
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'adnoc-account-summary-header' },
  standalone: false,
})
export class AdnocAccountSummaryHeaderComponent implements OnInit, OnDestroy {
  notApplicable!: string;
  headerDetails$: Observable<AccountSummaryDetails>;

  protected subscriptions = new Subscription();

  constructor(
    protected accountSummaryFacade: AccountSummaryFacade,
    protected languageService: LanguageService,
    protected translation: TranslationService,
    protected currencyService: CurrencyService
  ) {
    this.headerDetails$ = this.languageService.getActive().pipe(
      switchMap(() => this.currencyService.getActive()),
      switchMap(() => this.accountSummaryFacade.getAccountSummary())
    );
  }

  ngOnInit(): void {
    this.subscriptions.add(
      this.translation
        .translate(
          'accountSummaryTranslations.orgAccountSummary.details.notApplicable'
        )
        .subscribe((text) => (this.notApplicable = text))
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
  }

  getIdCardContent(id?: string): Observable<Card> {
    return this.translation
      .translate('accountSummaryTranslations.orgAccountSummary.details.uid')
      .pipe(
        map((idTitle) => ({
          title: idTitle,
          text: [id || this.notApplicable],
        }))
      );
  }

  getNameCardContent(name?: string): Observable<Card> {
    return this.translation
      .translate('accountSummaryTranslations.orgAccountSummary.details.name')
      .pipe(
        map((nameTitle) => ({
          title: nameTitle,
          text: [name || this.notApplicable],
        }))
      );
  }

  getAddressCardContent(billingAddress?: Address): Observable<Card> {
    return this.translation
      .translate('accountSummaryTranslations.orgAccountSummary.details.address')
      .pipe(
        map((addressTitle) => {
          const address = billingAddress?.formattedAddress;
          const country = billingAddress?.country?.name;
          return {
            title: addressTitle,
            text: Boolean(billingAddress)
              ? [address, country]
              : [this.notApplicable],
          } as Card;
        })
      );
  }

  getCreditRepCardContent(creditRep?: string): Observable<Card> {
    return this.translation
      .translate(
        'accountSummaryTranslations.orgAccountSummary.details.creditRep'
      )
      .pipe(
        map((creditRepTitle) => ({
          title: creditRepTitle,
          text: [creditRep || this.notApplicable],
        }))
      );
  }

  getCreditLineCardContent(creditLine?: string): Observable<Card> {
    return this.translation
      .translate(
        'accountSummaryTranslations.orgAccountSummary.details.creditLine'
      )
      .pipe(
        map((creditLineTitle) => ({
          title: creditLineTitle,
          text: [creditLine || this.notApplicable],
        }))
      );
  }

  getCurrentBalanceCardContent(currentBalance?: string): Observable<Card> {
    return this.translation
      .translate(
        'accountSummaryTranslations.orgAccountSummary.details.currentBalance'
      )
      .pipe(
        map((currentBalanceTitle) => ({
          title: currentBalanceTitle,
          text: [currentBalance || this.notApplicable],
        }))
      );
  }

  getOpenBalanceCardContent(openBalance?: string): Observable<Card> {
    return this.translation
      .translate(
        'accountSummaryTranslations.orgAccountSummary.details.openBalance'
      )
      .pipe(
        map((openBalanceTitle) => ({
          title: openBalanceTitle,
          text: [openBalance || this.notApplicable],
        }))
      );
  }

  getPastDueBalanceCardContent(pastDueBalance?: string): Observable<Card> {
    return this.translation
      .translate(
        'accountSummaryTranslations.orgAccountSummary.details.pastDueBalance'
      )
      .pipe(
        map((pastDueBalanceTitle) => ({
          title: pastDueBalanceTitle,
          text: [pastDueBalance ?? this.notApplicable],
        }))
      );
  }
}
