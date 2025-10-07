/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  Input,
  ViewEncapsulation,
  OnDestroy,
  ChangeDetectorRef,
  AfterViewInit,
} from '@angular/core';
import {
  UntypedFormControl,
  UntypedFormGroup,
  ValidationErrors,
} from '@angular/forms';
import { OrderEntry } from '@spartacus/cart/base/root';
import { Price } from '@spartacus/core';
import { map, Observable, Subject, Subscription } from 'rxjs';
import { AdnocOrderAmendService } from '../adnoc-amend-order.service';
import { FILEUPLOADMAXSIZE } from '../../../../constants/adnoc-user-account-constants';
import { AdnocConfigRoot } from '../../../../services/apiServices/api-response.model';
import { AdnocOrderEntry } from '../../../../../core/model/adnoc-cart.model';
import { IReturnReason } from '../../../../../core/model/adnoc-users.model';
@Component({
  selector: 'adnoc-amend-order-items',
  templateUrl: './adnoc-amend-order-items.component.html',
  styleUrls: ['./adnoc-amend-order-items.component.scss'],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class AdnocCancelOrReturnItemsComponent
  implements AfterViewInit, OnDestroy
{
  @Input() entries: AdnocOrderEntry[] | undefined;
  @Input() isConfirmation = false;
  @Input() isReturnOrder = false;
  @Input() orderNumber: string | undefined;

  form$: Observable<UntypedFormGroup>;
  forms!: UntypedFormGroup;
  protected destroy$ = new Subject<void>();
  returnReasons$: Observable<IReturnReason[]>;
  maxFileSize = 1;
  uploadedFiles: string | null = null;
  private formSubscription: Subscription | undefined;
  selectedReturnReason: string = '';

  constructor(
    protected orderAmendService: AdnocOrderAmendService,
    protected cd: ChangeDetectorRef
  ) {
    this.form$ = this.orderAmendService.getForm();
    this.returnReasons$ = this.orderAmendService
      .getReturnReson()
      .pipe(map((data) => data?.returnReasons));
  }

  ngOnInit(): void {
    this.orderAmendService
      .getFileUploadConfigvalue(FILEUPLOADMAXSIZE)
      .subscribe(
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

    this.formSubscription = this.form$.subscribe((form: UntypedFormGroup) => {
      const filePath = form.get('returnRequestDocument')?.value;
      this.uploadedFiles = filePath ? filePath.split('\\').pop() : null;
      this.selectedReturnReason = form.get('returnReason')?.value.split('|')[0];
    });
  }

  ngAfterViewInit(): void {
    const entriesControl = this.forms.get('cancelReason');
    this.clearControlError(entriesControl as UntypedFormControl);
  }

  getControl(form: UntypedFormGroup, entry: OrderEntry) {
    this.forms = form;
    const control = <UntypedFormControl>(
      form.get('entries')?.get(entry.entryNumber?.toString() ?? '')
    );
    return { control };
  }

  getFormvalues(form: UntypedFormGroup, item: string) {
    this.forms = form;
    const control = <UntypedFormControl>form.get(item);
    return { control };
  }

  setAll(form: UntypedFormGroup): void {
    this.entries?.forEach((entry) =>
      this.getControl(form, entry).control.setValue(
        this.getMaxAmendQuantity(entry)
      )
    );
  }

  getItemPrice(entry: OrderEntry): Price {
    return this.orderAmendService.getAmendedPrice(entry);
  }

  getMaxAmendQuantity(entry: OrderEntry) {
    return this.orderAmendService.getMaxAmendQuantity(entry);
  }

  isCancellation() {
    return this.orderAmendService.isCancellation();
  }

  setControlError(control: UntypedFormControl, error: ValidationErrors): void {
    control.setErrors(error);
  }
  clearControlError(control: UntypedFormControl): void {
    control.setErrors(null);
  }

  handleFileInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const inputId = input.id;
    const fileName =
      input.files && input.files.length > 0
        ? Array.from(input.files)
            .map((file) => file.name)
            .join(', ')
        : null;
    this.uploadedFiles = fileName;
    if (input.files && input.files.length > 0) {
      const file = input.files[0] as File;
      this.validateFile(file, inputId);
    }
  }

  validateFile(file: File, id: string): void {
    const maxSize = this.maxFileSize * 1024 * 1024;
    const allowedTypes = ['application/pdf'];
    if (!allowedTypes.includes(file.type)) {
      this.forms.get(id)?.setErrors({ invalidFileType: true });
      return;
    }
    if (file.size > maxSize) {
      this.forms
        .get(id)
        ?.setErrors({ fileTooLarge: { maxFileSize: this.maxFileSize } });
      return;
    }
    if (file && id == 'returnRequestDocument') {
      this.orderAmendService.setFile(file);
    }
  }

  removeUploadedFile(form: UntypedFormGroup) {
    this.uploadedFiles = null;
    this.forms = form;
    this.forms?.get('returnRequestDocument')?.reset();
  }

  returnRequestDocumentControl(form: UntypedFormGroup): UntypedFormControl {
    this.forms = form;
    return this.forms?.get('returnRequestDocument') as UntypedFormControl;
  }

  returnReasonControl(form: UntypedFormGroup): UntypedFormControl {
    this.forms = form;
    return this.forms?.get('returnReason') as UntypedFormControl;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.formSubscription) {
      this.formSubscription.unsubscribe();
    }
  }
}
