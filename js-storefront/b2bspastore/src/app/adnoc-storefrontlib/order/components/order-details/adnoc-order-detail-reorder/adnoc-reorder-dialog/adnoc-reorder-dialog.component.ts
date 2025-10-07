import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ReorderDialogComponent } from '@spartacus/order/components';

@Component({
    selector: 'cx-reorder-dialog',
    templateUrl: './adnoc-reorder-dialog.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'adnoc-reorder-dialog' },
    standalone: false
})
export class AdnocReorderDialogComponent extends ReorderDialogComponent {}
