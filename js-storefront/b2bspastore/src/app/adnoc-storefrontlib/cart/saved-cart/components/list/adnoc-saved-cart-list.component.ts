/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  inject,
  OnDestroy,
  OnInit,
  ViewChild,
  ViewContainerRef,
} from '@angular/core';
import { Cart } from '@spartacus/cart/base/root';
import { SavedCartListComponent } from '@spartacus/cart/saved-cart/components';
import {
  SavedCartFacade,
  SavedCartFormType,
} from '@spartacus/cart/saved-cart/root';
import { RoutingService, useFeatureStyles } from '@spartacus/core';
import {
  LAUNCH_CALLER,
  LaunchDialogService,
  SiteContextComponentService,
  SiteContextType,
} from '@spartacus/storefront';
import { from, mergeMap, Observable, Subscription } from 'rxjs';
import { map, skip, take } from 'rxjs/operators';

@Component({
  selector: 'cx-saved-cart-list',
  templateUrl: './adnoc-saved-cart-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class AdnocSavedCartListComponent extends SavedCartListComponent {}