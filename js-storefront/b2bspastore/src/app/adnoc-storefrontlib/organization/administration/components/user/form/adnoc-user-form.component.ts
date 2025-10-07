import {
  ChangeDetectionStrategy,
  Component,
  inject,
  Input,
  OnDestroy,
  OnInit,
} from '@angular/core';
import {
  UntypedFormArray,
  UntypedFormControl,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { B2BUser, B2BUserRight, Title } from '@spartacus/core';
import {
  CurrentItemService,
  CurrentUserService,
} from '@spartacus/organization/administration/components';
import {
  B2BUnitNode,
  OrgUnitService,
} from '@spartacus/organization/administration/core';
import { UserProfileFacade } from '@spartacus/user/profile/root';
import { MatDateFormats } from '@angular/material/core';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';
import { Observable, of, Subscription } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { AdnocUserFormService } from './adnoc-user-form.service';
import { ItemService } from '../../shared/item.service';
import { AdnocUserItemService } from '../services/adnoc-user-item.service';
import { DatePipe } from '@angular/common';
import {
  AdnocConfigRoot,
  Icommon,
  IcommonIso,
} from '../../../../../services/apiServices/api-response.model';
import { FILEUPLOADMAXSIZE } from '../../../../../constants/adnoc-user-account-constants';
import { AdnocCustomFormService } from '../../shared/adnoc-form/adnoc-custom-form.service';
import { MatDatepickerInputEvent } from '@angular/material/datepicker';
import { B2BUserService } from '../services/b2b-user.service';
import { B2BUserRole } from '../../../../../../core/src/model/org-unit.model';

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
  selector: 'cx-org-user-form',
  templateUrl: './adnoc-user-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'content-wrapper adnoc-org-user-form' },
  providers: [
    {
      provide: ItemService,
      useExisting: AdnocUserItemService,
    },
    {
      provide: CurrentItemService,
      useExisting: CurrentUserService,
    },
    provideMomentDateAdapter(CUSTOM_DATE_FORMATS),
    DatePipe,
  ],
  standalone: false,
})
export class AdnocUserFormComponent implements OnInit, OnDestroy {
  protected itemService = inject(ItemService<B2BUser>);
  form: UntypedFormGroup | null = this.itemService.getForm();

  todayDate: Date = new Date();
  minStartDate!: Date;
  maxStartDate!: Date;
  selectedStartDate: Date | string | undefined;
  minEndDate!: Date;
  maxEndDate!: Date;
  selectedEndDate: Date | string | undefined;
  /**
   * Initialize the business unit for the user.
   *
   * If there's a unit provided, we disable the unit form control.
   */
  @Input() set unitKey(value: string | null) {
    if (value) {
      this.form?.get('orgUnit.uid')?.setValue(value);
      this.form?.get('orgUnit')?.disable();
    }
  }

  units$: Observable<B2BUnitNode[] | undefined> | undefined;

  titles$: Observable<Title[]> | undefined;

  gender$: Observable<any> | undefined;
  nationality$: Observable<any> | undefined;
  identityType$: Observable<any> | undefined;
  communicationChannels$: Observable<any> | undefined;
  designation$: Observable<Icommon[]> | undefined;
  countries$: Observable<IcommonIso[]> | undefined;

  regions: IcommonIso[] | undefined;
  maxFileSize = 1;
  fileError = '';
  uploadedFiles: string | null = null;
  identificationNumberDocument: File | null = null;
  uploadedFilesIdentificationNumberDocument: string | null = null;

  availableRoles: B2BUserRole[] | undefined;
  availableRights: B2BUserRight[] | undefined;
  private subscription: Subscription = new Subscription(); // To hold the subscriptions
  private titleCodeSubscription: any;
  private genderSubscription: any;
  contactPersonPhoneMinLength = 10;

  constructor(
    protected unitService: OrgUnitService,
    protected userProfileFacade: UserProfileFacade,
    protected b2bUserService: B2BUserService,
    protected userFormService: AdnocUserFormService,
    protected adnocCustomFormService: AdnocCustomFormService,
    private datePipe: DatePipe
  ) {}

