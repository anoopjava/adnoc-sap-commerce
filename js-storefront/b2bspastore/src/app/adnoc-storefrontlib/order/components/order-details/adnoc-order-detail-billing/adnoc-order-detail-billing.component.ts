import { ChangeDetectionStrategy, Component } from '@angular/core';
import { OrderDetailBillingComponent } from '@spartacus/order/components';

@Component({
    selector: 'cx-order-detail-billing',
    templateUrl: './adnoc-order-detail-billing.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'adnoc-order-detail-billing' },
    standalone: false
})
export class AdnocOrderDetailBillingComponent extends OrderDetailBillingComponent {}