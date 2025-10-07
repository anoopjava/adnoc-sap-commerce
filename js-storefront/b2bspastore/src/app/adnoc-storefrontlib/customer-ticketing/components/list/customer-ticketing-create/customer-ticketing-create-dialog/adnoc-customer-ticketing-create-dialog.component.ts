/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  inject,
  Input,
  OnDestroy,
  OnInit,
  ViewEncapsulation,
} from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import {
  AssociatedObject,
  CustomerTicketingConfig,
  CustomerTicketingFacade,
  MAX_ENTRIES_FOR_ATTACHMENT,
  TicketDetails,
} from '@spartacus/customer-ticketing/root';
import {
  Adnoccsticketcategorymaplist,
  AdnocTicketStarter,
  RequestType,
} from '../../../../root/model/adnoc-customer-ticketing.model';
import {
  FilesFormValidators,
  FormUtils,
  LaunchDialogService,
} from '@spartacus/storefront';
import {
  distinctUntilChanged,
  first,
  map,
  Subject,
  Subscription,
  takeUntil,
} from 'rxjs';
import {
  GlobalMessageType,
  HttpErrorModel,
  RoutingService,
  TranslationService,
} from '@spartacus/core';

import { AdnocCustomerTicketingDialogComponent } from '../../../shared/customer-ticketing-dialog/adnoc-customer-ticketing-dialog.component';
import { AdnocCustomerDialogFormService } from './adnoc-custom-ticket.service';
import { FILEUPLOADMAXSIZE } from '../../../../../constants/adnoc-user-account-constants';
import { AdnocConfigRoot } from '../../../../../services/apiServices/api-response.model';
import { AdnocGlobalMessageService } from '../../../../../../core/global-message/facade/adnoc-global-message.service';

