import { Component } from '@angular/core';
import { OrderDetailActionsComponent } from '@spartacus/order/components';

@Component({
    selector: 'cx-order-details-actions',
    templateUrl: './adnoc-order-detail-actions.component.html',
    host: { class: 'adnoc-order-details-actions' },
    standalone: false
})
export class AdnocOrderDetailActionsComponent extends OrderDetailActionsComponent {}
