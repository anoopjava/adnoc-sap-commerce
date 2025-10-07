/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { translationChunksConfig } from '@spartacus/assets';
import {
  FeaturesConfig,
  I18nConfig,
  OccConfig,
  provideConfig,
  SiteContextConfig,
} from '@spartacus/core';
import {
  defaultCmsContentProviders,
  layoutConfig,
  mediaConfig,
} from '@spartacus/storefront';
import { adnocLayoutConfig } from '../adnoc-storefrontlib/config/layout/adnoc-layout-config';
import { AdnocRoutingModule } from '../adnoc-storefrontlib/routing/adnoc-routing.module';
import { adnocDefaultB2bOccConfig } from '../core/b2b/config/default-b2b-occ-config';
import { defaultB2BCheckoutConfig } from '../adnoc-storefrontlib/checkout/b2b/root/config/default-b2b-checkout-config';

@NgModule({
  declarations: [],
  imports: [AdnocRoutingModule],
  providers: [
    provideConfig(layoutConfig),
    provideConfig(adnocLayoutConfig),
    provideConfig(mediaConfig),
    ...defaultCmsContentProviders,
    // Required for B2B portal development
    //  provideConfig(<OccConfig>{
    //    backend: {
    //      occ: {
    //        baseUrl: "https://localhost:9002",
    //      },
    //    },
    //  }),
    provideConfig(<SiteContextConfig>{
      context: {
        baseSite: ['adnoc'],
        language: ['en'],
        currency: ['AED', 'USD'],
        urlParameters: ['baseSite', 'language', 'currency'],
      },
    }),
    provideConfig(<I18nConfig>{
      i18n: {
        backend: {
          loadPath: 'translations/{{lng}}/{{ns}}.json',
        },
        chunks: translationChunksConfig,
        fallbackLang: 'en',
      },
    }),
    provideConfig(<FeaturesConfig>{
      features: {
        level: '2211.41.0',
      },
    }),
    provideConfig(defaultB2BCheckoutConfig),
    provideConfig(adnocDefaultB2bOccConfig),
    provideConfig({
      routing: {
        routes: {
          contact: {
            paths: ['contact'],
            protected: false, // no auth required
            authFlow: false,
          },
        },
      },
    }),
  ],
})
export class SpartacusConfigurationModule {}
