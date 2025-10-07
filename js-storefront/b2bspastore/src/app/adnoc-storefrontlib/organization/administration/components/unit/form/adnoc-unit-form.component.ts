/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  Input,
  OnDestroy,
  OnInit,
} from '@angular/core';
import { AbstractControl, UntypedFormGroup, Validators } from '@angular/forms';
import {
  BehaviorSubject,
  EMPTY,
  Observable,
  of,
  Subject,
  Subscription,
} from 'rxjs';
import {
  distinctUntilChanged,
  map,
  switchMap,
  takeUntil,
  tap,
} from 'rxjs/operators';
import { B2BUnit } from '@spartacus/core';
import { OrgUnitService } from '@spartacus/organization/administration/core';
import { Title } from '@spartacus/user/profile/root';
import { MatDateFormats } from '@angular/material/core';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';
import { CurrentItemService } from '../../shared/current-item.service';
import { ItemService } from '../../shared/item.service';
import { createCodeForEntityName } from '../../shared/utility/entity-code';
import { AdnocUnitItemService } from '../services/adnoc-unit-item.service';
import { AdnocCurrentUnitService } from '../services/adnoc-current-unit.service';
import { FILEUPLOADMAXSIZE } from '../../../../../constants/adnoc-user-account-constants';
import {
  Icommon,
  IcommonIso,
} from '../../../../../services/apiServices/api-response.model';
import { AdnocCustomFormService } from '../../shared/adnoc-form/adnoc-custom-form.service';
import { AdnocUnitFormService } from './adnoc-unit-form.service';
import { DatePipe } from '@angular/common';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';

export const CUSTOM_DATE_FORMATS: MatDateFormats = {
  parse: {
    dateInput: 'DD/MM/YYYY', // Format for input parsing
  },
  display: {
    dateInput: 'DD/MM/YYYY', // Format shown in the input
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  },
};
@Component({
  selector: 'cx-org-unit-form',
  templateUrl: './adnoc-unit-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'content-wrapper adnoc-org-unit-form' },
  providers: [
    {
      provide: ItemService,
      useExisting: AdnocUnitItemService,
    },
    {
      provide: CurrentItemService,
      useExisting: AdnocCurrentUnitService,
    },
    provideMomentDateAdapter(CUSTOM_DATE_FORMATS),
    DatePipe,
  ],
  standalone: false,
})
export class AdnocUnitFormComponent implements OnInit, OnDestroy {
  @Input() i18nRoot = 'orgUnit';
  @Input() createChildUnit = false;

  form: UntypedFormGroup | null;
  private subscription: Subscription = new Subscription(); // To hold the subscriptions

  isLoading$ = new BehaviorSubject(false);
  countries$!: Observable<IcommonIso[]>;
  regions!: IcommonIso[];
  titles$: Observable<Title[]>;
  gender$: Observable<Icommon[]>;
  nationality$: Observable<Icommon[]>;
  identityType$: Observable<Icommon[]>;

  tradeLicenseAuthority$: Observable<Icommon[]>;

  designation$: Observable<Icommon[]>;
  maxFileSize = 1;
  fileError = '';
  uploadedFiles: string | null = null;
  uploadedFilesVatIdDocument: string | null = null;
  identificationNumberDocument: File | null = null;
  uploadedFilesIdentificationNumberDocument: string | null = null;
  incoTerms$!: Observable<Icommon[]>;

  todayDate: Date = new Date();
  minStartDate!: Date;
  maxStartDate!: Date;
  selectedStartDate!: Date | string;
  minEndDate!: Date;
  maxEndDate!: Date;
  selectedEndDate!: Date | string;
  uid = '';
  partnerFunction: string = '';
  protected destroy$ = new Subject<void>();
  private titleCodeSubscription: any;
  private genderSubscription: any;
  contactPersonPhoneMinLength = 10;
  constructor(
    protected itemService: ItemService<B2BUnit>,
    protected unitService: OrgUnitService,
    protected unitFormService: AdnocUnitFormService,
    protected adnocCustomFormService: AdnocCustomFormService,
    private datePipe: DatePipe
  ) {
    this.form = this.itemService.getForm();
    this.countries$ = this.unitFormService
      .getCountries()
      .pipe(map((response) => response.countries || []));

    this.titles$ = this.unitFormService.getTitles();

    this.gender$ = this.unitFormService
      .getGenderOptions()
      .pipe(map((response) => response.genders || []));

    this.nationality$ = this.unitFormService
      .getNationalities()
      .pipe(map((response) => response.nationalities || []));

    this.identityType$ = this.unitFormService
      .getIdentityType()
      .pipe(map((response) => response.identityTypes || []));

    this.tradeLicenseAuthority$ = this.unitFormService
      .getTradeLicenseAuthorityTypes()
      .pipe(map((response) => response.tradeLicenseAuthorityTypes || []));

    this.designation$ = this.unitFormService
      .getDesignationTypes()
      .pipe(map((response) => response.designationTypes || []));

    this.incoTerms$ = this.unitFormService
      .getIncoTerms()
      .pipe(map((response) => response.incoTerms || []));
  }

