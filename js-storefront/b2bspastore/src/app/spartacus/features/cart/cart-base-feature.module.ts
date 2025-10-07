/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import {
  cartBaseTranslationChunksConfig,
  cartBaseTranslations,
} from '@spartacus/cart/base/assets';
import {
  ADD_TO_CART_FEATURE,
  CartBaseRootModule,
  CART_BASE_FEATURE,
  MINI_CART_FEATURE,
} from '@spartacus/cart/base/root';
import { CmsConfig, I18nConfig, provideConfig } from '@spartacus/core';

@NgModule({
  declarations: [],
  imports: [CartBaseRootModule],
  providers: [
    provideConfig(<CmsConfig>{
      featureModules: {
        [CART_BASE_FEATURE]: {
          module: () =>
            import('./cart-base-wrapper.module').then(
              (m) => m.AdnocCartBaseWrapperModule
            ),
        },
      },
    }),
    provideConfig(<CmsConfig>{
      featureModules: {
        [MINI_CART_FEATURE]: {
          module: () =>
            import('../../../adnoc-storefrontlib/cart/base/components/adnoc-mini-cart/adnoc-mini-cart.module').then(
              (m) => m.AdnocMiniCartModule
            ),
        },
      },
    }),
    provideConfig(<CmsConfig>{
      featureModules: {
        [ADD_TO_CART_FEATURE]: {
          module: () =>
            import(
              '../../../adnoc-storefrontlib/cart/base/components/adnoc-add-to-cart/adnoc-add-to-cart.module'
            ).then((m) => m.AdnocAddToCartModule),
        },
      },
    }),
    provideConfig(<I18nConfig>{
      i18n: {
        resources: cartBaseTranslations,
        chunks: cartBaseTranslationChunksConfig,
      },
    }),
  ],
})
export class CartBaseFeatureModule {}
