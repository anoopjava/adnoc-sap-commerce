/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  ViewEncapsulation,
} from '@angular/core';
import { AdnocUnitListComponent } from '../../../administration/components/unit/list';

@Component({
  selector: 'cx-account-summary-list',
  templateUrl: './adnoc-account-summary-list.component.html',
  styleUrls: ['./adnoc-account-summary-list.component.scss'],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'adnoc-org-account-summary-list' },
  standalone: false,
})
export class AdnocAccountSummaryListComponent extends AdnocUnitListComponent {}