  ngOnInit(): void {
    const regionSubscription = this.form
      ?.get('companyAddressCountryIso')!
      .valueChanges.pipe(
        switchMap(() => {
          this.form?.get('companyAddressRegion')?.setValue(null);
          if (
            this.form?.get('companyAddressCountryIso')?.value != null &&
            this.form?.get('companyAddressCountryIso')?.value !== ''
          ) {
            return this.unitFormService.getRegions(
              this.form?.get('companyAddressCountryIso')?.value
            );
          } else {
            return EMPTY;
          }
        }),
        tap((regions) => {
          let regionArray = regions.regions;
          this.regions = regionArray;
        })
      )
      .subscribe();
    this.subscription.add(regionSubscription);

    this.unitFormService
      .getConfigvalue(FILEUPLOADMAXSIZE)
      .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe({
        next: (response: { adnocConfigs: any[] }) => {
          const configItem = response.adnocConfigs?.find(
            (config) => config.configKey === FILEUPLOADMAXSIZE
          );
          if (configItem) {
            const configValue = configItem.configValue;
            this.maxFileSize = parseInt(configValue, 10);
          }
        },
      });

    // Set the minimum date to 100 years ago
    this.minStartDate = new Date(this.todayDate);
    this.minStartDate.setFullYear(this.todayDate.getFullYear() - 100); // Subtract 100 years

    this.todayDate.setHours(0, 0, 0, 0);
    this.maxStartDate = new Date(this.todayDate);

    // Initialize form for end date
    const tomorrow = new Date(this.todayDate);
    tomorrow.setDate(this.todayDate.getDate() + 1);
    this.minEndDate = tomorrow; // Minimum end date is tomorrow
    this.maxEndDate = new Date(this.todayDate); // Set maxEndDate to 100 years from today
    this.maxEndDate.setFullYear(this.todayDate.getFullYear() + 100); // Add 100 years to today

    this.adnocCustomFormService.unitParentInfo$.subscribe((data) => {
      if (data.partnerFunction) {
        this.partnerFunction = data.partnerFunction;
        const isSP = this.partnerFunction === 'SP';
        const requiredFields = ['vatId', 'vatIdDocument'];
        const optionalFields = ['incoTerms', 'latitude', 'longitude'];

        requiredFields.forEach((field) =>
          this.form
            ?.get(field)
            ?.setValidators(isSP ? Validators.required : null)
        );

        optionalFields.forEach((field) =>
          this.form
            ?.get(field)
            ?.setValidators(isSP ? null : Validators.required)
        );

        [...requiredFields, ...optionalFields].forEach((field) =>
          this.form?.get(field)?.updateValueAndValidity()
        );
      }

      if (data.parentOrgUnitName) {
        this.form?.get('companyName')?.setValue(data.parentOrgUnitName);
      }
    });

    // Subscribe to titleCode changes
    this.titleCodeSubscription = this.form
      ?.get('titleCode')
      ?.valueChanges.subscribe(() => {
        this.setGenderValue();
      });

    // Subscribe to gender changes
    this.genderSubscription = this.form
      ?.get('gender')
      ?.valueChanges.subscribe(() => {
        this.checkTitleGenderCombination();
      });

    this.form?.get('identityType')?.valueChanges.subscribe((type) => {
      const control = this.form?.get('identificationNumber');

      if (type === 'FS0001') {
        //FS0001 means Emirates ID
        control?.setValidators([
          Validators.required,
          Validators.pattern(/^\d{3}-\d{4}-\d{7}-\d{1}$/),
        ]);
      } else if (type === 'FS0002') {
        //FS0002 means Passport Number
        control?.setValidators([Validators.required, Validators.maxLength(20)]);
      }
      control?.reset();
      control?.updateValueAndValidity();
    });

    // Add the individual subscription to the subscription object
    this.subscription.add(this.titleCodeSubscription);
    this.subscription.add(this.genderSubscription);

    this.setupPhoneValidationForCountry(
      'countryOfOrigin',
      'telephone',
      (length) => (this.contactPersonPhoneMinLength = length)
    );
  }

