/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  inject,
  OnDestroy,
  OnInit,
  ViewEncapsulation,
} from '@angular/core';
import {
  GlobalMessageType,
  LanguageService,
  SortModel,
  TranslationService,
} from '@spartacus/core';
import { AccountSummaryFacade } from '@spartacus/organization/account-summary/root';
import { FileDownloadService, ICON_TYPE } from '@spartacus/storefront';
import {
  BehaviorSubject,
  combineLatest,
  Observable,
  of,
  Subscription,
} from 'rxjs';
import { catchError, skip, switchMap, take, tap } from 'rxjs/operators';
import {
  AccountSummaryDocumentType,
  AccountSummaryList,
  DocumentFields,
  DocumentQueryParams,
  DocumentStatus,
  FilterByOptions,
} from '../../../root/model';
import { AdnocGlobalMessageService } from '../../../../../../core/global-message/facade/adnoc-global-message.service';

@Component({
  selector: 'cx-account-summary-document',
  templateUrl: './adnoc-account-summary-document.component.html',
  styleUrl: './adnoc-account-summary-document.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  host: { class: 'adnoc-account-summary-document' },
  standalone: false,
})
export class AdnocAccountSummaryDocumentComponent implements OnInit, OnDestroy {
  protected cd = inject(ChangeDetectorRef);
  /* For Enum use in HTML */
  ICON_TYPE = ICON_TYPE;

  documentTypeOptions!: AccountSummaryDocumentType[];
  sortOptions!: SortModel[];

  // Contains the initial query parameters and will be updated with current state of filters
  _queryParams: DocumentQueryParams = {
    status: DocumentStatus.OPEN,
    filterByKey: FilterByOptions.DOCUMENT_NUMBER,
    page: 0,
    pageSize: 10,
    fields: DocumentFields.FULL,
  };
  // Used to fire event every time query params are changed
  queryParams$ = new BehaviorSubject<DocumentQueryParams>(this._queryParams);
  // Used by template to subscribe to data from documents api
  accountSummary$: Observable<AccountSummaryList> = this.queryParams$.pipe(
    switchMap((param: any) => this.accountSummaryFacade.getDocumentList(param)),
    tap((accountSummaryList: AccountSummaryList) => {
      if (accountSummaryList.orgDocumentTypes) {
        this.documentTypeOptions = accountSummaryList.orgDocumentTypes;
      }

      if (accountSummaryList.sorts) {
        this.addNamesToSortModel(accountSummaryList.sorts);
      }
    })
  );

  private subscription = new Subscription();
  loader = false;
  loaderMap: { [key: string]: boolean } = {}; // Track loading state for each document
  constructor(
    protected accountSummaryFacade: AccountSummaryFacade,
    protected translation: TranslationService,
    protected downloadService: FileDownloadService,
    protected languageService: LanguageService,
    protected globalMessageService: AdnocGlobalMessageService
  ) {}

  ngOnInit() {
    this.subscription.add(
      this.languageService
        .getActive()
        .pipe(skip(1))
        .subscribe(() =>
          this.updateQueryParams({ fields: DocumentFields.FULL })
        )
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }

  pageChange(page: number): void {
    this.updateQueryParams({
      page: page,
    });
  }

  changeSortCode(sortCode: string): void {
    this.updateQueryParams({
      sort: sortCode,
      page: 0,
    });
  }

  filterChange(newFilters: DocumentQueryParams): void {
    this.updateQueryParams({
      ...newFilters,
      page: 0,
    });
  }

  downloadAttachment(documentId?: string): void {
    if (!documentId) return;
    const attachmentId: string = '';
    this.loaderMap[documentId] = true; 
    this.cd.detectChanges();

    this.accountSummaryFacade
      .getDocumentAttachment(documentId, attachmentId)
      .pipe(
        take(1),
        catchError((error) => {
          this.loaderMap[documentId] = false; 
          this.cd.detectChanges();
          const message = error?.message;
          this.globalMessageService.add(
            { key: message },
            GlobalMessageType.MSG_TYPE_ERROR
          );
          return of(null);
        })
      )
      .subscribe({
        next: (data) => {
          this.loaderMap[documentId] = false; 
          this.cd.detectChanges();
          if (!data) return;
          const file = new Blob([data], { type: data.type });
          const url = URL.createObjectURL(file);
          this.downloadService.download(url, attachmentId);
        },
      });
  }

  private updateQueryParams(partialParams: DocumentQueryParams) {
    // Overwrite each value present in partialParams to _queryParams
    Object.entries(partialParams).forEach(
      (param) => ((this._queryParams as any)[param[0]] = param[1])
    );
    // Every request that doesn't specify fields should be set to DEFAULT
    if (!partialParams.fields) {
      this._queryParams.fields = DocumentFields.DEFAULT;
    }

    this.queryParams$.next(this._queryParams);
  }

  private addNamesToSortModel(sorts: Array<SortModel>) {
    this.sortOptions = sorts;
    const translations = sorts.map((sort) =>
      this.translation.translate(`orgAccountSummary.sorts.${sort.code}`)
    );

    combineLatest(translations)
      .pipe(take(1))
      .subscribe((translated) =>
        this.sortOptions.forEach(
          (sort, index) => (sort.name = translated[index])
        )
      );
  }
}
