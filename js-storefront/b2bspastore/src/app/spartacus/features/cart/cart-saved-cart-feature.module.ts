import { NgModule } from '@angular/core';
import { savedCartTranslationChunksConfig, savedCartTranslations } from "@spartacus/cart/saved-cart/assets";
import { CART_SAVED_CART_FEATURE, SavedCartRootModule } from "@spartacus/cart/saved-cart/root";
import { CmsConfig, I18nConfig, provideConfig } from "@spartacus/core";

@NgModule({
  declarations: [],
  imports: [
    SavedCartRootModule
  ],
  providers: [provideConfig(<CmsConfig>{
    featureModules: {
      [CART_SAVED_CART_FEATURE]: {
        module: () =>
          import('../../../adnoc-storefrontlib/cart/saved-cart/saved-cart.module').then((m) => m.SavedCartModule),
      },
    }
  }),
  provideConfig(<I18nConfig>{
    i18n: {
      resources: savedCartTranslations,
      chunks: savedCartTranslationChunksConfig,
    },
  })
  ]
})
export class CartSavedCartFeatureModule { }
