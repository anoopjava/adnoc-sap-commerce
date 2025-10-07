import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { OrderEntry } from '@spartacus/cart/base/root';
import { Observable, tap } from 'rxjs';
import { AdnocOrderAmendService } from '../../adnoc-amend-order.service';
import { GlobalMessageType, RoutingService } from '@spartacus/core';
import { AdnocGlobalMessageService } from '../../../../../../core/global-message/facade/adnoc-global-message.service';

@Component({
  selector: 'cx-cancel-order-confirmation',
  templateUrl: './adnoc-cancel-order-confirmation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'adnoc-cancel-order-confirmation' },
  standalone: false,
})
export class AdnocCancelOrderConfirmationComponent {
  orderCode!: string;
  form$: Observable<UntypedFormGroup>;
  entries$: Observable<OrderEntry[]>;

  constructor(
    protected orderAmendService: AdnocOrderAmendService,
    protected globalMessageService: AdnocGlobalMessageService,
    protected routing: RoutingService
  ) {
    this.form$ = this.orderAmendService
      .getForm()
      .pipe(tap((form) => (this.orderCode = form.value.orderCode)));

    this.entries$ = this.orderAmendService.getAmendedEntries();
  }

  submit(form: UntypedFormGroup) {
    if (form.valid) {
      const entries = form.get('entries')?.value || {};
      const inputs = Object.keys(entries)
        .filter((entryNumber) => entries[entryNumber] > 0)
        .map((entryNumber) => {
          const cancelReasonValue =
            form.get(`cancelReason.${entryNumber}`)?.value || '';
          const [code, name] = cancelReasonValue.split('|');

          return {
            orderEntryNumber: Number(entryNumber),
            quantity: entries[entryNumber],
            cancelReason: {
              code,
              name,
            },
          };
        });

      const payload = {
        cancellationRequestEntryInputs: inputs,
      };

      this.orderAmendService
        .submitCancellationRequest(payload, this.orderCode)
        .subscribe({
          next: () => {
            this.globalMessageService.add(
              {
                key: 'orderDetails.cancellationAndReturn.cancelSuccess',
                params: { orderCode: this.orderCode },
              },
              GlobalMessageType.MSG_TYPE_CONFIRMATION
            );
            this.routing.go({
              cxRoute: 'orders',
            });
          },
          error: () => {},
        });
    } else {
      form.markAllAsTouched();
    }
  }
}
