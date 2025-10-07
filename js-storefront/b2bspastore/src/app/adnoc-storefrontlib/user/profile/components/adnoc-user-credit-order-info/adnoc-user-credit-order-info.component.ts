import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  inject,
  ViewEncapsulation,
} from '@angular/core';
import { AdnocUserCreditOrderInfoComponentService } from './adnoc-user-credit-order-info.component.service';
import {
  catchError,
  distinctUntilChanged,
  filter,
  first,
  map,
  merge,
  Observable,
  of,
  shareReplay,
  Subject,
  switchMap,
  takeUntil,
  tap,
} from 'rxjs';
import { Store } from '@ngrx/store';
import { GlobalMessageType } from '@spartacus/core';
import {
  ICreditLimit,
  ICurrentUser,
  userOrdersSummary,
} from '../../../../services/apiServices/api-response.model';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';
import { selectCreditLimit } from '../../../../checkout/b2b/b2b-store/selector/creditLimit.selector';
import {
  saveCreditLimit,
  clearCreditLimit,
} from '../../../../checkout/b2b/b2b-store/actions/creditLimit.actions';

@Component({
  selector: 'adnoc-user-credit-order-info',
  templateUrl: './adnoc-user-credit-order-info.component.html',
  styleUrl: './adnoc-user-credit-order-info.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  standalone: false,
})
export class AdnocUserCreditOrderInfoComponent {
  protected adnocUserCreditOrderInfoService = inject(
    AdnocUserCreditOrderInfoComponentService
  );
  protected store = inject(Store);
  protected readonly destroy$ = new Subject<void>();
  constructor(
    protected globalMessageService: AdnocGlobalMessageService,
    protected cd: ChangeDetectorRef
  ) {}

  currentUser$: Observable<ICurrentUser> = this.adnocUserCreditOrderInfoService
    .getCurrentUser()
    .pipe(shareReplay(1));
  // Merge initial currentUser$ and refresh on payerUpdated$
  private b2bUnitUid$: Observable<string> = merge(
    this.adnocUserCreditOrderInfoService.getCurrentUser(),
    this.adnocUserCreditOrderInfoService.payerUpdated$.pipe(
      switchMap(() => this.adnocUserCreditOrderInfoService.getCurrentUser())
    )
  ).pipe(
    map((user: ICurrentUser) => user.orgUnit.uid),
    shareReplay(1)
  );
  creditLimit: any = {};
  orderInfo$: Observable<userOrdersSummary> | undefined;
  creditLimitErrorMsg: string = '';
  private unsubscribe$ = new Subject<void>();
  adminUser: boolean = true;
  ngOnInit(): void {
    merge(
      this.currentUser$,
      this.adnocUserCreditOrderInfoService.payerUpdated$.pipe(
        switchMap(() => this.currentUser$)
      )
    )
      .pipe(
        takeUntil(this.unsubscribe$),
        filter((user: ICurrentUser) => user?.roles?.[0] !== 'b2badmingroup') // Only proceed if role is not 'b2badmingroup'
      )
      .subscribe(() => {
        this.adminUser = false;
        this.refreshCreditLimitData(false);
        this.refreshOrderInfoData();
      });
  }

  refreshCreditLimitData(refreshButton: boolean) {
    this.creditLimit = null;
    this.store
      .select(selectCreditLimit)
      .pipe(
        switchMap((creditLimit) => {
          if (creditLimit && !refreshButton) {
            return of(creditLimit);
          }
          refreshButton = false;
          return this.b2bUnitUid$.pipe(
            first(),
            switchMap((b2bUnitUid) =>
              this.adnocUserCreditOrderInfoService
                .getCreditLimit(b2bUnitUid)
                .pipe(
                  map((data) => {
                    return {
                      ...data.b2BCreditLimit,
                      updatedOn: data.updatedOn,
                    };
                  }),
                  tap((fetchedCreditLimit) => {
                    this.store.dispatch(
                      saveCreditLimit({ creditLimit: fetchedCreditLimit })
                    );
                  }),
                  catchError((error) => {
                    this.globalMessageService.remove(
                      GlobalMessageType.MSG_TYPE_ERROR
                    );
                    this.creditLimitErrorMsg =
                      error?.error?.errors
                        ?.map((err: any) => err.message)
                        .join('\n') || 'Unknown error';
                    return of({} as ICreditLimit);
                  })
                )
            )
          );
        }),
        distinctUntilChanged(),
        takeUntil(this.destroy$)
      )
      .subscribe((data) => {
        this.creditLimit = data;
        this.cd.detectChanges();
      });
  }

  refreshOrderInfoData() {
    this.orderInfo$ = this.adnocUserCreditOrderInfoService.getUserOderSummary();
  }

  ngOnDestroy() {
    this.unsubscribe$.next();
    this.unsubscribe$.complete(); // Clean up the observable subscription
    this.store.dispatch(clearCreditLimit());
  }
}