@Component({
  selector: 'cx-customer-ticketing-create-dialog',
  templateUrl: './adnoc-customer-ticketing-create-dialog.component.html',
  styleUrl: './adnoc-customer-ticketing-create-dialog.component.scss',
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'adnoc-customer-ticketing-create-dialog' },
  standalone: false,
})
export class AdnocCustomerTicketingCreateDialogComponent
  extends AdnocCustomerTicketingDialogComponent
  implements OnInit, OnDestroy
{
  @Input() selectedType!: RequestType;
  @Input()
  selectedAssociatedObject!: AssociatedObject;
  subscription!: Subscription;
  attachment!: File;
  protected globalMessage = inject(AdnocGlobalMessageService);

  protected translationService = inject(TranslationService);

  destroy$ = new Subject<void>();

  customerServiceInfo!: Adnoccsticketcategorymaplist[];
  associatesInfo!: AssociatedObject[];
  selectedMapId = '';
  requestTypes!: RequestType[];
  requestCategories!: RequestType[];
  subCategories!: RequestType[];
  selectedRequestType!: string;
  selectedCategory!: string;
  selectedSubCategory!: string;
  maxFileSize = 1;

  constructor(
    protected override launchDialogService: LaunchDialogService,
    protected override el: ElementRef,
    protected override customerTicketingConfig: CustomerTicketingConfig,
    protected override filesFormValidators: FilesFormValidators,
    protected override customerTicketingFacade: CustomerTicketingFacade,
    protected override routingService: RoutingService,
    protected AdnocCustomerTicketingService: AdnocCustomerDialogFormService,
    private cdr: ChangeDetectorRef
  ) {
    super(
      launchDialogService,
      el,
      customerTicketingConfig,
      filesFormValidators,
      customerTicketingFacade,
      routingService
    );
  }

  ngOnInit(): void {
    this.buildForm();

    this.AdnocCustomerTicketingService.getRequestDetails('current')
      .pipe(
        map((data) => data?.adnocCsTicketCategoryMapList || []),
        takeUntil(this.destroy$),
        distinctUntilChanged()
      )
      .subscribe({
        next: (data) => {
          this.customerServiceInfo = data;
          this.requestTypes = this.getUnique(data, 'requestType', 'name');
          this.cdr.detectChanges();
        },
        error: (error) => {
          this.handleError(error);
        },
      });

    // Listen for changes in request Type selection
    this.form
      .get('ticketRequest')
      ?.valueChanges.pipe(takeUntil(this.destroy$), distinctUntilChanged())
      .subscribe((selectedType) => {
        this.selectedRequestType = selectedType;
        this.form.get('ticketCategory')?.enable();
        this.onRequestTypeChange();
      });

    // Listen for changes in request category selection
    this.form
      .get('ticketCategory')
      ?.valueChanges.pipe(takeUntil(this.destroy$), distinctUntilChanged())
      .subscribe((selectedCategory) => {
        this.selectedCategory = selectedCategory;
        this.onRequestCategoryChange();
      });

    this.form
      .get('ticketSubCategory')
      ?.valueChanges.pipe(takeUntil(this.destroy$), distinctUntilChanged())
      .subscribe((selectedSubCategory) => {
        this.selectedSubCategory = selectedSubCategory;
        this.onRequestSubCategoryChange();
      });

    this.AdnocCustomerTicketingService.getConfigvalue(
      FILEUPLOADMAXSIZE
    ).subscribe(
      (response: AdnocConfigRoot) => {
        const configItem = response.adnocConfigs?.find(
          (config) => config.configKey === FILEUPLOADMAXSIZE
        );
        if (configItem) {
          const configValue = configItem.configValue;
          this.maxFileSize = parseInt(configValue, 10);
        } else {
          console.log('Config value not found, using default.');
        }
      },
      (error: any) => {
        console.error('Error fetching config value:', error);
      }
    );
  }

  protected getUnique(list: any[], property: string, sortKey: string): any[] {
    const map = new Map();

    list.forEach((item) => {
      const key = item[property]?.code;
      if (key && !map.has(key)) {
        map.set(key, item[property]);
      }
    });

    // Convert to array & sort by given sortKey
    return Array.from(map.values()).sort((a: any, b: any) =>
      (a?.[sortKey] ?? '').localeCompare(b?.[sortKey] ?? '')
    );
  }

  protected override buildForm(): void {
    const form = new FormGroup({});
    form.setControl(
      'subject',
      new FormControl('TEST_SUBJECT', [
        Validators.required,
        Validators.maxLength(this.inputCharactersForSubject),
      ])
    );
    form.setControl(
      'ticketRequest',
      new FormControl('', [Validators.required])
    );
    form.setControl(
      'ticketCategory',
      new FormControl({ value: '', disabled: true }, [Validators.required])
    );
    form.setControl(
      'ticketSubCategory',
      new FormControl({ value: '', disabled: true }, [Validators.required])
    );
    form.setControl(
      'associatedTo',
      new FormControl({ value: '', disabled: true }, [Validators.required])
    );
    form.setControl(
      'message',
      new FormControl('', [
        Validators.required,
        Validators.maxLength(this.inputCharactersLimit),
      ])
    );
    form.setControl(
      'file',
      new FormControl('', [
        this.filesFormValidators.maxSize(this.maxFileSize),
        this.filesFormValidators.maxEntries(MAX_ENTRIES_FOR_ATTACHMENT),
        this.filesFormValidators.allowedTypes(this.allowedTypes),
      ])
    );
    this.form = form;
  }

  createTicketRequest(): void {
    this.attachment = this.form.get('file')?.value?.[0];
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      FormUtils.deepUpdateValueAndValidity(this.form);
    } else {
      this.subscription = this.customerTicketingFacade
        .createTicket(this.getCreateTicketPayload(this.form))
        .subscribe({
          next: (response: TicketDetails) => {
            if (
              response.id &&
              this.attachment &&
              response.ticketEvents?.[0].code
            ) {
              this.customerTicketingFacade.uploadAttachment(
                this.attachment,
                response.ticketEvents?.[0].code,
                response.id
              );
            }
          },
          complete: () => {
            this.onComplete();
          },
          error: (error: any) => {
            this.handleError(error);
          },
        });
    }
  }

  protected handleError(error: any): void {
    if (error instanceof HttpErrorModel) {
      (error.details ?? []).forEach((err) => {
        if (err.message) {
          this.globalMessage.add(
            { raw: err.message },
            GlobalMessageType.MSG_TYPE_ERROR
          );
        }
      });
    } else {
      this.translationService
        .translate('httpHandlers.unknownError')
        .pipe(first())
        .subscribe((text) => {
          this.globalMessage.add(
            { raw: text },
            GlobalMessageType.MSG_TYPE_ERROR
          );
        });
    }
    this.onError();
  }

  //load the request Type data from api on form load
  onRequestTypeChange() {
    if (this.selectedRequestType && this.customerServiceInfo) {
      const filteredInfo = this.customerServiceInfo.filter(
        (item) => item.requestType?.code === this.selectedRequestType
      );
      this.requestCategories = this.getUnique(
        filteredInfo,
        'requestFor',
        'name'
      );
    } else {
      this.requestCategories = [];
      this.subCategories = [];
    }

    this.selectedMapId = '';

    /* when dropdown changes will disable & reset the other dropdowns based on level */
    this.form
      .get('ticketRequest')
      ?.valueChanges.pipe(takeUntil(this.destroy$), distinctUntilChanged())
      .subscribe((value) => {
        if (value) {
          this.resetFormExceptFirst();
          this.form.get('ticketCategory')?.enable();
        } else {
          this.resetForm();
        }
      });

    this.form
      .get('ticketCategory')
      ?.valueChanges.pipe(takeUntil(this.destroy$), distinctUntilChanged())
      .subscribe((value) => {
        if (value) {
          this.resetFormExceptSecond();
          this.form.get('ticketSubCategory')?.enable();
        } else {
          this.form.get('ticketSubCategory')?.disable();
          this.form.get('associatedTo')?.disable();
        }
      });

    this.form
      .get('ticketSubCategory')
      ?.valueChanges.pipe(takeUntil(this.destroy$), distinctUntilChanged())
      .subscribe((value) => {
        if (value) {
          this.resetFormExceptLast();
          this.form.get('associatedTo')?.enable();
        } else {
          this.form.get('associatedTo')?.disable();
        }
      });
  }

  onRequestCategoryChange() {
    this.selectedType = this.form.get('ticketRequest')?.value;
    this.selectedCategory = this.form.get('ticketCategory')?.value;
    if (
      this.selectedType &&
      this.selectedCategory &&
      this.customerServiceInfo
    ) {
      const filteredItems = this.customerServiceInfo.filter(
        (item) =>
          item.requestType?.code === this.selectedType &&
          item.requestFor?.code === this.selectedCategory
      );
      this.subCategories = this.getUnique(filteredItems, 'subCategory', 'name');
    } else {
      this.subCategories = [];
    }
    this.selectedMapId = '';
  }

  onRequestSubCategoryChange() {
    this.selectedType = this.form.get('ticketRequest')?.value;
    this.selectedCategory = this.form.get('ticketCategory')?.value;
    this.selectedSubCategory = this.form.get('ticketSubCategory')?.value;

    const selectedItem = this.customerServiceInfo.find(
      (item) =>
        item.requestType?.code === this.selectedType &&
        item.requestFor?.code === this.selectedCategory &&
        item.subCategory?.code === this.selectedSubCategory
    );

    this.selectedMapId = selectedItem?.csTicketCategoryMapId || '';
    if (this.selectedMapId) {
      this.getAssociateInfo();
    }
  }

  /* load the assocatesTo mapping values based on subcategory selection*/
  getAssociateInfo() {
    this.AdnocCustomerTicketingService.getAssociateMapDetails(
      this.selectedMapId
    )
      .pipe(
        map((data) => data?.ticketAssociatedObjects || []),
        takeUntil(this.destroy$),
        distinctUntilChanged()
      )
      .subscribe({
        next: (data) => {
          this.associatesInfo = data;
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error(error);
        },
      });
  }

  /* reset the form if dropdowns change */
  resetForm() {
    this.form.reset({
      ticketRequest: '',
      ticketCategory: '',
      ticketSubCategory: '',
      associatedTo: '',
    });
  }

  resetFormExceptFirst() {
    this.form.patchValue({
      ticketCategory: '',
      ticketSubCategory: '',
      associatedTo: '',
    });
  }

  resetFormExceptSecond() {
    this.form.patchValue({
      ticketSubCategory: '',
      associatedTo: '',
    });
  }

  resetFormExceptLast() {
    this.form.patchValue({
      associatedTo: '',
    });
  }

  formatToCustomDateTime(date: Date): string {
    const offset = date.getTimezoneOffset();
    const offsetSign = offset <= 0 ? '+' : '-';
    const absOffset = Math.abs(offset);
    const offsetHours = String(Math.floor(absOffset / 60)).padStart(2, '0');
    const offsetMinutes = String(absOffset % 60).padStart(2, '0');

    // Format date and time components
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');

    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}${offsetSign}${offsetHours}:${offsetMinutes}`;
  }

  /* sending the payload information when submit the form */
  protected getCreateTicketPayload(form: FormGroup): AdnocTicketStarter {
    return {
      subject: form?.get('subject')?.value,
      message: form?.get('message')?.value,
      ticketCategory: {
        id: form?.get('ticketRequest')?.value,
        name: form?.get('ticketRequest')?.value,
      },
      requestFor: {
        code: form?.get('ticketCategory')?.value,
      },
      subCategory: {
        code: form?.get('ticketSubCategory')?.value,
      },
      associatedTo: {
        code: form?.get('associatedTo')?.value?.code,
        type: form?.get('associatedTo')?.value?.type,
        modifiedAt: this.formatToCustomDateTime(new Date()),
      },
      csTicketCategoryMapId: this.selectedMapId,
    };
  }

  protected onComplete(): void {
    this.close('Ticket created successfully');
  }

  protected onError(): void {
    this.close('Something went wrong');
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
