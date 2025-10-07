import { ChangeDetectionStrategy, Component } from '@angular/core';
import { OrderDetailReorderComponent } from '@spartacus/order/components';

@Component({
    selector: 'cx-order-details-reorder',
    templateUrl: './adnoc-order-detail-reorder.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'adnoc-order-details-reorder' },
    standalone: false
})
export class AdnocOrderDetailReorderComponent extends OrderDetailReorderComponent {}