  ngOnInit(): void {
    this.units$ = this.unitService.getActiveUnitList().pipe(
      tap((units) => {
        if (units && units.length === 1) {
          this.form?.get('orgUnit.uid')?.setValue(units[0]?.id);
        }
      })
    );

    this.titles$ = this.userProfileFacade.getTitles();

    this.gender$ = this.userFormService
      .getGenderOptions()
      .pipe(map((response) => response.genders || []));

    this.nationality$ = this.userFormService
      .getNationalities()
      .pipe(map((response) => response.nationalities || []));

    this.identityType$ = this.userFormService
      .getIdentityType()
      .pipe(map((response) => response.identityTypes || []));

    this.communicationChannels$ = this.userFormService
      .getcommunicationChannels()
      .pipe(map((response) => response.preferredCommunicationChannels || []));

    this.designation$ = this.userFormService
      .getDesignationTypes()
      .pipe(map((response) => response.designationTypes || []));

    this.countries$ = this.userFormService
      .getCountries()
      .pipe(map((response) => response.countries || []));

    this.availableRoles = this.b2bUserService.getAllRoles();
    this.availableRights = this.b2bUserService.getAllRights();

    this.unitService.loadList();
    // Set the minimum date to 100 years ago
    this.minStartDate = new Date(this.todayDate);
    this.minStartDate.setFullYear(this.todayDate.getFullYear() - 100); // Subtract 100 years

    this.todayDate.setHours(0, 0, 0, 0);
    this.maxStartDate = new Date(this.todayDate);

    // Initialize form for end date
    const tomorrow = new Date(this.todayDate);
    tomorrow.setDate(this.todayDate.getDate() + 1);
    this.minEndDate = tomorrow;
    this.maxEndDate = new Date(this.todayDate); // Set maxEndDate to 100 years from today
    this.maxEndDate.setFullYear(this.todayDate.getFullYear() + 100); // Add 100 years to today

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

    // Add the individual subscription to the subscription object
    this.subscription.add(this.titleCodeSubscription);
    this.subscription.add(this.genderSubscription);

    this.form
      ?.get('companyAddressCountryIso')!
      .valueChanges.pipe(
        switchMap(() => {
          this.form?.get('companyAddressRegion')?.setValue(null);
          this.form?.get('companyAddressRegion')?.markAsUntouched();
          return this.userFormService.getRegions();
        }),
        tap((regions) => {
          let regionArray = regions.regions;
          this.regions = regionArray;
        })
      )
      .subscribe();
    this.userFormService.getConfigvalue(FILEUPLOADMAXSIZE).subscribe(
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
        this.form?.get(phoneControlName)?.setValidators([...phoneValidators]);
        this.form?.get(phoneControlName)?.updateValueAndValidity();
      });
  }

  updateRoles(event: MouseEvent) {
    const { checked, value } = event.target as HTMLInputElement;
    if (checked) {
      this.roles.push(new UntypedFormControl(value));
    } else {
      this.roles.removeAt(this.roles.value.indexOf(value));
    }
  }

  selectSingleRole(selectedRole: B2BUserRole): void {
    this.roles.clear(); // remove all selected roles
    this.roles.push(new UntypedFormControl(selectedRole)); // add the selected one
  }

  get roles(): UntypedFormArray {
    return this.form?.get('roles') as UntypedFormArray;
  }

  get isAssignedToApprovers(): UntypedFormControl {
    return this.form?.get('isAssignedToApprovers') as UntypedFormControl;
  }

  filterInputAcceptAlphaNumeric(event: Event): void {
    const input = event.target as HTMLInputElement;
    input.value = input.value.replace(/[^a-zA-Z0-9 ]/g, '');
  }

  filterInputPhoneNumber(event: Event): void {
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

  onStartDateChange(event: MatDatepickerInputEvent<Date>): void {
    const date = event.value;
    this.selectedStartDate =
      this.datePipe.transform(date, "yyyy-MM-dd'T'HH:mm:ssZ") || '';
    this.form?.get('identificationValidFrom')?.setValue(this.selectedStartDate);
  }
  // Method to handle end date change
  onEndDateChange(event: MatDatepickerInputEvent<Date>): void {
    const date = event.value;
    this.selectedEndDate =
      this.datePipe.transform(date, "yyyy-MM-dd'T'HH:mm:ssZ") || '';
    this.form?.get('identificationValidTo')?.setValue(this.selectedEndDate);
  }

  setGenderValue() {
    const titleCode = this.form?.get('titleCode')?.value;
    if (this.gender$) {
      this.gender$
        .pipe(
          switchMap((genders) => {
            let defaultGenderCode: string | null = null;
            switch (titleCode) {
              case '0001':
                defaultGenderCode =
                  genders.find(
                    (gender: { code: string }) => gender.code === 'FEMALE'
                  )?.code || null;
                break;
              case '0002':
                defaultGenderCode =
                  genders.find(
                    (gender: { code: string }) => gender.code === 'MALE'
                  )?.code || null;
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

  resetIdentificationFields() {
    this.form?.get('identificationNumber')?.reset();
    this.form?.get('identificationValidFrom')?.reset();
    this.form?.get('identificationValidTo')?.reset();
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
    if (inputId == 'identificationNumberDocument') {
      this.uploadedFilesIdentificationNumberDocument = fileName;
    } else {
      this.uploadedFiles = fileName;
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
    this.adnocCustomFormService.setIdentificationNumberDocumentFile(file);
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

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
