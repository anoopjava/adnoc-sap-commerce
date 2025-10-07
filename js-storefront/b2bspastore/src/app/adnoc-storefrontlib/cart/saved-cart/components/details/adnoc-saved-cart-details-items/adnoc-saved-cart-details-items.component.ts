/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  OnDestroy,
  OnInit,
  ViewEncapsulation,
} from '@angular/core';
import {
  Cart,
  CartType,
  DeleteCartSuccessEvent as DeleteSavedCartSuccessEvent,
  PromotionLocation,
} from '@spartacus/cart/base/root';
import { SavedCartDetailsService } from '@spartacus/cart/saved-cart/components';
import { SavedCartFacade } from '@spartacus/cart/saved-cart/root';
import {
  EventService,
  GlobalMessageType,
  RoutingService,
  TranslationService,
} from '@spartacus/core';
import { Observable, Subscription } from 'rxjs';
import { map, switchMap, take, tap } from 'rxjs/operators';
import { AdnocCartOutlets } from '../../../../base/root/models/cart-outlets.model';
import { AdnocGlobalMessageService } from '../../../../../../core/global-message/facade/adnoc-global-message.service';

@Component({
  selector: 'cx-saved-cart-details-items',
  templateUrl: './adnoc-saved-cart-details-items.component.html',
  styleUrl: './adnoc-saved-cart-details-items.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  host: { class: 'adnoc-saved-cart-details-items' },
  standalone: false,
})
export class AdnocSavedCartDetailsItemsComponent implements OnInit, OnDestroy {
  private subscription = new Subscription();

  readonly CartOutlets = AdnocCartOutlets;
  readonly CartType = CartType;
  CartLocation = PromotionLocation;

  buyItAgainTranslation$!: Observable<string>;

  cartLoaded$: Observable<boolean>;

  savedCart$: Observable<Cart | undefined>;

  constructor(
    protected savedCartDetailsService: SavedCartDetailsService,
    protected savedCartService: SavedCartFacade,
    protected eventSercvice: EventService,
    protected globalMessageService: AdnocGlobalMessageService,
    protected routingService: RoutingService,
    protected translation: TranslationService
  ) {
    this.cartLoaded$ = this.savedCartDetailsService
      .getSavedCartId()
      .pipe(switchMap((cartId) => this.savedCartService.isStable(cartId)));

    this.savedCart$ = this.savedCartDetailsService.getCartDetails().pipe(
      tap((cart) => {
        if ((cart?.entries ?? []).length <= 0 && !!cart?.code) {
          this.savedCartService.deleteSavedCart(cart.code);
        }
      })
    );
  }

  ngOnInit(): void {
    this.subscription.add(
      this.eventSercvice
        .get(DeleteSavedCartSuccessEvent)
        .pipe(
          take(1),
          map(() => true)
        )
        .subscribe((success) => this.onDeleteComplete(success))
    );

    this.buyItAgainTranslation$ = this.translation.translate(
      'addToCart.addToActiveCart'
    );
  }

  onDeleteComplete(success: boolean): void {
    if (success) {
      this.routingService.go({ cxRoute: 'savedCarts' });
      this.globalMessageService.add(
        {
          key: 'savedCartDialog.deleteCartSuccess',
        },
        GlobalMessageType.MSG_TYPE_CONFIRMATION
      );
    }
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }
}
