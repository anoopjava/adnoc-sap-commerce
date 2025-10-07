import { NgModule } from '@angular/core';
import { CmsConfig, I18nConfig, provideConfig } from "@spartacus/core";
import { accountSummaryTranslationChunksConfig } from "@spartacus/organization/account-summary/assets";
import { AccountSummaryRootModule, ORGANIZATION_ACCOUNT_SUMMARY_FEATURE } from "@spartacus/organization/account-summary/root";
import { accountSummaryTranslations } from '../../../adnoc-storefrontlib/organization/account-summary/assets/public_api';

@NgModule({
  declarations: [],
  imports: [AccountSummaryRootModule],
  providers: [
    provideConfig(<CmsConfig>{
      featureModules: {
        [ORGANIZATION_ACCOUNT_SUMMARY_FEATURE]: {
          module: () =>
            import(
              '../../../adnoc-storefrontlib/organization/account-summary/adnoc-account-summary.module'
            ).then((m) => m.AdnocAccountSummaryModule),
        },
      },
    }),
    provideConfig(<I18nConfig>{
      i18n: {
        resources: accountSummaryTranslations,
        chunks: accountSummaryTranslationChunksConfig,
      },
    }),
  ],
})
export class OrganizationAccountSummaryFeatureModule {}
