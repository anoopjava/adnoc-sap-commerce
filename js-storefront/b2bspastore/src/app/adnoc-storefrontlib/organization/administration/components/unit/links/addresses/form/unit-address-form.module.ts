/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { NgSelectModule } from '@ng-select/ng-select';
import { FeaturesConfigModule, I18nModule, UrlModule } from '@spartacus/core';
import { FormErrorsModule } from '@spartacus/storefront';
//import { FormModule } from '../../../../shared/form/form.module';
import { UnitAddressFormComponent } from './unit-address-form.component';
import { FormModule } from '@spartacus/organization/administration/components';
import { AdnocFormModule } from '../../../../shared/adnoc-form/adnoc-form.module';

@NgModule({
  imports: [
    CommonModule,
    AdnocFormModule,
    NgSelectModule,
    UrlModule,
    I18nModule,
    ReactiveFormsModule,
    FormErrorsModule,
    FeaturesConfigModule,
  ],
  declarations: [UnitAddressFormComponent],
})
export class UnitAddressFormModule {}
