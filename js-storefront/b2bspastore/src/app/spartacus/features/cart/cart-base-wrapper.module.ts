import { NgModule } from '@angular/core';
import { AdnocCartBaseModule } from '../../../adnoc-storefrontlib/cart/base/adnoc-cart-base.module';
import { EstimatedDeliveryDateModule } from "@spartacus/estimated-delivery-date";

@NgModule({
  imports: [
    AdnocCartBaseModule,
    EstimatedDeliveryDateModule
  ]
})
export class AdnocCartBaseWrapperModule { }
