/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  inject,
  Inject,
  OnDestroy,
} from '@angular/core';
import { FormGroup, Validators } from '@angular/forms';
import { GlobalMessageType } from '@spartacus/core';
import { Title } from '@spartacus/user/profile/root';
import {
  BehaviorSubject,
  map,
  Observable,
  of,
  Subscription,
  switchMap,
  tap,
} from 'rxjs';
import { AdnocUserRegistrationFormService } from './adnoc-user-registration-form.service';
import { MatDateFormats } from '@angular/material/core';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';
import {
  AdnocConfigRoot,
  IcommonIso,
} from '../../../../services/apiServices/api-response.model';
import { Router } from '@angular/router';
import { DatePipe, DOCUMENT } from '@angular/common';
import { FILEUPLOADMAXSIZE } from '../../../../constants/adnoc-user-account-constants';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

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
  selector: 'adnoc-user-registration-form',
  templateUrl: './adnoc-user-registration-form.component.html',
  styleUrl: './adnoc-user-registration-form.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [provideMomentDateAdapter(CUSTOM_DATE_FORMATS), DatePipe],
  standalone: false,
})
export class AdnocUserRegistrationFormComponent implements OnDestroy {
  protected userRegistrationFormService = inject(
    AdnocUserRegistrationFormService
  );

  submitted = false;
  tlnDocument: File | null = null;
  vatIdDocument: File | null = null;
  selectedFile: File | null = null;
  identificationNumberDocument: File | null = null;
  fileError: string | null = null;
  uploadedFiles: string | null = null;
  uploadedFilesTlnDocument: string | null = null;
  uploadedFilesVatIdDocument: string | null = null;
  uploadedFilesIdentificationNumberDocument: string | null = null;

  isLoading$ = new BehaviorSubject(false);

  primaryProducts$ = this.userRegistrationFormService
    .getprimaryProductOptions()
    .pipe(map((response) => response.primaryProducts || []));

  countries$ = this.userRegistrationFormService
    .getCountries()
    .pipe(map((response) => response.countries || []));

  regions!: IcommonIso[];

  communicationChannels$ = this.userRegistrationFormService
    .getcommunicationChannels()
    .pipe(map((response) => response.preferredCommunicationChannels || []));

  titles$: Observable<Title[]> = this.userRegistrationFormService.getTitles();

  gender$ = this.userRegistrationFormService
    .getGenderOptions()
    .pipe(map((response) => response.genders || []));

  nationality$ = this.userRegistrationFormService
    .getNationalities()
    .pipe(map((response) => response.nationalities || []));

  identityType$ = this.userRegistrationFormService
    .getIdentityType()
    .pipe(map((response) => response.identityTypes || []));

  tradeLicenseAuthority$ = this.userRegistrationFormService
    .getTradeLicenseAuthorityTypes()
    .pipe(map((response) => response.tradeLicenseAuthorityTypes || []));

  designation$ = this.userRegistrationFormService
    .getDesignationTypes()
    .pipe(map((response) => response.designationTypes || []));

  signupForm: FormGroup = this.userRegistrationFormService.form;

  maxFileSize = 1;

  todayDate: Date = new Date();
  minStartDate!: Date;
  maxStartDate!: Date;
  selectedStartDate!: Date | string;
  minEndDate!: Date;
  maxEndDate!: Date;
  selectedEndDate!: Date | string;
  protected subscriptions = new Subscription();
  private titleCodeSubscription: any;
  private genderSubscription: any;

  companyPhoneMinLength = 10;
  contactPersonPhoneMinLength = 10;

  constructor(
    private router: Router,
    protected globalMessageService: AdnocGlobalMessageService,
    private datePipe: DatePipe,
    @Inject(DOCUMENT) private document: Document
  ) {}

