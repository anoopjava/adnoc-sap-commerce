import { DOCUMENT } from '@angular/common';
import {
  ChangeDetectionStrategy,
  Component,
  inject,
  Inject,
  OnDestroy,
  OnInit,
  ViewEncapsulation,
} from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { selectPaymentResultInfo } from '../../../overdueInvoice-store/selector/overdueInvoice.selector';
import { OverdueInvoiceResultInfo } from '../adnoc-payer-overdue-invoices/adnoc-payer-overdue-invoices.model';
import { clearOverdueInvoicePaymentResult } from '../../../overdueInvoice-store/actions/overdueInvoice.actions';

@Component({
  selector: 'app-adnoc-overdue-invoices-payment-success',
  templateUrl: './adnoc-overdue-invoices-payment-success.component.html',
  styleUrl: './adnoc-overdue-invoices-payment-success.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  standalone: false,
})
export class AdnocOverdueInvoicesPaymentSuccessComponent
  implements OnInit, OnDestroy
{
  overdueInvoicepaymentResultStatus$: Observable<OverdueInvoiceResultInfo>;
  protected store = inject(Store);
  constructor(@Inject(DOCUMENT) private readonly document: Document) {
    this.overdueInvoicepaymentResultStatus$ = this.store.select(
      selectPaymentResultInfo
    );
  }

  ngOnInit(): void {
    this.document.body.classList.add('registartionPage', 'hide-header-footer');
  }

  ngOnDestroy(): void {
    this.document.body.classList.remove(
      'registartionPage',
      'hide-header-footer'
    );
    this.store.select(clearOverdueInvoicePaymentResult);
  }
}
