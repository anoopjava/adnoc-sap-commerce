/*
 * SPDX-FileCopyrightText: 2025 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { Injectable } from '@angular/core';
import { select } from '@ngrx/store';
import {
  GlobalMessageActions,
  GlobalMessageEntities,
  GlobalMessageSelectors,
  GlobalMessageService,
  GlobalMessageType,
  isNotUndefined,
  Translatable,
} from '@spartacus/core';
import { filter, Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AdnocGlobalMessageService extends GlobalMessageService {
  /**
   * Get all global messages
   */
  override get(): Observable<GlobalMessageEntities> {
    return this.store.pipe(
      select(GlobalMessageSelectors.getGlobalMessageEntities),
      filter(isNotUndefined),
      tap(() => window.scrollTo({ top: 0, behavior: 'smooth' }))
    );
  }

  /**
   * Add one message into store
   * @param text: string | Translatable
   * @param type: GlobalMessageType object
   * @param timeout: number
   */
  override add(
    text: string | Translatable,
    type: GlobalMessageType,
    timeout?: number
  ): void {
    // Scroll to the top of the page
    window.scrollTo({ top: 0, behavior: 'smooth' });

    this.store.dispatch(
      new GlobalMessageActions.AddMessage({
        text: typeof text === 'string' ? { raw: text } : text,
        type,
        timeout: 15000, // Default timeout of 15 seconds
      })
    );
  }
}
