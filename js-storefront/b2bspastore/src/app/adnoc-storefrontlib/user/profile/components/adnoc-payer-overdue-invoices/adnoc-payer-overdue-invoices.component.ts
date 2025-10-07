import {
  Component,
  ChangeDetectionStrategy,
  ViewEncapsulation,
  ChangeDetectorRef,
  OnInit,
  OnDestroy,
  AfterViewInit,
  inject,
} from '@angular/core';
import { AdnocOtploginComponentService } from '../../../../components/adnoc-otp-login/adnoc-otp-login-service';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import {
  GlobalMessageType,
  TranslationService,
  WindowRef,
} from '@spartacus/core';
import {
  BehaviorSubject,
  Subject,
  distinctUntilChanged,
  map,
  of,
  switchMap,
  takeUntil,
} from 'rxjs';
import { payerInfo } from '../../../../../core/model/adnoc-cart.model';
import {
  IAdnocOverdueInvoice,
  IBankTransactionResponse,
  InvoicePaymentType,
} from './adnoc-payer-overdue-invoices.model';
import { MatTableDataSource } from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections';
import { AdnocOverdueInvoiceFacade } from '../../../core-overdueInvoice/facade/adnoc-overdue-invoice.facade';
import { Store } from '@ngrx/store';
import { saveOverdueInvoicePaymentResult } from '../../../overdueInvoice-store/actions/overdueInvoice.actions';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';
import { ICON_TYPE } from '@spartacus/storefront';

