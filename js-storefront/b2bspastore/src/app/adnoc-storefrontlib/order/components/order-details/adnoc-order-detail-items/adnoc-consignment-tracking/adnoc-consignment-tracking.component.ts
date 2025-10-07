import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ConsignmentTrackingComponent } from '@spartacus/order/components';

@Component({
    selector: 'cx-consignment-tracking',
    templateUrl: './adnoc-consignment-tracking.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'adnoc-consignment-tracking' },
    standalone: false
})
export class AdnocConsignmentTrackingComponent extends ConsignmentTrackingComponent {}
