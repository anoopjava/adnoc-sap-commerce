/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { OrderConfirmationThankYouMessageComponent } from '@spartacus/order/components';

@Component({
    selector: 'adnoc-order-confirmation-thank-you-message',
    templateUrl: './adnoc-order-confirmation-thank-you-message.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class AdnocOrderConfirmationThankYouMessageComponent extends OrderConfirmationThankYouMessageComponent {}
