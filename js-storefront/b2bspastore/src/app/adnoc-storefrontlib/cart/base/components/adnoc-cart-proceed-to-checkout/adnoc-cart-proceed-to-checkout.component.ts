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
  OnInit,
  ViewEncapsulation,
} from '@angular/core';
import {
  Event,
  NavigationCancel,
  NavigationEnd,
  Router,
} from '@angular/router';
import {
  Observable,
  Subject,
  Subscription,
  takeUntil,
  switchMap,
  tap,
  distinctUntilChanged,
} from 'rxjs';
import { AdnocActiveCartService } from '../../core/facade/adnoc-active-cart.service';
import _ from 'lodash';
import { AdnocOrderEntry } from '../../../../../core/model/adnoc-cart.model';
import { GlobalMessageType, RoutingService } from '@spartacus/core';
import { AdnocActiveCartFacade } from '../../root/facade/adnoc-active-cart.facade';
import { Store } from '@ngrx/store';
import { EntryState } from '../../core/adnoc-store/adnoc-cart-state/adnoc-cart.state';
import moment from 'moment-timezone';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';
@Component({
  selector: 'adnoc-cart-proceed-to-checkout',
  templateUrl: './adnoc-cart-proceed-to-checkout.component.html',
  styleUrls: ['./adnoc-cart-proceed-to-checkout.component.scss'],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class CartProceedToCheckoutComponent implements OnInit, OnDestroy {
  cartValidationInProgress = false;

  protected subscription = new Subscription();
  protected anocActiveCartService = inject(AdnocActiveCartService);
  protected activeCartService = inject(AdnocActiveCartFacade);
  protected routingService = inject(RoutingService);
  protected globalMessageService = inject(AdnocGlobalMessageService);
  protected destroy$ = new Subject<void>();
  protected store = inject(Store<{ entriesState: EntryState }>);
  cart$: Observable<AdnocOrderEntry[]> = this.activeCartService.getEntries();

  activeCartId = '';
  validDateAndAddress = true;
  flatgroup!: AdnocOrderEntry[];
  disableProceedToCheckout = true;
  reCalculation = true;
  updateCheckoutRequired = true;
  loader = false;

  constructor(
    router: Router,
    // eslint-disable-next-line @typescript-eslint/unified-signatures
    cd?: ChangeDetectorRef
  );
  /**
   * @deprecated since 5.2
   */
  constructor(router: Router);
  constructor(protected router: Router, protected cd?: ChangeDetectorRef) {
    this.cart$.pipe(takeUntil(this.destroy$)).subscribe((entries) => {
      const allHaveDeliveryAddress = _.every(
        entries,
        (entry) => !!entry.deliveryAddress
      );
      this.disableProceedToCheckout = !allHaveDeliveryAddress;
    });
  }

  ngOnInit(): void {
    this.anocActiveCartService
      .getActiveCartId()
      .pipe(takeUntil(this.destroy$))
      .subscribe((cartId: string) => (this.activeCartId = cartId));

    this.subscription.add(
      this.router.events.subscribe((event: Event) => {
        if (
          event instanceof NavigationEnd ||
          event instanceof NavigationCancel
        ) {
          this.cartValidationInProgress = false;
          this.cd?.markForCheck();
        }
      })
    );

    this.anocActiveCartService.cartDateAndAddress$
      .pipe(takeUntil(this.destroy$), distinctUntilChanged())
      .subscribe({
        next: (entries) => {
          this.validDateAndAddress = entries.btnDisable;
          this.flatgroup = entries.data;
          this.updateCheckoutRequired = true;
          this.cd?.detectChanges();
        },
      });

    this.subscription.add(
      this.router.events.subscribe((event: Event) => {
        if (
          event instanceof NavigationEnd ||
          event instanceof NavigationCancel
        ) {
          this.cartValidationInProgress = true;
          this.cd?.markForCheck();
        } else{
          this.cartValidationInProgress = false;
          this.cd?.markForCheck();
        }
      })
    );
  }

  disableButtonWhileNavigation(): void {
    if (this.flatgroup) {
      this.loader = true;
      const newEntries = _.sortBy(
        _.map(this.flatgroup, (item, i) => ({
          entryNumber: item.entryNumber ?? 0,
          deliveryAddress: {
            id:
              item.division === '23' && item.incoTerms?.code === 'PICKUP'
                ? item.deliveryPointOfService.id ?? ''
                : item.deliveryAddress?.id ?? '',
          },
          namedDeliveryDate: item.namedDeliveryDate
            ? this.dateFormat(item.namedDeliveryDate)
            : '',
          incoTerms: {
            code: item.incoTermsCode || item.incoTerms?.code,
          },
        }))
      );
      const payLoad = { orderEntries: newEntries };
      this.anocActiveCartService
        .updateforcheckout(payLoad, this.activeCartId)
        .pipe(takeUntil(this.destroy$))
        .subscribe({
          next: (data) => {
            this.cartValidationInProgress = true;
            this.anocActiveCartService.reloadActiveCart();
            this.disableProceedToCheckout = false;
            this.updateCheckoutRequired = false;
            this.loader = false;
            this.cd?.detectChanges();
          },
          error: (error) => {
            this.cartValidationInProgress = false;
            this.disableProceedToCheckout = true;
            this.updateCheckoutRequired = true;
            this.loader = false;
            this.cd?.detectChanges();
            const errorMessage = error.error.errors[0]?.message;
            this.globalMessageService.add(
              errorMessage ??
                'Duplicate entry exist in the cart. Please remove to proceed', // Provide default message
              GlobalMessageType.MSG_TYPE_ERROR
            );
          },
          complete: () => {
            this.cartValidationInProgress = false;
            this.loader = false;
          },
        });
    }
  }

  dateFormat(date: Date | string): string {
    // Convert the date to Gulf Standard Time (GST) and format it
    return moment(date).tz('Asia/Dubai').format('YYYY-MM-DDTHH:mm:ssZ');
  }

  proceedToCheckout(): void {
    this.cartValidationInProgress = true;
    if (this.updateCheckoutRequired) {
      if (this.flatgroup) {
        const newEntries = _.sortBy(
          _.map(this.flatgroup, (item, i) => ({
            entryNumber: item.entryNumber ?? 0,
            deliveryAddress: {
              id:
                item.division === '23' && item.incoTerms?.code === 'PICKUP'
                  ? item.deliveryPointOfService.id ?? ''
                  : item.deliveryAddress?.id ?? '',
            },
            namedDeliveryDate: item.namedDeliveryDate
              ? this.dateFormat(item.namedDeliveryDate)
              : '',
            incoTerms: {
              code: item.incoTermsCode || item.incoTerms?.code,
            },
          }))
        );

        let payLoad = { orderEntries: newEntries };
        this.anocActiveCartService
          .updateforcheckout(payLoad, this.activeCartId)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: (data) => {
              this.cartValidationInProgress = false;
              this.anocActiveCartService.reloadActiveCart();

              this.disableProceedToCheckout = true;
              this.routingService.go({ cxRoute: 'checkout' });
              this.cd?.detectChanges();
            },
            error: (error) => {
              this.cartValidationInProgress = false;
              this.disableProceedToCheckout = false;
              this.cd?.detectChanges();
              const errorMessage = error.error.errors[0]?.message;
              this.globalMessageService.add(
                errorMessage ??
                  'Duplicate entry exist in the cart. Please remove to proceed', // Provide default message
                GlobalMessageType.MSG_TYPE_ERROR
              );
            },
          });
        this.cd?.detectChanges();
      }
    } else {
       this.cartValidationInProgress = true; 
      this.routingService.go({ cxRoute: 'checkout' });
    }
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    this.destroy$.next();
    this.destroy$.complete();
  }
}