  ngOnInit(): void {
    this.document.body.classList.add('registartionPage', 'hide-header-footer');
    this.signupForm
      .get('companyAddressCountryIso')!
      .valueChanges.pipe(
        switchMap(() => {
          this.signupForm.get('companyAddressRegion')?.setValue(null);
          this.signupForm.get('companyAddressRegion')?.markAsUntouched();
          return this.userRegistrationFormService.getRegions();
        }),
        tap((regions) => {
          let regionArray = regions.regions;
          this.regions = regionArray;
        })
      )
      .subscribe();

    this.signupForm.get('identityType')?.valueChanges.subscribe((type) => {
      const control = this.signupForm.get('identificationNumber');

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

    this.userRegistrationFormService
      .getConfigvalue(FILEUPLOADMAXSIZE)
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

    // Set the minimum date to 100 years ago
    this.minStartDate = new Date(this.todayDate);
    this.minStartDate.setFullYear(this.todayDate.getFullYear() - 100); // Subtract 100 years

    this.todayDate.setHours(0, 0, 0, 0);
    this.maxStartDate = new Date(this.todayDate);

    // Initialize form for end date
    // Calculate the maxStartDate as yesterday
    const tomorrow = new Date(this.todayDate);
    tomorrow.setDate(this.todayDate.getDate() + 1);
    this.minEndDate = tomorrow; // Minimum end date is tomorrow
    this.maxEndDate = new Date(this.todayDate); // Set maxEndDate to 100 years from today
    this.maxEndDate.setFullYear(this.todayDate.getFullYear() + 100); // Add 100 years to today

    // Subscribe to titleCode changes
    this.titleCodeSubscription = this.signupForm
      .get('titleCode')
      ?.valueChanges.subscribe(() => {
        this.setGenderValue();
      });

    // Subscribe to gender changes
    this.genderSubscription = this.signupForm
      .get('gender')
      ?.valueChanges.subscribe(() => {
        this.checkTitleGenderCombination();
      });

    this.subscriptions.add(this.titleCodeSubscription);
    this.subscriptions.add(this.genderSubscription);

    this.setupPhoneValidationForCountry(
      'companyAddressCountryIso',
      'companyPhoneNumber',
      (length) => (this.companyPhoneMinLength = length)
    );

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
    this.signupForm
      .get(countryControlName)
      ?.valueChanges.subscribe((countryCode) => {
        const length = countryCode === 'AE' ? 9 : 10;
        updateMinLengthVar(length);
        const phoneValidators = [
          Validators.minLength(length),
          Validators.maxLength(length),
          Validators.pattern('^[0-9]*$'),
        ];

        // Phone number is required
        this.signupForm
          .get(phoneControlName)
          ?.setValidators([...phoneValidators]);
        this.signupForm.get(phoneControlName)?.updateValueAndValidity();
      });
  }

  filterInputAcceptAlphaNumeric(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^a-zA-Z0-9 ]/g, '');
  }

