import { Component } from '@angular/core';
import { MyAccountV2ConsignmentTrackingComponent } from '@spartacus/order/components';
import { Consignment, Order } from '@spartacus/order/root';
type ConsignmentOutletContextData = { item: Consignment; order?: Order };
@Component({
    selector: 'cx-my-account-v2-consignment-tracking',
    templateUrl: './adnoc-my-account-v2-consignment-tracking.component.html',
    standalone: false
})
export class AdnocMyAccountV2ConsignmentTrackingComponent extends MyAccountV2ConsignmentTrackingComponent {}
