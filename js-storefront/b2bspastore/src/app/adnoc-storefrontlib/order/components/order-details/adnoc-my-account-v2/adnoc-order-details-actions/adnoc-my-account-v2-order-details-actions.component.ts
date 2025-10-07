import { Component } from '@angular/core';
import { MyAccountV2OrderDetailsActionsComponent } from '@spartacus/order/components';

@Component({
    selector: 'cx-my-account-v2-order-details-actions',
    templateUrl: './adnoc-my-account-v2-order-details-actions.component.html',
    host: { class: 'adnoc-my-account-v2-order-details-actions' },
    standalone: false
})
export class AdnocMyAccountV2OrderDetailsActionsComponent extends MyAccountV2OrderDetailsActionsComponent {}
