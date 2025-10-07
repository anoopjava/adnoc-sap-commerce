import {
  ChangeDetectionStrategy,
  Component,
  ViewEncapsulation,
} from '@angular/core';
import {
  ProductGridItemComponent,
  ProductListItemContext,
  ProductListItemContextSource,
} from '@spartacus/storefront';

@Component({
    selector: 'cx-product-grid-item',
    templateUrl: './adnoc-product-grid-item.component.html',
    styleUrl: './adnoc-product-grid-item.component.scss',
    providers: [
        ProductListItemContextSource,
        {
            provide: ProductListItemContext,
            useExisting: ProductListItemContextSource,
        },
    ],
    changeDetection: ChangeDetectionStrategy.OnPush,
    encapsulation: ViewEncapsulation.None,
    host: { class: 'adnoc-product-grid-item' },
    standalone: false
})
export class AdnocProductGridItemComponent extends ProductGridItemComponent {}
