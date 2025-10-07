/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectorRef,
  Component,
  inject,
  Input,
  OnDestroy,
  OnInit,
  Optional,
} from '@angular/core';
import { OutletContextData, PageLayoutService } from '@spartacus/storefront';
import {
  distinctUntilChanged,
  Observable,
  Subject,
  Subscription,
  takeUntil,
} from 'rxjs';
import { AdnocActiveCartService } from '../../../core/facade/adnoc-active-cart.service';
import {
  AdnocCart,
  B2BcreditLimit,
} from '../../../../../checkout/b2b/assets/checkout/checkout-model';
import { selectCreditLimit } from '../../../../../checkout/b2b/b2b-store/selector/creditLimit.selector';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'adnoc-order-summary',
    templateUrl: './adnoc-order-summary.component.html',
    standalone: false
})
export class AdnocOrderSummaryComponent implements OnInit, OnDestroy {
  @Input()
  cart: AdnocCart | undefined;

  protected subscription = new Subscription();
  anocActiveCartService = inject(AdnocActiveCartService);
  protected store = inject(Store);
  readonly destroy$ = new Subject<void>();
  amountToBePaid = 0;
  creditLimitValue$!: Observable<B2BcreditLimit>;
  reviewPageAmountToBePaid = 0;
  protected pageLayoutService = inject(PageLayoutService);
  protected route = inject(ActivatedRoute);
  hasReviewOrder = false;
  checkRule = false;

  constructor(
    protected cd: ChangeDetectorRef,
    @Optional() protected outlet?: OutletContextData<any>
  ) {}

  ngOnInit(): void {
    this.route.url.pipe(takeUntil(this.destroy$)).subscribe((segments) => {
      this.hasReviewOrder = segments.some((s) => s.path === 'review-order');
    });

    this.creditLimitValue$ = this.store.select(selectCreditLimit);
    if (this.outlet?.context$) {
      this.subscription.add(
        //@ts-ignore
        this.outlet.context$.subscribe((context) => (this.cart = context))
      );
    }

    this.anocActiveCartService.creditLimitFlow$
      .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(({ isChecked, creditLimitValue }) => {
        const totalPriceWithTaxValue = this.cart?.totalPriceWithTax?.value || 0;

        if (isChecked && creditLimitValue > 0 && totalPriceWithTaxValue > creditLimitValue) {
          this.amountToBePaid = totalPriceWithTaxValue - creditLimitValue;
        } else {
          this.amountToBePaid = 0;
        }
        this.cd.detectChanges();
      });
    if (this.hasReviewOrder) {
      this.creditLimitValue$
        .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
        .subscribe((creditLimitValue) => {
          this.checkRule = creditLimitValue?.checkRule === 'Z2' ? true : false;
          const creditLimit = creditLimitValue?.availableCl || 0;
          const totalPriceWithTaxValue =
            this.cart?.totalPriceWithTax?.value || 0;

          if (+creditLimit > 0 && totalPriceWithTaxValue > +creditLimit) {
            this.reviewPageAmountToBePaid = totalPriceWithTaxValue - +creditLimit;
          } 
          this.cd.detectChanges();
        });
    }
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    this.destroy$.next();
    this.destroy$.complete();
  }
}
