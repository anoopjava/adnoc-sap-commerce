/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ComponentRef,
  HostListener,
  inject,
  Input,
  Optional,
  ViewEncapsulation,
} from '@angular/core';
import { UntypedFormGroup, UntypedFormControl } from '@angular/forms';
import {
  CartItemComponentOptions,
  CartOutlets,
  CartUiEventAddToCart,
} from '@spartacus/cart/base/root';
import {
  Product,
  FeatureConfigService,
  CmsAddToCartComponent,
  EventService,
  isNotNullable,
  GlobalMessageType,
} from '@spartacus/core';
import {
  ICON_TYPE,
  CurrentProductService,
  CmsComponentData,
  ProductListItemContext,
} from '@spartacus/storefront';
import { Observable, map, Subscription, filter, take } from 'rxjs';
import { AdnocActiveCartFacade } from '../../root/facade/adnoc-active-cart.facade';
import { Actions, ofType } from '@ngrx/effects';
import { CartActions } from '@spartacus/cart/base/core';
import { adnocProduct } from '../../../../../core/model/adnoc-cart.model';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';
@Component({
  selector: 'cx-add-to-cart',
  templateUrl: './adnoc-add-to-cart.component.html',
  styleUrl: './adnoc-add-to-cart.component.scss',
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'adnoc-add-to-cart' },
  standalone: false,
})
export class AdnocAddToCartComponent {
  @Input() optiones: CartItemComponentOptions | undefined;
  @Input() productCode!: string;
  @Input() showQuantity = true;
  @Input() options!: CartItemComponentOptions;
  @Input() pickupStore: string | undefined;
  /**
   * As long as we do not support #5026, we require product input, as we need
   *  a reference to the product model to fetch the stock data.
   */
  @Input() product!: adnocProduct;
  minQuantity: number = 1;
  maxQuantity!: number;

  hasStock: boolean = false;
  inventoryThreshold: boolean = false;

  showInventory$: Observable<boolean | undefined> | undefined;

  quantity = 1;

  subscription!: Subscription;

  addToCartForm = new UntypedFormGroup({
    quantity: new UntypedFormControl(1, { updateOn: 'blur' }),
  });

  readonly CartOutlets = CartOutlets;

  pickupOptionCompRef: any;

  iconTypes = ICON_TYPE;

  @Optional() featureConfigService = inject(FeatureConfigService, {
    optional: true,
  });

  isAddToCartPopup = true;
  /**
   * We disable the dialog launch on quantity input,
   * as it causes an unexpected change of context.
   * The expectation is only for the quantity to get updated in the Qty field.
   */
  @HostListener('document:keydown', ['$event'])
  handleKeyboardEvent(event: KeyboardEvent) {
    // TODO: (CXSPA-6034) Remove Feature flag next major release
    if (!this.featureConfigService?.isEnabled('a11yQuantityOrderTabbing')) {
      return;
    }
    const eventTarget = event.target as HTMLElement;
    const isQuantityInput =
      eventTarget.ariaLabel === 'Quantity' && eventTarget.tagName === 'INPUT';
    if (event.key === 'Enter' && isQuantityInput) {
      event.preventDefault();
    }
  }

  constructor(
    protected currentProductService: CurrentProductService,
    protected cd: ChangeDetectorRef,
    protected activeCartService: AdnocActiveCartFacade,
    protected component: CmsComponentData<CmsAddToCartComponent>,
    protected eventService: EventService,
    private actions$: Actions,
    protected globalMessageService: AdnocGlobalMessageService,
    @Optional() protected productListItemContext?: ProductListItemContext
  ) {}

  ngOnInit() {
    this.showInventory$ = this.component?.data$.pipe(
      map((data) => data.inventoryDisplay)
    );
    if (this.product) {
      this.productCode = this.product.code ?? '';
      this.setStockInfo(this.product);
      this.cd.markForCheck();
    } else if (this.productCode) {
      // force hasStock and quantity for the time being, as we do not have more info:
      this.quantity = 1;
      this.hasStock = true;
      this.cd.markForCheck();
    } else {
      this.subscription = (
        this.productListItemContext
          ? this.productListItemContext.product$
          : this.currentProductService.getProduct()
      )
        .pipe(filter(isNotNullable))
        .subscribe((product) => {
          this.productCode = product.code ?? '';
          this.setStockInfo(product as any);
          this.cd.markForCheck();
        });
    }
  }

