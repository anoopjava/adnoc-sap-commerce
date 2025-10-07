import { ChangeDetectionStrategy, Component } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { OrderEntry } from '@spartacus/cart/base/root';
import { GlobalMessageType } from '@spartacus/core';
import { Observable, tap } from 'rxjs';
import { AdnocOrderAmendService } from '../../adnoc-amend-order.service';

@Component({
  selector: 'cx-cancel-order',
  templateUrl: './adnoc-cancel-order.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'adnoc-cancel-order' },
  standalone: false,
})
export class AdnocCancelOrderComponent {
  orderCode!: string;
  globalMessageType = GlobalMessageType;
  form$: Observable<UntypedFormGroup>;
  entries$: Observable<OrderEntry[]>;

  constructor(protected orderAmendService: AdnocOrderAmendService) {
    this.form$ = this.orderAmendService
      .getForm()
      .pipe(tap((form) => (this.orderCode = form.value.orderCode)));

    this.entries$ = this.orderAmendService.getEntries();
  }
}