@Component({
  selector: 'adnoc-payer-overdue-invoices',
  templateUrl: './adnoc-payer-overdue-invoices.component.html',
  styleUrl: './adnoc-payer-overdue-invoices.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  standalone: false,
})
export class AdnocPayerOverdueInvoicesComponent
  implements OnInit, AfterViewInit, OnDestroy
{
  isError = '';
  payer = '';
  selectedPayment = '';
  payersList: payerInfo[] = [];
  paymentOptions: InvoicePaymentType[] = [];
  destroy$ = new Subject<void>();
  private scriptElement: HTMLScriptElement | null = null;
  showInvoiceList = false;
  isLoading = false;
  isProcessing = false;
  paymentInitialized = false;
  sessionId = '';
  paymentPortalUrl = '';
  resultIndicator = '';
  transactionID = '';
  paymentTypeImages = {
    BANK_TRANSFER: ['images/payment/bank.png'],
    CARD: [
      'images/payment/mastro.png',
      'images/payment/amex.png',
      'images/payment/visa.png',
    ],
  };
  displayedColumns: string[] = [
    'select',
    'documentNumber',
    'documentDate',
    'productGroup',
    'fiscalYear',
    'invoiceValue',
    'openValue',
    'currency',
    'status',
    'netDueDate',
  ];
  invoices = new MatTableDataSource<IAdnocOverdueInvoice>([]);
  selection = new SelectionModel<IAdnocOverdueInvoice>(true, []);
  protected activatedRoute = inject(ActivatedRoute);
  protected busy$ = new BehaviorSubject(false);
  iconTypes = ICON_TYPE;
  disableSelectAll = false;
  selectedCurrency!: string | null;

  constructor(
    protected payerService: AdnocOtploginComponentService,
    protected router: Router,
    protected http: HttpClient,
    protected cdr: ChangeDetectorRef,
    protected overdueInvoiceService: AdnocOverdueInvoiceFacade,
    protected translationService: TranslationService,
    protected globalMessage: AdnocGlobalMessageService,
    private store: Store,
    protected winRef: WindowRef
  ) {}

  ngAfterViewInit(): void {
    this.activatedRoute.queryParams
      .pipe(
        switchMap((params) => {
          const resultIndicator = params['resultIndicator'] ?? '';
          const transactionID = params['transactionID'] ?? '';
          this.isLoading = true;

          if (resultIndicator) {
            return this.overdueInvoiceService
              .getRetrieveOverdueInvoicePayment({ resultIndicator })
              .pipe(
                map((data) => ({
                  type: 'card',
                  transaction: data.transaction?.[0],
                  currency: data.currency ?? 'AED',
                  success: data.transaction?.[0]?.result === 'SUCCESS',
                }))
              );
          } else if (transactionID) {
            return this.overdueInvoiceService
              .getFinalizeOverdueInvoicePayment({
                transactionID,
              })
              .pipe(
                map((data) => ({
                  type: 'bank',
                  transaction: data.transaction,
                  currency: 'AED',
                  success: data.transaction?.responseCode === '0',
                }))
              );
          }
          this.isLoading = false;
          return of(null);
        })
      )
      .subscribe({
        next: (result) => {
          if (!result) {
            this.isLoading = false;
            return;
          }
          if (result.success) {
            this.dispatchOverdueInvoiceResult(
              result.transaction,
              result.currency
            );
            this.router.navigate(['/my-account/overdue-invoices-success']);
          } else {
            this.isLoading = false;
            if (result.type === 'card') {
              this.navigateToErrorPage();
            } else if (result.type === 'bank') {
              const data = result.transaction as IBankTransactionResponse;
              this.globalMessage.add(
                data.responseDescription,
                GlobalMessageType.MSG_TYPE_ERROR
              );
              this.busy$.next(false);
              this.router.navigate(['/my-account/overdue-invoices']);
            }
          }
        },
        error: (err) => {
          this.isLoading = false;
          const errorMessage = err.error.errors[0]?.message;
          this.globalMessage.add(
            errorMessage,
            GlobalMessageType.MSG_TYPE_ERROR
          );
          this.busy$.next(false);
          this.cdr.detectChanges();
          this.router.navigate(['/my-account/overdue-invoices']);
        },
        complete: () => {
          this.isLoading = false;
        },
      });
  }
  private dispatchOverdueInvoiceResult(
    transaction: any,
    currency: string
  ): void {
    this.store.dispatch(
      saveOverdueInvoicePaymentResult({
        overdueInvoiceResultInfo: {
          cardNumber: transaction.sourceOfFunds?.provided?.card?.number,
          referenceNumber: transaction?.approvalCode,
          receipt:
            transaction.transaction?.receipt || transaction?.transactionId,
          amount: transaction.transaction?.amount || transaction.amount?.value,
          currency,
        },
      })
    );
  }
  private navigateToErrorPage(): void {
    this.isLoading = false;
    this.globalMessage.add(
      { key: 'common.payerOverdueInovice.paymentInitServiceError' },
      GlobalMessageType.MSG_TYPE_ERROR
    );
    this.busy$.next(false);
    this.router.navigate(['/my-account/overdue-invoices']);
  }

  ngOnInit(): void {
    this.payerService
      .getPayerOptions()
      .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.payersList = data?.b2bUnitListData ?? [];
          if (this.payersList?.length === 1) {
            this.payer = this.payersList[0].uid;
          }
          this.cdr.detectChanges();
        },
        error: () =>
          this.setError('common.statementOfAccount.payerServiceError'),
      });
    this.loadMastercardScript();
  }

  setError(key: string) {
    this.translationService.translate(key).subscribe((translation) => {
      this.isError = translation;
      this.cdr.detectChanges();
    });
  }

  validateFields(): boolean {
    return !this.payer;
  }
  validateInvSelection(paymentType: string): boolean {
    const noInvoicesSelected = this.selection?.selected?.length === 0;
    const isBankTransferWithUSD =
      paymentType === 'BANK_TRANSFER' && this.selectedCurrency === 'USD';
    const isCardPaymentDisabled =
      paymentType === 'CARD' && !this.paymentInitialized;
    return noInvoicesSelected || isBankTransferWithUSD || isCardPaymentDisabled;
  }
  onPayerChange(event: Event): void {
    this.payer = (<HTMLInputElement>event.target).value;
  }

  getInvoicesList() {
    this.isLoading = true;
    this.showInvoiceList = false;
    this.clearSelection();
    this.overdueInvoiceService
      .getPayerOverdueInvoiceList({ payer: this.payer })
      .pipe(
        distinctUntilChanged(),
        takeUntil(this.destroy$),
        map((data: any) => data?.adnocOverdueInvoices)
      )
      .subscribe({
        next: (data) => {
          this.invoices.data = Array.isArray(data)
            ? data.map((invoice: any) => ({ ...invoice, selected: false }))
            : [];
          this.showInvoiceList = true;
          this.isLoading = false;
          this.isError = '';
          this.cdr.detectChanges();
          this.getPaymentType();
          const currencies = this.invoices.data.map((invoice) =>
            invoice.currency?.toUpperCase()
          );
          this.disableSelectAll =
            currencies.includes('USD') && currencies.includes('AED');
        },
        error: () => {
          this.isLoading = false;
          this.globalMessage.remove(GlobalMessageType.MSG_TYPE_ERROR);
          this.setError('common.payerOverdueInovice.invoiceServiceError');
        },
      });
  }

  getPaymentType() {
    this.overdueInvoiceService
      .getInvoicePaymentType()
      .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.paymentOptions = data?.paymentTypes ?? [];
          this.cdr.detectChanges();
        },
        error: () =>
          this.setError('common.payerOverdueInovice.paymentTypeServiceError'),
      });
  }

  isAllSelected(): boolean {
    const selectableInvoices = this.getSelectableInvoices();
    return (
      selectableInvoices.length > 0 &&
      this.selection.selected.length === selectableInvoices.length
    );
  }
  onSelectAllChange(): void {
    const selectableInvoices = this.getSelectableInvoices();
    if (this.isAllSelected()) {
      this.clearSelection();
    } else {
      if (selectableInvoices.length > 0) {
        this.selection.select(...selectableInvoices);
        this.selectedCurrency = selectableInvoices[0].currency ?? null;
      } else {
        this.clearSelection();
      }
    }
  }
  private getSelectableInvoices(): IAdnocOverdueInvoice[] {
    return (
      this.invoices?.data?.filter(
        (invoice) => !this.isCurrencyDisabled(invoice)
      ) ?? []
    );
  }
  private clearSelection(): void {
    this.selection.clear();
    this.selectedCurrency = null;
    this.selectedPayment = '';
  }
  calculateTotalDue(): number {
    return this.selection.selected.reduce(
      (acc, curr) =>
        ['Open', 'Overdue'].includes(curr.status ?? '')
          ? acc + parseFloat(String(curr.dueAmount ?? '0'))
          : acc,
      0
    );
  }
  getSelectedRows(): IAdnocOverdueInvoice[] {
    return this.selection.selected;
  }
  validatePayNow(): boolean {
    return (
      !this.selectedPayment ||
      this.selection.selected.length === 0 ||
      this.calculateTotalDue() <= 0
    );
  }

  payNow() {
    this.isLoading = true;
    const payload = {
      payerId: this.payer,
      totalAmount: this.calculateTotalDue(),
      paymentType: this.selectedPayment,
      currency: this.selectedCurrency,
      invoiceDetails: this.getSelectedRows().map((row) => ({
        companyCode: row.companyCode,
        invoiceNumber: row.documentNumber,
        fiscalYear: row.fiscalYear,
      })),
    };
    this.overdueInvoiceService
      .getInvoicePaymentSessionId(payload)
      .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.isLoading = false;
          this.sessionId = data?.session?.id ?? '';
          this.paymentPortalUrl = data?.transaction?.paymentPage ?? '';
          const window = this.winRef.nativeWindow;

          if (
            this.selectedPayment === 'BANK_TRANSFER' &&
            window &&
            this.paymentPortalUrl
          ) {
            window.location.href = this.paymentPortalUrl;
            return;
          }
          if (this.sessionId) {
            this.initializeCheckout(this.sessionId);
            this.triggerPayment();
          }
          this.cdr.detectChanges();
        },
        error: () => {
          this.isLoading = false;
          this.globalMessage.remove(GlobalMessageType.MSG_TYPE_ERROR);
          this.setError('common.payerOverdueInovice.paymentInitServiceError');
        },
      });
  }

  initializeCheckout(sessionId: string): void {
    const Checkout = (window as any).Checkout;
    if (Checkout) Checkout.configure({ session: { id: sessionId } });
  }
  triggerPayment(): void {
    const Checkout = (window as any).Checkout;
    if (Checkout) Checkout.showPaymentPage();
  }
  loadMastercardScript(): void {
    this.overdueInvoiceService
      .getCardConfigUrlOverdueInvoicePayment()
      .subscribe({
        next: (res) => {
          const scriptUrl = res?.adnocConfigs?.[0]?.configValue ?? null;
          if (!scriptUrl) {
            this.setError('common.payerOverdueInovice.cardPaymentServiceError');
            return;
          }
          const script = document.createElement('script');
          script.src = `${scriptUrl}/static/checkout/checkout.min.js`;
          script.setAttribute('data-error', 'errorCallback');
          script.setAttribute('data-cancel', 'cancelCallback');
          script.onload = () => (this.paymentInitialized = true);
          this.scriptElement = script;
          document.head.appendChild(script);
          (window as any).errorCallback = () => (this.isProcessing = false);
          (window as any).cancelCallback = () => (this.isProcessing = false);
        },
        error: (err) => {
          const errorMessage = err.error.errors[0]?.message;
          this.globalMessage.add(
            { raw: errorMessage },
            GlobalMessageType.MSG_TYPE_ERROR
          );
          this.cdr?.detectChanges();
        },
      });
  }

  get hasInvoices(): boolean {
    return Array.isArray(this.invoices?.data) && this.invoices.data.length > 0;
  }
  get hasPaymentOptions(): boolean {
    return Array.isArray(this.paymentOptions) && this.invoices.data.length > 0;
  }
  toggleRowSelection(row: IAdnocOverdueInvoice): void {
    const isSelectable =
      ['Open', 'Overdue'].includes(row.status ?? '') &&
      !this.isCurrencyDisabled(row);
    if (!isSelectable) {
      return;
    }
    this.selection.toggle(row);
    if (this.selection.selected.length > 0) {
      this.selectedCurrency = this.selection.selected[0].currency ?? null;
    } else {
      this.selectedCurrency = null;
      this.selectedPayment = '';
    }
  }
  isCurrencyDisabled(row: any): boolean {
    return !!this.selectedCurrency && row.currency !== this.selectedCurrency;
  }
  get isAnyInvoiceSelected(): boolean {
    return this.selection.selected.length > 0;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.scriptElement?.parentNode)
      this.scriptElement.parentNode.removeChild(this.scriptElement);
    if (window) {
      (window as any).errorCallback = null;
      (window as any).cancelCallback = null;
    }
  }
}
