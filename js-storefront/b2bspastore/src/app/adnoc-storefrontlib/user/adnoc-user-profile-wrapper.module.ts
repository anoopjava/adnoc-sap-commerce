/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { NgModule, Type } from '@angular/core';
import { AdnocUserProfileModule } from './profile/adnoc-user-profile.module';

const extensions: Type<any>[] = [];

@NgModule({
  imports: [AdnocUserProfileModule, ...extensions],
})
export class AdnocUserProfileWrapperModule {}
