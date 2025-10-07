import { NgModule } from '@angular/core';
import { CmsConfig, I18nConfig, provideConfig } from '@spartacus/core';
import {
  organizationUserRegistrationTranslationChunksConfig,
  organizationUserRegistrationTranslations,
} from '../../../adnoc-storefrontlib/organization/user-registration/assets/public_api';
import {
  OrganizationUserRegistrationRootModule,
  ORGANIZATION_USER_REGISTRATION_FEATURE,
} from '@spartacus/organization/user-registration/root';


@NgModule({
  declarations: [],
  imports: [OrganizationUserRegistrationRootModule],
  providers: [
    provideConfig(<CmsConfig>{
      featureModules: {
        [ORGANIZATION_USER_REGISTRATION_FEATURE]: {
          module: () =>
            import(
              '../../../adnoc-storefrontlib/organization/user-registration/adnoc-user-registration.module'
            ).then((m) => m.AdnocOrganizationUserRegistrationModule),
        },
      },
    }),
    provideConfig(<I18nConfig>{
      i18n: {
        resources: organizationUserRegistrationTranslations,
        chunks: organizationUserRegistrationTranslationChunksConfig,
      },
    }),
  ],
})
export class OrganizationUserRegistrationFeatureModule {}
