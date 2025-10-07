import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  OnInit,
  ViewEncapsulation,
} from '@angular/core';
import { AdnocOtploginComponentService } from '../../../../components/adnoc-otp-login/adnoc-otp-login-service';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { distinctUntilChanged, Subject, switchMap, takeUntil } from 'rxjs';
import { payerInfo } from '../../../../../core/model/adnoc-cart.model';
import { AdnocActiveCartService } from '../../../../cart/base/core/facade/adnoc-active-cart.service';
import {
  CurrencyService,
  GlobalMessageType,
  TranslationService,
} from '@spartacus/core';
import { tap } from 'lodash';
import { CheckoutPaymentTypeFacade } from '../../../../checkout/b2b/root/facade/checkout-payment-type.facade';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

@Component({
  selector: 'adnoc-statement-of-account',
  templateUrl: './adnoc-statement-of-account.component.html',
  styleUrl: './adnoc-statement-of-account.component.scss',
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class AdnocStatementOfAccount implements OnInit, OnDestroy {
  errorMsg: string = '';
  isError: string = '';
  isDisabled: boolean = true;
  b2bUnitUid: string = '';
  payers!: payerInfo[];
  destroy$ = new Subject<void>();
  currencyIsoCode = '';
  downloadAttachment$ = new Subject<{ b2bUnitUid: string; currency: string }>();
  loader = false;
  constructor(
    protected payerService: AdnocOtploginComponentService,
    protected router: Router,
    protected http: HttpClient,
    protected cdr: ChangeDetectorRef,
    protected attachmentService: AdnocActiveCartService,
    protected translationService: TranslationService,
    protected paymentFacade: CheckoutPaymentTypeFacade,
    protected globalMessageService: AdnocGlobalMessageService
  ) {}

  ngOnInit(): void {
    this.payerService
      .getPayerOptions()
      .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.payers = data?.b2bUnitListData;
          if (this.payers?.length === 1) {
            this.b2bUnitUid = this.payers[0].uid;
          }
          this.cdr.detectChanges();
        },
        error: () => {
          this.translationService
            .translate('common.statementOfAccount.payerServiceError')
            .subscribe((translation) => {
              this.isError = translation;
              this.cdr.detectChanges();
            });
        },
      });

    this.paymentFacade
      .getCurrentUser()
      .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe((currencyIso) => {
        if (currencyIso) {
          this.currencyIsoCode = currencyIso?.currency?.isocode;
        }
      });

    this.initializeDownloadAttachment();
  }

  validateFields(): boolean {
    if (!this.b2bUnitUid) {
      return true;
    }
    return false;
  }

  onPayerChange(event: Event): void {
    this.b2bUnitUid = (<HTMLInputElement>event.target).value;
  }

  downloadAttachment() {
    if (this.validateFields()) {
      return;
    }
    const payload = {
      b2bUnitUid: this.b2bUnitUid ?? '',
      currency: this.currencyIsoCode ?? 'AED',
    };

    this.downloadAttachment$.next(payload);
  }

  initializeDownloadAttachment() {
    this.downloadAttachment$
      .pipe(
        takeUntil(this.destroy$),
        switchMap((payload) => {
          this.loader = true;
          this.cdr.detectChanges();
          return this.attachmentService
            .statementOfAccount(payload)
            .pipe(distinctUntilChanged());
        })
      )
      .subscribe({
        next: (response) => {
          this.loader = false;
          this.cdr.detectChanges();
          if (response.body) {
            const headers = response.headers;
            const contentDisposition = headers?.get('content-disposition');
            let fileName = contentDisposition
              ? contentDisposition
                  .split(';')[1]
                  .split('filename=')[1]
                  .replace(/"/g, '')
                  .replace('.pdf', '')
                  .trim()
              : `StatementOfAccount.pdf`;
            const blobUrl = window.URL.createObjectURL(response.body);
            const a = document.createElement('a');
            a.href = blobUrl;
            a.download = fileName;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(blobUrl);
            this.isError = '';
          } else {
            this.translationService
              .translate('common.statementOfAccount.attachmentServiceError')
              .subscribe((translation) => {
                this.isError = translation;
                this.cdr.detectChanges();
              });
          }
          this.cdr.detectChanges();
        },
        error: () => {
          this.globalMessageService.remove(GlobalMessageType.MSG_TYPE_ERROR);
          this.translationService
            .translate('common.statementOfAccount.attachmentServiceError')
            .subscribe((translation) => {
              this.isError = translation;
              this.cdr.detectChanges();
            });
          this.loader = false;
          this.cdr.detectChanges();
        },
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
