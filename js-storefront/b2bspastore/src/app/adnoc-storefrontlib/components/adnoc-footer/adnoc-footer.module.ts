import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { CmsConfig, I18nModule, provideDefaultConfig } from '@spartacus/core';
import { GenericLinkModule, NavigationModule } from '@spartacus/storefront';
import { AdnocFooterComponent } from './adnoc-footer.component';


@NgModule({
  declarations: [AdnocFooterComponent],
  imports: [
    CommonModule,
    RouterModule,
    NavigationModule,
    GenericLinkModule,
    I18nModule,
  ],
  providers: [
    provideDefaultConfig(<CmsConfig>{
      cmsComponents: {
        FooterNavigationComponent: {
          component: () =>
            import('./adnoc-footer.component').then((m) => m.AdnocFooterComponent),
        },
      },
    }),
  ],
  exports: [AdnocFooterComponent],
})
export class AdnocFooterModule { }
