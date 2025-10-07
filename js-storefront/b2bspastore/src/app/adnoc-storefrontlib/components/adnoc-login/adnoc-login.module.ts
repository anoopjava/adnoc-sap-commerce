import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  CmsConfig,
  ConfigModule,
  I18nModule,
  UrlModule,
} from '@spartacus/core';
import { PageSlotModule } from '@spartacus/storefront';
import { AdnocLoginComponent } from './adnoc-login.component';


@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    UrlModule,
    PageSlotModule,
    I18nModule,
    ConfigModule.withConfig({
      cmsComponents: { 
        LoginComponent: {
          component: AdnocLoginComponent,
        },
      }
    }as CmsConfig),
  ],
  declarations: [AdnocLoginComponent],
})
export class AdnocLoginModule { }
