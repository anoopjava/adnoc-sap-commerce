/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule, Type } from '@angular/core';
//@ts-ignore
import { CdcAdministrationModule } from '@spartacus/cdc/organization/administration';
import { AdnocAdministrationModule } from './administration/adnoc-administration.module';

const extensions: Type<any>[] = [];

//if (environment.cdc) {
  extensions.push(CdcAdministrationModule);
//}
@NgModule({
  imports: [AdnocAdministrationModule, ...extensions],
})
export class AdnocAdministrationWrapperModule {}
