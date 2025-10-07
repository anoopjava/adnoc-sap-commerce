import { NgModule } from '@angular/core';
import { CmsConfig, I18nConfig, provideConfig } from "@spartacus/core";
import { quoteTranslationChunksConfig, quoteTranslations } from "../../../adnoc-storefrontlib/quote/assets/translations/translations";
import { QuoteRootModule, QUOTE_CART_GUARD_FEATURE, QUOTE_FEATURE, QUOTE_REQUEST_FEATURE } from "@spartacus/quote/root";

@NgModule({
  declarations: [],
  imports: [
    QuoteRootModule
  ],
  providers: [provideConfig(<CmsConfig>{
    featureModules: {
      [QUOTE_FEATURE]: {
        module: () =>
          import('../../../adnoc-storefrontlib/quote/quote.module').then((m) => m.AdnocQuoteModule),
      },
    }
  }),
  provideConfig(<CmsConfig>{
    featureModules: {
      [QUOTE_CART_GUARD_FEATURE]: {
        module: () =>
          import('../../../adnoc-storefrontlib/quote/components/cart-guard/quote-cart-guard.component.module').then((m) => m.QuoteCartGuardComponentModule),
      },
    }
  }),
  provideConfig(<CmsConfig>{
    featureModules: {
      [QUOTE_REQUEST_FEATURE]: {
        module: () =>
          import('../../../adnoc-storefrontlib/quote/components/adnoc-request-button/adnoc-quote-request-button.module').then((m) => m.AdnocQuoteRequestButtonModule),
      },
    }
  }),
  provideConfig(<I18nConfig>{
    i18n: {
      resources: quoteTranslations,
      chunks: quoteTranslationChunksConfig,
    },
  })
  ]
})
export class QuoteFeatureModule { }
