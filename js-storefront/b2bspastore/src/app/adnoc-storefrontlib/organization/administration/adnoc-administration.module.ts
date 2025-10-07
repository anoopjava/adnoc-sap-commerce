/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule } from '@angular/core';
import { AdnocAdministrationComponentsModule } from './components/adnoc-administration-components.module';
import { AdministrationCoreModule } from '@spartacus/organization/administration/core';
import { AdnocAdministrationOccModule } from './occ/adnoc-administration-occ.module';


@NgModule({
  imports: [
    AdministrationCoreModule.forRoot(),
    AdnocAdministrationOccModule,
    AdnocAdministrationComponentsModule,
  ],
})
export class AdnocAdministrationModule {
}
