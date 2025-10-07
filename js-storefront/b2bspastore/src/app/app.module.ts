import {
  provideHttpClient,
  withFetch,
  withInterceptorsFromDi,
} from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { AppRoutingModule, SpinnerModule } from '@spartacus/storefront';
import { AppComponent } from './app.component';
import { SpartacusModule } from './spartacus/spartacus.module';
import { AdnocComponentModule } from './adnoc-storefrontlib/components/adnoc-components.module';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import {
  configReducer,
  entryReducer,
} from './adnoc-storefrontlib/cart/base/core/adnoc-store/adnoc-cart-state/adnoc-cart.reducer';
import { creditLimitReducer } from './adnoc-storefrontlib/checkout/b2b/b2b-store/reducer/credit-limit.reducer';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    StoreModule.forRoot({
      cartEntries: entryReducer,
      config: configReducer,
      creditLimit: creditLimitReducer,
    }),
    AppRoutingModule,
    SpinnerModule,
    EffectsModule.forRoot([]),
    SpartacusModule,
    AdnocComponentModule,
  ],
  providers: [
    provideHttpClient(withFetch(), withInterceptorsFromDi()),
    provideAnimationsAsync(),
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
