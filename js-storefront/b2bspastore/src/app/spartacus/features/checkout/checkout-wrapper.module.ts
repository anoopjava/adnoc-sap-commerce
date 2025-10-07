import { NgModule } from '@angular/core';
//import { CheckoutModule } from "@spartacus/checkout/base";
import { DigitalPaymentsModule } from "@spartacus/digital-payments";
import { CheckoutB2BModule } from '../../../adnoc-storefrontlib/checkout/b2b/checkout-b2b.module';
import { CheckoutModule } from '../../../adnoc-storefrontlib/checkout/base/checkout.module';

@NgModule({
  declarations: [],
  imports: [
    CheckoutModule,
    CheckoutB2BModule,
    DigitalPaymentsModule
  ]
})
export class CheckoutWrapperModule { }
