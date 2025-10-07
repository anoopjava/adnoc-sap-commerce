import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  CmsConfig,
  PageMetaModule,
  I18nModule,
  FeaturesConfigModule,
  ConfigModule,
} from '@spartacus/core';
import { AdnocBreadcrumbComponent } from './adnoc-breadcrumb.component';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    PageMetaModule,
    I18nModule,
    FeaturesConfigModule,
    ConfigModule.withConfig({
      cmsComponents: {
        BreadcrumbComponent: {
          component: AdnocBreadcrumbComponent,
        },
      },
    } as CmsConfig),
  ],
  providers: [],
  declarations: [AdnocBreadcrumbComponent],
  exports: [AdnocBreadcrumbComponent],
})
export class AdnocBreadcrumbModule {}
