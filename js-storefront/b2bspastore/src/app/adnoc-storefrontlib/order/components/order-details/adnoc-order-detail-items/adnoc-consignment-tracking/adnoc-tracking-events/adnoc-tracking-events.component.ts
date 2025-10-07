import { ChangeDetectionStrategy, Component } from '@angular/core';
import { TrackingEventsComponent } from '@spartacus/order/components';

@Component({
    selector: 'cx-tracking-events',
    templateUrl: './adnoc-tracking-events.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'adnoc-tracking-events' },
    standalone: false
})
export class AdnocTrackingEventsComponent extends TrackingEventsComponent {}
