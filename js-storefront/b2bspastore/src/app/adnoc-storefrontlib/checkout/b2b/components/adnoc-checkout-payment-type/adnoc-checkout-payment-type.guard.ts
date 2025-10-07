import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';
import { combineLatest, Observable, of } from 'rxjs';
import {
  map,
  switchMap,
  take,
  catchError,
  distinctUntilChanged,
} from 'rxjs/operators';
import { GlobalMessageType } from '@spartacus/core';
import * as _ from 'lodash';
import { CheckoutPaymentTypeFacade } from '../../root/facade/checkout-payment-type.facade';
import { ICurrentUser } from '../../assets/checkout/checkout-model';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';
import { AdnocActiveCartFacade } from '../../../../cart/base/root/facade/adnoc-active-cart.facade';
import {
  AdnocConfig,
  AdnocOrderEntry,
} from '../../../../../core/model/adnoc-cart.model';
import { Store } from '@ngrx/store';
import { selectConfig } from '../../../../cart/base/core/adnoc-store/adnoc-cart-state/adnoc-cart.selector';
import { setConfig } from '../../../../cart/base/core/adnoc-store/adnoc-cart-state/adnoc-cart.action';

@Injectable({
  providedIn: 'root',
})
export class AdnocCheckoutPaymentTypeGuard {
  private readonly currentUser$: Observable<ICurrentUser>;
  private readonly b2bUnitUid$: Observable<string>;
  config$: Observable<{} | AdnocConfig>;
  minDate!: {} | AdnocConfig;
  constructor(
    private router: Router,
    protected globalMessageService: AdnocGlobalMessageService,
    protected checkoutPaymentTypeFacade: CheckoutPaymentTypeFacade,
    protected activeCartService: AdnocActiveCartFacade,
    protected store: Store
  ) {
    this.config$ = this.store.select(selectConfig);

    this.config$.pipe(take(1)).subscribe({
      next: (config) => {
        if (!config || Object.keys(config).length === 0) {
          this.activeCartService
            .cartConfig()
            .pipe(
              distinctUntilChanged(),
              take(1),
              map((data) => data.adnocConfigs)
            )
            .subscribe((data: AdnocConfig[]) => {
              if (data) {
                const configObject = _.mapValues(
                  _.keyBy(data, 'configKey'),
                  'configValue'
                ) as AdnocConfig;
                this.store.dispatch(setConfig({ config: configObject }));
                this.minDate = configObject;
              }
            });
        } else {
          this.minDate = config;
        }
      },
    });

    this.currentUser$ = this.createCurrentUserObservable();
    this.b2bUnitUid$ = this.createB2bUnitUidObservable();
  }

  canActivate(): Observable<boolean | UrlTree> {
    return combineLatest([
      this.checkPaymentTypeCondition(),
      this.activeCartService.getEntries(),
    ]).pipe(
      take(1),
      map(([isPaymentTypeValid, entries]) => {
        const today = new Date();
        today.setHours(0, 0, 0, 0); // Start of today
        const upper = new Date(today);
        const minDays = Number(
          (this.minDate as AdnocConfig)?.minRequestedDelieveryDays
        );
        upper.setDate(today.getDate() + minDays); // Start of "today + min requested delivery days"
        upper.setHours(0, 0, 0, 0); // Ensure time is reset for accurate comparison

        let hasDeliveryDateError = false;
        const areEntriesValid = _.every(entries, (entry: AdnocOrderEntry) => {
          if (
            !_.has(entry, 'deliveryAddress') ||
            !_.has(entry, 'namedDeliveryDate') ||
            !entry.namedDeliveryDate
          ) {
            return false;
          }
          const d = new Date(entry.namedDeliveryDate);
          d.setHours(0, 0, 0, 0); // Normalize time to start of the day for date-only comparison

          if (!isNaN(d.getTime()) && d <= upper) {
            hasDeliveryDateError = true;
            return false;
          }
          return true;
        });

        if (!isPaymentTypeValid) {
          this.handleRedirect('payments.overDueError');
          return false;
        }

        if (!areEntriesValid) {
          if (hasDeliveryDateError) {
            this.handleRedirect('payments.namedDeliveryDateError');
          } else {
            this.handleRedirect('payments.cartValidationError');
          }
          return false;
        }

        return true;
      })
    );
  }

  private createCurrentUserObservable(): Observable<ICurrentUser> {
    return this.checkoutPaymentTypeFacade.getCurrentUser();
  }

  private createB2bUnitUidObservable(): Observable<string> {
    return this.currentUser$.pipe(map((user) => user.orgUnit.uid));
  }

  checkPaymentTypeCondition(): Observable<boolean> {
    return this.b2bUnitUid$.pipe(
      switchMap((b2bUnitUid) =>
        this.checkoutPaymentTypeFacade.creditSimulationCheck(b2bUnitUid).pipe(
          map((exists) => exists?.creditSimulation ?? false),
          catchError((error) => {
            const errorMessage = error.error.errors[0]?.message;
            this.handleRedirect(errorMessage);
            return of(false);
          })
        )
      )
    );
  }

  private handleRedirect(errorMessageKey: string): void {
    this.globalMessageService.remove(GlobalMessageType.MSG_TYPE_ERROR);
    this.router.navigate(['/cart']).then(() => {
      if (errorMessageKey && errorMessageKey.startsWith('payments.')) {
        this.globalMessageService.add(
          { key: errorMessageKey },
          GlobalMessageType.MSG_TYPE_ERROR
        );
      } else {
        this.globalMessageService.add(
          errorMessageKey ??
            'Technical issue please retry later or contact support',
          GlobalMessageType.MSG_TYPE_ERROR
        );
      }
    });
  }
}
