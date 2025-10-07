import {
    ChangeDetectionStrategy,
    Component,
    ViewEncapsulation,
  } from '@angular/core';
  import {
    ProductListItemComponent,
    ProductListItemContext,
    ProductListItemContextSource,
  } from '@spartacus/storefront';
  
  @Component({
    selector: 'cx-product-list-item',
    templateUrl: './adnoc-product-list-item.component.html',
    styleUrl: './adnoc-product-list-item.component.scss',
    providers: [
        ProductListItemContextSource,
        {
            provide: ProductListItemContext,
            useExisting: ProductListItemContextSource,
        },
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    encapsulation: ViewEncapsulation.None,
    host: { class: 'adnoc-product-list-item' },
    standalone: false
})
  export class AdnocProductListItemComponent extends ProductListItemComponent {}