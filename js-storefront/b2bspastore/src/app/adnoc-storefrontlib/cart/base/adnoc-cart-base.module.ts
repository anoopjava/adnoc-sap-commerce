/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { AdnocCartBaseComponentsModule } from './components/adnoc-cart-base-components.module';
import { CartBaseOccModule } from '@spartacus/cart/base/occ';
import { CartBaseCoreModule } from '@spartacus/cart/base/core';
import { AdnocActiveCartFacade } from './root/facade/adnoc-active-cart.facade';
import { AdnocActiveCartService } from './core/facade/adnoc-active-cart.service';
import { MultiCartService } from './core/facade/adnoc-multi-cart.service';
import { MultiCartFacade } from './root/facade/adnoc-multi-cart.facade';


@NgModule({
  providers:[
    {
      provide: AdnocActiveCartFacade,
      useExisting: AdnocActiveCartService,
    },
    {
      provide: MultiCartFacade,
      useExisting: MultiCartService,
    },
  ],
  imports: [CartBaseCoreModule, CartBaseOccModule, AdnocCartBaseComponentsModule],
})
export class AdnocCartBaseModule {}
