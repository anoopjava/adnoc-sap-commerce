/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  HostBinding,
  Input,
  ViewChild,
} from '@angular/core';
import { EntitiesModel } from '@spartacus/core';
import { TableStructure } from '@spartacus/storefront';
import { Observable } from 'rxjs';
import { switchMap, tap } from 'rxjs/operators';
import { AdnocListComponent } from '../list';
import { MessageService } from '../message/service/message.service';

@Component({
  selector: 'cx-org-sub-list',
  templateUrl: './sub-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'content-wrapper' },
  standalone: false,
})
export class SubListComponent extends AdnocListComponent {
  hostClass = '';

  @ViewChild(MessageService, { read: MessageService })
  messageService!: MessageService;

  @Input() previous: boolean | string = true;

  @Input() override key = this.adnocListService.key();

  @Input() showHint? = false;

  @Input() set routerKey(key: string) {
    this.subKey$ = this.organizationItemService.getRouterParam(key);
  }

  @HostBinding('class.ghost') override hasGhostData = false;

  subKey$!: Observable<string>;

  override readonly listData$: Observable<EntitiesModel<any> | undefined> =
    this.currentKey$.pipe(
      switchMap((key) => this.adnocListService.getData(key)),
      tap((data) => {
        this.hasGhostData = this.adnocListService.hasGhostData(data);
      })
    );

  readonly dataStructure$: Observable<TableStructure> =
    this.adnocListService.getStructure();
}
