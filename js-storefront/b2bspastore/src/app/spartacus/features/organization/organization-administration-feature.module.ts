import { NgModule } from '@angular/core';
import { CmsConfig, I18nConfig, provideConfig } from '@spartacus/core';
import {
  AdministrationRootModule,
  ORGANIZATION_ADMINISTRATION_FEATURE,
} from '@spartacus/organization/administration/root';
import {
  organizationTranslationChunksConfig,
  organizationTranslations,
} from '../../../adnoc-storefrontlib/organization/administration/assets/translations/translations';

@NgModule({
  declarations: [],
  imports: [AdministrationRootModule],
  providers: [
    provideConfig(<CmsConfig>{
      featureModules: {
        [ORGANIZATION_ADMINISTRATION_FEATURE]: {
          module: () =>
            import(
              '../../../adnoc-storefrontlib/organization/administration/adnoc-administration.module'
            ).then((m) => m.AdnocAdministrationModule),
        },
      },
    }),
    provideConfig(<I18nConfig>{
      i18n: {
        resources: organizationTranslations,
        chunks: organizationTranslationChunksConfig,
      },
    }),
  ],
})
export class OrganizationAdministrationFeatureModule {}
