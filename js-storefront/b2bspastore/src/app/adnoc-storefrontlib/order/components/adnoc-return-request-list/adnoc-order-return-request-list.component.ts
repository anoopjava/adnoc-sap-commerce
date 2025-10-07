/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { OrderReturnRequestListComponent } from '@spartacus/order/components';

@Component({
    selector: 'cx-order-return-request-list',
    templateUrl: './adnoc-order-return-request-list.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'adnoc-order-return-request-list' },
    standalone: false
})
export class AdnocOrderReturnRequestListComponent extends OrderReturnRequestListComponent {}
