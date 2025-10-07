/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { I18nModule } from '@spartacus/core';
import { ToggleStatusComponent } from './toggle-status.component';
import { ConfirmationMessageModule } from '@spartacus/organization/administration/components';
import { MessageModule } from '../../message/message.module';

@NgModule({
  imports: [CommonModule, I18nModule, MessageModule, ConfirmationMessageModule],
  declarations: [ToggleStatusComponent],
  exports: [ToggleStatusComponent],
})
export class ToggleStatusModule {}