  setupPhoneValidationForCountry(
    countryControlName: string,
    phoneControlName: string,
    updateMinLengthVar: (length: number) => void
  ) {
    this.form
      ?.get(countryControlName)
      ?.valueChanges.subscribe((countryCode) => {
        const length = countryCode === 'AE' ? 9 : 10;
        updateMinLengthVar(length);
        const phoneValidators = [
          Validators.minLength(length),
          Validators.maxLength(length),
          Validators.pattern('^[0-9]*$'),
        ];

        // Phone number is required
        this.form
          ?.get(phoneControlName)
          ?.setValidators([...phoneValidators]);
        this.form?.get(phoneControlName)?.updateValueAndValidity();
      });
  }

  createUidWithName(
    name: AbstractControl | null,
    code: AbstractControl | null
  ): void {
    createCodeForEntityName(name, code);
  }

  filterInputAcceptAlphaNumeric(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^a-zA-Z0-9 ]/g, '');
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
    switch (inputId) {
      case 'vatIdDocument':
        this.uploadedFilesVatIdDocument = fileName;
        break;
      case 'identificationNumberDocument':
        this.uploadedFilesIdentificationNumberDocument = fileName;
        break;
      default:
        this.uploadedFiles = fileName;
        break;
    }

    if (input.files && input.files.length > 0) {
      const file = input.files[0] as File;
      this.validateFile(file, inputId);
    }
  }

  validateFile(file: File, id: string): void {
    const maxSize = this.maxFileSize * 1024 * 1024;
    const allowedTypes = ['application/pdf'];
    if (!allowedTypes.includes(file.type)) {
      this.form?.get(id)?.setErrors({ invalidFileType: true });
      return;
    }
    if (file.size > maxSize) {
      this.form?.get(id)?.setErrors({ fileTooLarge: true });
      return;
    }
    this.fileError = '';
    switch (id) {
      case 'vatIdDocument':
        this.adnocCustomFormService.setvatIdDocumentFile(file);
        break;
      case 'identificationNumberDocument':
        this.adnocCustomFormService.setIdentificationNumberDocumentFile(file);
        break;
      default:
        this.adnocCustomFormService.setFile(file);
        break;
    }
  }

  startDateFilter = (date: Date | null): boolean => {
    return date !== null && date <= this.maxStartDate; // Allow dates up to and including today for validFrom
  };

  // Custom date filter for validTo (restricts to dates up to 100 years from today)
  endDateFilter = (date: Date | null): boolean => {
    return date !== null && date >= this.minEndDate && date <= this.maxEndDate;
  };

  onStartDateChange(event: MatDatepickerInputEvent<Date>): void {
    if (event.value) {
      const date = new Date(event.value);
      this.selectedStartDate =
        this.datePipe.transform(date, "yyyy-MM-dd'T'HH:mm:ssZ") || '';
      this.form
        ?.get('identificationValidFrom')
        ?.setValue(this.selectedStartDate);
    }
  }

  // Method to handle end date change
  onEndDateChange(event: MatDatepickerInputEvent<Date>): void {
    if (event.value) {
      const date = new Date(event.value);
      this.selectedEndDate =
        this.datePipe.transform(date, "yyyy-MM-dd'T'HH:mm:ssZ") || '';
      this.form?.get('identificationValidTo')?.setValue(this.selectedEndDate);
    }
  }

  filterInputPhoneNumber(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '');
  }

  setGenderValue() {
    const titleCode = this.form?.get('titleCode')?.value;
    this.gender$
      .pipe(
        switchMap((genders) => {
          let defaultGenderCode: string | null = null;
          switch (titleCode) {
            case '0001':
              defaultGenderCode =
                genders.find((gender) => gender.code === 'FEMALE')?.code ||
                null;
              break;
            case '0002':
              defaultGenderCode =
                genders.find((gender) => gender.code === 'MALE')?.code || null;
              break;
            default:
              defaultGenderCode = null;
              break;
          }
          return of(defaultGenderCode);
        })
      )
      .subscribe((defaultGenderCode) => {
        if (defaultGenderCode) {
          this.form?.get('gender')?.setValue(defaultGenderCode);
        } else {
          this.form?.get('gender')?.reset();
        }
      });
  }

  checkTitleGenderCombination() {
    const titleCode = this.form?.get('titleCode')?.value;
    const gender = this.form?.get('gender')?.value;
    if (this.titles$ && titleCode) {
      this.titles$.subscribe((titles) => {
        const title = titles.find((t) => t.code === titleCode)?.name;
        if (
          (title === 'Mr.' && gender === 'FEMALE') ||
          (title === 'Ms.' && gender === 'MALE')
        ) {
          this.form?.get('gender')?.setErrors({ invalidGender: true });
        } else {
          this.form?.get('gender')?.setErrors(null);
        }
      });
    }
  }

  removeUploadedFile(fieldName: string) {
    switch (fieldName) {
      case 'identificationNumberDocument':
        this.identificationNumberDocument = null;
        this.uploadedFilesIdentificationNumberDocument = null;
        this.form?.get('identificationNumberDocument')?.reset();
        break;
      default:
        //this.selectedFile = null;
        this.uploadedFiles = '';
        this.form?.get('otherDocument')?.reset();
        break;
    }
  }

  resetIdentificationFields() {
    this.removeUploadedFile('identificationNumberDocument');
    this.form?.get('identificationNumber')?.reset();
    this.form?.get('identificationValidFrom')?.reset();
    this.form?.get('identificationValidTo')?.reset();
  }

  filterInputAcceptNumbers(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '');
  }

  onIdentificationNumberInput(event: any): void {
    const inputElement = event.target as HTMLInputElement;
    const identityType = this.form?.get('identityType')?.value;

    const originalCursor = inputElement.selectionStart ?? 0;
    const rawValue = inputElement.value;

    if (identityType === 'FS0001') {
      // Remove all non-digits
      let digits = rawValue.replace(/\D/g, '').slice(0, 15); // max 15 digits (no hyphens counted here)

      // Format: XXX-XXXX-XXXXXXX-X
      let formatted = '';
      if (digits.length > 0) formatted += digits.substring(0, 3);
      if (digits.length > 3) formatted += '-' + digits.substring(3, 7);
      if (digits.length > 7) formatted += '-' + digits.substring(7, 14);
      if (digits.length > 14) formatted += '-' + digits.substring(14, 15);

      // Set formatted value
      inputElement.value = formatted;
      this.form
        ?.get('identificationNumber')
        ?.setValue(formatted, { emitEvent: false });

      // Calculate new cursor position
      let newCursorPos = originalCursor;

      // Positions in the raw input where hyphens appear in formatted string
      const hyphenPositions = [3, 8, 16];

      // If cursor just passed a hyphen insertion, move it forward by 1
      let digitCountBeforeCursor = 0;
      for (let i = 0; i < originalCursor; i++) {
        if (/\d/.test(rawValue[i])) digitCountBeforeCursor++;
      }

      // Count how many hyphens should be before the cursor based on digitCountBeforeCursor
      let hyphensBeforeCursor = 0;
      if (digitCountBeforeCursor > 3) hyphensBeforeCursor++;
      if (digitCountBeforeCursor > 7) hyphensBeforeCursor++;
      if (digitCountBeforeCursor > 14) hyphensBeforeCursor++;

      newCursorPos = digitCountBeforeCursor + hyphensBeforeCursor;

      // Fix cursor position (must be within value length)
      if (newCursorPos > formatted.length) {
        newCursorPos = formatted.length;
      }

      // Restore cursor position asynchronously
      setTimeout(() => {
        inputElement.setSelectionRange(newCursorPos, newCursorPos);
      }, 0);
    } else {
      // Passport: clean input to alphanumeric max length 20
      let cleaned = rawValue.replace(/[^a-zA-Z0-9]/g, '').slice(0, 20);
      inputElement.value = cleaned;
      this.form
        ?.get('identificationNumber')
        ?.setValue(cleaned, { emitEvent: false });

      // Restore cursor to end (simple approach)
      setTimeout(() => {
        const len = inputElement.value.length;
        inputElement.setSelectionRange(len, len);
      }, 0);
    }
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
