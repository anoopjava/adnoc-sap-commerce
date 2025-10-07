import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {
  CmsConfig,
  ConfigModule,
  I18nModule,
  provideDefaultConfig,
  UrlModule,
} from '@spartacus/core';
import { AdnocMiniCartComponent } from './adnoc-mini-cart.component';

@NgModule({
  declarations: [AdnocMiniCartComponent],
  imports: [CommonModule, RouterModule, I18nModule, UrlModule],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        MiniCartComponent: {
          component: AdnocMiniCartComponent,
        },
      },
    }),
  ],
  exports: [AdnocMiniCartComponent],
})
export class AdnocMiniCartModule {}
