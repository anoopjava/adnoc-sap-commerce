/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Cart } from '@spartacus/cart/base/root';
import { Observable } from 'rxjs';
import { AdnocActiveCartFacade } from '../../../../../cart/base/root/facade/adnoc-active-cart.facade';

@Component({
    selector: 'cx-checkout-review-overview',
    templateUrl: './checkout-review-overview.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class CheckoutReviewOverviewComponent {
  constructor(protected activeCartFacade: AdnocActiveCartFacade) {}

  get cart$(): Observable<Cart> {
    return this.activeCartFacade.getActive();
  }
}
