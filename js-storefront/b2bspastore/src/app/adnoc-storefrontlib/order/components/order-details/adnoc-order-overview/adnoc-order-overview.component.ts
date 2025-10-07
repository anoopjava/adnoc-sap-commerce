import { ChangeDetectionStrategy, Component } from '@angular/core';
import { OrderOverviewComponent } from '@spartacus/order/components';
import { Card } from '@spartacus/storefront';
import { combineLatest, map, Observable } from 'rxjs';
import { PaymentTransaction } from '../../../root/model/adnoc-order.model';

@Component({
  selector: 'cx-order-overview',
  templateUrl: './adnoc-order-overview.component.html',
  styleUrl: './adnoc-order-overview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'adnoc-order-overview' },
  standalone: false,
})
export class AdnocOrderOverviewComponent extends OrderOverviewComponent {
  override order$: Observable<any> = this.orderDetailsService.getOrderDetails();
  paymentMethodCardContent$!: Observable<any>;

  ngOnInit(): void {
    this.order$.subscribe((order) => {
      this.paymentMethodCardContent$ = this.getMethodOfPaymentCardContentData(
        order?.paymentTransactions || ''
      );
    });
  }

  getMethodOfPaymentCardContentData(
    hasPaymentInfo: PaymentTransaction[]
  ): Observable<Card> {
    return combineLatest([
      this.translation.translate('orderDetails.methodOfPayment'),
      this.translation.translate(
        'userOrderTranslations.order.paymentMethods.bankCard'
      ),
      this.translation.translate(
        'userOrderTranslations.order.paymentMethods.creditLimit'
      ),
      this.translation.translate(
        'userOrderTranslations.order.paymentMethods.bankTransfer'
      ),
    ]).pipe(
      map(([title, bankCardText, creditLimitText, bankTransferText]) => {
        const hasBankCard = hasPaymentInfo.some(
          (t) => !!t?.paymentInfo?.cardNumber
        );

        const hasCreditLimit = hasPaymentInfo.some(
          (t) => !t?.paymentInfo?.cardNumber && !t?.paymentInfo?.account
        );

        const hasBankTransfer = hasPaymentInfo.some(
          (t) => !!t?.paymentInfo?.account === true
        );

        const paymentMethods: string[] = [];

        if (hasBankCard) {
          paymentMethods.push(bankCardText);
        }

        if (hasBankTransfer) {
          paymentMethods.push(bankTransferText);
        }

        if (hasCreditLimit) {
          paymentMethods.push(creditLimitText);
        }

        return {
          title,
          text: [paymentMethods.join(' + ')],
        };
      })
    );
  }
}
