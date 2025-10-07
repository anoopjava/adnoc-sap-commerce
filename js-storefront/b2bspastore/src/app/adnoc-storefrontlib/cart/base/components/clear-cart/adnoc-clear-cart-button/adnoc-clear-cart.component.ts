/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  OnDestroy,
  ViewChild,
  ViewContainerRef,
} from '@angular/core';
import { LaunchDialogService, LAUNCH_CALLER } from '@spartacus/storefront';
import { Cart } from '@spartacus/cart/base/root';
import { Observable, Subscription } from 'rxjs';
import { take } from 'rxjs/operators';
import { AdnocActiveCartFacade } from '../../../root/facade/adnoc-active-cart.facade';

@Component({
    selector: 'adnoc-clear-cart',
    templateUrl: './adnoc-clear-cart.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    standalone: false
})
export class AdnocClearCartComponent implements OnDestroy {
  cart$: Observable<Cart>;

  protected subscription = new Subscription();

  @ViewChild('element') element!: ElementRef;

  constructor(
    protected activeCartFacade: AdnocActiveCartFacade,
    protected vcr: ViewContainerRef,
    protected launchDialogService: LaunchDialogService
  ) {
    this.cart$ = this.activeCartFacade.getActive();
  }

  openDialog(event: Event): void {
    const dialog = this.launchDialogService.openDialog(
      LAUNCH_CALLER.CLEAR_CART,
      this.element,
      this.vcr
    );
    if (dialog) {
      this.subscription.add(dialog.pipe(take(1)).subscribe());
    }
    event.stopPropagation();
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
