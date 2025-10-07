/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Component, Input, ViewEncapsulation } from '@angular/core';
import {
  AbstractOrderType,
  PromotionLocation,
} from '@spartacus/cart/base/root';
import { Consignment, Order, OrderOutlets } from '@spartacus/order/root';
import { AdnocCartOutlets } from '../../../../../cart/base/root/models/cart-outlets.model';

@Component({
    selector: 'cx-order-consigned-entries',
    templateUrl: './adnoc-order-consigned-entries.component.html',
    styleUrls: ['./adnoc-order-consigned-entries.component.scss'],
    encapsulation: ViewEncapsulation.None,
    host: { class: 'adnoc-order-consigned-entries' },
    standalone: false
})
export class AdnocOrderConsignedEntriesComponent {
  @Input() consignments!: Consignment[];
  @Input() order!: Order;
  @Input() enableAddToCart: boolean | undefined;
  @Input() buyItAgainTranslation!: string;

  promotionLocation: PromotionLocation = PromotionLocation.Order;

  readonly OrderOutlets = OrderOutlets;
  readonly CartOutlets = AdnocCartOutlets;
  readonly abstractOrderType = AbstractOrderType;
}
