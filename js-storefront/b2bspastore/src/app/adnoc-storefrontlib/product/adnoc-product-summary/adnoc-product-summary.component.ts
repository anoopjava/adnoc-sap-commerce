import {
  ChangeDetectionStrategy,
  Component,
  inject,
  Input,
  OnInit,
  ViewEncapsulation,
} from '@angular/core';
import {
  CmsAddToCartComponent,
  FeatureConfigService,
  ProductScope,
  useFeatureStyles,
} from '@spartacus/core';
import {
  CmsComponentData,
  CurrentProductService,
  ProductDetailOutlets,
  ProductSummaryComponent,
} from '@spartacus/storefront';
import { Product } from '../../../core/src/model/product.model';
import { Observable } from 'rxjs';

@Component({
  selector: 'cx-product-summary',
  templateUrl: './adnoc-product-summary.component.html',
  styleUrl: './adnoc-product-summary.component.scss',
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'adnoc-product-summary' },
  standalone: false,
})
export class AdnocProductSummaryComponent implements OnInit {
  maxQuantity!: number;

  hasStock: boolean = false;
  inventoryThreshold: boolean = false;

  private featureConfig = inject(FeatureConfigService);

  outlets = ProductDetailOutlets;

  product$!: Observable<Product | null>;

  constructor(
    protected currentProductService: CurrentProductService,
    protected component: CmsComponentData<CmsAddToCartComponent>
  ) {}

  ngOnInit(): void {
    this.product$ = this.getProduct();
  }

  protected getProduct(): Observable<Product | null> {
    const productScopes = [ProductScope.DETAILS, ProductScope.PRICE];
    if (this.featureConfig.isEnabled('showPromotionsInPDP')) {
      productScopes.push(ProductScope.PROMOTIONS);
    }
    return this.currentProductService.getProduct(productScopes);
  }
}