  filterInputAcceptNumbers(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^0-9]/g, '');
  }

  startDateFilter = (date: Date | null): boolean => {
    return date !== null && date <= this.maxStartDate; // Allow dates up to and including today for validFrom
  };

  // Custom date filter for validTo (restricts to dates up to 100 years from today)
  endDateFilter = (date: Date | null): boolean => {
    return date !== null && date >= this.minEndDate && date <= this.maxEndDate;
  };

  onStartDateChange(
    event: MatDatepickerInputEvent<Date>,
    controlName: string
  ): void {
    if (event.value) {
      const date = new Date(event.value); // Only create a date if value is not null
      this.selectedStartDate =
        this.datePipe.transform(date, "yyyy-MM-dd'T'HH:mm:ssZ") || '';
      this.signupForm?.get(controlName)?.setValue(this.selectedStartDate);
    } else {
      console.error('Selected date is null');
    }
  }

  // Method to handle end date change
  onEndDateChange(
    event: MatDatepickerInputEvent<Date>,
    controlName: string
  ): void {
    if (event.value) {
      const date = new Date(event.value);
      this.selectedEndDate =
        this.datePipe.transform(date, "yyyy-MM-dd'T'HH:mm:ssZ") || '';
      this.signupForm?.get(controlName)?.setValue(this.selectedEndDate);
    } else {
      console.error('Selected date is null');
    }
  }

  // Prevent form submission on Enter key press
  onKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      event.preventDefault();
    }
  }

  submit(): void {
    if (this.signupForm.valid) {
      this.isLoading$.next(true);
      this.subscriptions.add(
        this.userRegistrationFormService
          .registerUser(this.signupForm)
          .subscribe({
            complete: () => this.isLoading$.next(false),
            next: (res) => {
              sessionStorage.setItem(
                'registrationResponse',
                JSON.stringify(res)
              );
              this.router.navigate(['/registration-success']);
            },
            error: (error) => {
              this.isLoading$.next(false);
              let errorType = GlobalMessageType.MSG_TYPE_ERROR;
              this.globalMessageService.remove(
                GlobalMessageType.MSG_TYPE_ERROR
              );
              if (
                [
                  400, 401, 403, 404, 405, 408, 409, 410, 429, 500, 501, 502,
                  503, 504,
                ].includes(error.status) &&
                error.error?.errors?.length
              ) {
                let errorMessage: string = error.error.errors
                  .map((err: any) => err.message)
                  .join('\n');
                this.globalMessageService.add(errorMessage, errorType, 10000);
              } else {
                this.globalMessageService.add(
                  { key: 'userRegistrationForm.messageToFailedToRegister' },
                  errorType,
                  10000
                );
              }
            },
          })
      );
    } else {
      this.signupForm.markAllAsTouched();
      return;
    }
  }

  handleFileInput(event: Event): void {
    this.userRegistrationFormService.handleFileInput(event);
    const input = event.target as HTMLInputElement;
    const inputId = input.id;
    const fileName =
      input.files && input.files.length > 0
        ? Array.from(input.files)
            .map((file) => file.name)
            .join(', ')
        : null;
    switch (inputId) {
      case 'tlnDocument':
        this.uploadedFilesTlnDocument = fileName;
        break;
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
      this.signupForm.get(id)?.setErrors({ invalidFileType: true });
      return;
    }
    if (file.size > maxSize) {
      this.signupForm.get(id)?.setErrors({ fileTooLarge: true });
      return;
    }
    this.fileError = null;
    switch (id) {
      case 'tlnDocument':
        this.tlnDocument = file;
        break;
      case 'vatIdDocument':
        this.vatIdDocument = file;
        break;
      case 'identificationNumberDocument':
        this.identificationNumberDocument = file;
        break;
      default:
        this.selectedFile = file;
        break;
    }
  }

  setGenderValue() {
    const titleCode = this.signupForm.get('titleCode')?.value;
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
          this.signupForm.get('gender')?.setValue(defaultGenderCode);
        } else {
          this.signupForm.get('gender')?.reset();
        }
      });
  }

  checkTitleGenderCombination() {
    const titleCode = this.signupForm.get('titleCode')?.value;
    const gender = this.signupForm.get('gender')?.value;
    if (this.titles$ && titleCode) {
      this.titles$.subscribe((titles) => {
        const title = titles.find((t) => t.code === titleCode)?.name;
        if (
          (title === 'Mr.' && gender === 'FEMALE') ||
          (title === 'Ms.' && gender === 'MALE')
        ) {
          this.signupForm.get('gender')?.setErrors({ invalidGender: true });
        } else {
          this.signupForm.get('gender')?.setErrors(null);
        }
      });
    }
  }

  removeUploadedFile(fieldName: string) {
    switch (fieldName) {
      case 'tlnDocument':
        this.tlnDocument = null;
        this.uploadedFilesTlnDocument = null;
        this.signupForm.get('tlnDocument')?.reset();
        break;
      case 'vatIdDocument':
        this.vatIdDocument = null;
        this.uploadedFilesVatIdDocument = null;
        this.signupForm.get('vatIdDocument')?.reset();
        break;
      case 'identificationNumberDocument':
        this.identificationNumberDocument = null;
        this.uploadedFilesIdentificationNumberDocument = null;
        this.signupForm.get('identificationNumberDocument')?.reset();
        break;
      default:
        this.selectedFile = null;
        this.uploadedFiles = null;
        this.signupForm.get('otherDocument')?.reset();
        break;
    }
  }

  resetIdentificationFields() {
    this.removeUploadedFile('identificationNumberDocument');
    this.signupForm.get('identificationNumber')?.reset();
    this.signupForm.get('identificationValidFrom')?.reset();
    this.signupForm.get('identificationValidTo')?.reset();
  }

  onTradeLicenseInput(event: any): void {
    const inputElement = event.target;
    let rawValue = inputElement.value;
    const originalCursor = inputElement.selectionStart;
    // Remove hyphen and non-alphanumerics, convert to uppercase
    let cleanValue = rawValue.replace(/[^a-zA-Z0-9]/g, '').toUpperCase();
    // Separate first 2 letters and up to 7 digits
    const letters = cleanValue.substring(0, 2).replace(/[^A-Z]/g, '');
    const digits = cleanValue
      .substring(2)
      .replace(/[^0-9]/g, '')
      .substring(0, 7);

    let formatted = letters;
    if (letters.length === 2) {
      formatted += '-' + digits;
    }
    // Limit to max 10 characters
    formatted = formatted.substring(0, 10);

    // Set the new value
    inputElement.value = formatted;
    this.signupForm
      .get('tradeLicenseNumber')
      ?.setValue(formatted, { emitEvent: false });

    // Fix cursor position so deleting works properly
    // Delay with setTimeout so cursor updates after DOM change
    setTimeout(() => {
      if (originalCursor <= 2) {
        inputElement.setSelectionRange(originalCursor, originalCursor);
      } else if (originalCursor === 3) {
        inputElement.setSelectionRange(4, 4); // skip over hyphen
      } else {
        inputElement.setSelectionRange(formatted.length, formatted.length);
      }
    });
  }

  onIdentificationNumberInput(event: any): void {
    const inputElement = event.target as HTMLInputElement;
    const identityType = this.signupForm.get('identityType')?.value;

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
      this.signupForm
        .get('identificationNumber')
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
      this.signupForm
        .get('identificationNumber')
        ?.setValue(cleaned, { emitEvent: false });

      // Restore cursor to end (simple approach)
      setTimeout(() => {
        const len = inputElement.value.length;
        inputElement.setSelectionRange(len, len);
      }, 0);
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.unsubscribe();
    this.document.body.classList.remove(
      'registartionPage',
      'hide-header-footer'
    );
  }
}
