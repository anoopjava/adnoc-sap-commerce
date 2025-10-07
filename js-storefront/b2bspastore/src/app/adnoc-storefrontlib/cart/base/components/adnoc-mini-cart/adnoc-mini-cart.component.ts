import { ChangeDetectionStrategy, Component } from '@angular/core';
import { AdnocActiveCartFacade } from '../../root/facade/adnoc-active-cart.facade';
import { Observable } from 'rxjs';
import { ICON_TYPE } from '@spartacus/storefront';
import { MiniCartComponentService } from './adnoc-mini-cart-component.service';

@Component({
  selector: 'adnoc-mini-cart',
  templateUrl: './adnoc-mini-cart.component.html',
  styleUrl: './adnoc-mini-cart.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class AdnocMiniCartComponent {
  iconTypes = ICON_TYPE;

  quantity$!: Observable<number>;

  total$!: Observable<string>;

  entryCount$!: Observable<number>;

  constructor(
    protected miniCartComponentService: MiniCartComponentService,
    protected activeCartService: AdnocActiveCartFacade
  ) {}
  
  ngOnInit() {
    this.total$ = this.miniCartComponentService.getTotalPrice();

    this.quantity$ = this.miniCartComponentService.getQuantity();

    this.entryCount$ = this.miniCartComponentService.getEntriesLength();
  }
}