  protected setStockInfo(product: adnocProduct): void {
    this.quantity = product?.minOrderQuantity;

    this.addToCartForm.controls['quantity'].setValue(this.quantity);

    this.hasStock = Boolean(product.stock?.stockLevelStatus !== 'outOfStock');

    this.inventoryThreshold = product.stock?.isValueRounded ?? false;

    if (this.hasStock && product.stock?.stockLevel) {
      this.maxQuantity =
        typeof product.maxOrderQuantity === 'number'
          ? Math.min(product.maxOrderQuantity, product.stock?.stockLevel)
          : product.stock?.stockLevel;
      this.minQuantity = product?.minOrderQuantity
        ? product?.minOrderQuantity
        : this.minQuantity;
    }

    if (
      product.stock?.stockLevel &&
      this.minQuantity > product.stock?.stockLevel
    ) {
      this.hasStock = false;
    }

    if (this.productListItemContext) {
      this.showQuantity = false;
    }
  }

  /**
   * In specific scenarios, we need to omit displaying the stock level or append a plus to the value.
   * When backoffice forces a product to be in stock, omit showing the stock level.
   * When product stock level is limited by a threshold value, append '+' at the end.
   * When out of stock, display no numerical value.
   */
  getInventory(): string {
    if (this.hasStock) {
      const quantityDisplay = this.maxQuantity
        ? this.maxQuantity.toString()
        : '';
      return this.inventoryThreshold ? quantityDisplay + '+' : quantityDisplay;
    } else {
      return '';
    }
  }

  updateCount(value: number): void {
    this.quantity = value;
  }

  addToCart() {
    const quantity = this.addToCartForm.get('quantity')?.value;
    if (!this.productCode || quantity <= 0) {
      return;
    }

    if (this.pickupOptionCompRef instanceof ComponentRef) {
      this.pickupOptionCompRef.instance.intendedPickupLocation$
        .pipe(take(1))
        .subscribe((intendedPickupLocation: any) => {
          this.pickupStore =
            intendedPickupLocation?.pickupOption === 'pickup'
              ? intendedPickupLocation.name
              : undefined;
        });
    }

    this.activeCartService
      .getEntries()
      .pipe(take(1))
      .subscribe((cartEntries) => {
        this.activeCartService.addEntry(
          this.productCode,
          quantity,
          this.pickupStore
        );

        // Subscribe to the CartAddEntryFail action
        this.subscription = this.actions$
          .pipe(ofType(CartActions.CART_ADD_ENTRY_FAIL))
          .subscribe((action: CartActions.CartAddEntryFail) => {
            const errorMessage = action.payload.error.details[0];
            const { message, type } = errorMessage;
            if (!message) {
              this.isAddToCartPopup = false;
            }
            this.globalMessageService.add(
              { raw: message },
              GlobalMessageType.MSG_TYPE_ERROR
            );
          });

        // A CartUiEventAddToCart is dispatched.  This event is intended for the UI
        // responsible to provide feedback about what was added to the cart, like
        // the added to cart dialog.
        //
        // Because we call activeCartService.getEntries() before, we can be sure the
        // cart library is loaded already and that the event listener exists.
        if (this.isAddToCartPopup) {
          this.eventService.dispatch(
            this.createCartUiEventAddToCart(
              this.productCode,
              quantity,
              cartEntries.length,
              this.pickupStore
            )
          );
        }
      });
  }

  protected createCartUiEventAddToCart(
    productCode: string,
    quantity: number,
    numberOfEntriesBeforeAdd: number,
    storeName?: string
  ) {
    const newEvent = new CartUiEventAddToCart();
    newEvent.productCode = productCode;
    newEvent.quantity = quantity;
    newEvent.numberOfEntriesBeforeAdd = numberOfEntriesBeforeAdd;
    newEvent.pickupStoreName = storeName;
    return newEvent;
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }
}
