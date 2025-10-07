/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  Component,
  ElementRef,
  OnDestroy,
  ViewChild,
  ViewContainerRef,
} from '@angular/core';
import { Cart } from '@spartacus/cart/base/root';
import { SavedCartFormType } from '@spartacus/cart/saved-cart/root';
import { LaunchDialogService, LAUNCH_CALLER } from '@spartacus/storefront';
import { Observable, Subscription } from 'rxjs';
import { take } from 'rxjs/operators';
import { AdnocSavedCartDetailsService } from '../adnoc-saved-cart-details.service';

@Component({
  selector: 'cx-saved-cart-details-action',
  templateUrl: './adnoc-saved-cart-details-action.component.html',
  host: { class: 'adnoc-saved-cart-details-action' },
  standalone: false,
})
export class AdnocSavedCartDetailsActionComponent implements OnDestroy {
  private subscription = new Subscription();
  savedCartFormType = SavedCartFormType;

  @ViewChild('element') element!: ElementRef;
  savedCart$: Observable<Cart | undefined>;

  constructor(
    protected savedCartDetailsService: AdnocSavedCartDetailsService,
    protected vcr: ViewContainerRef,
    protected launchDialogService: LaunchDialogService
  ) {
    this.savedCart$ = this.savedCartDetailsService.getCartDetails();
  }

  openDialog(cart: Cart, type: SavedCartFormType): void {
    const dialog = this.launchDialogService.openDialog(
      LAUNCH_CALLER.SAVED_CART,
      this.element,
      this.vcr,
      { cart, layoutOption: type }
    );

    if (dialog) {
      this.subscription.add(dialog.pipe(take(1)).subscribe());
    }
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }
}
