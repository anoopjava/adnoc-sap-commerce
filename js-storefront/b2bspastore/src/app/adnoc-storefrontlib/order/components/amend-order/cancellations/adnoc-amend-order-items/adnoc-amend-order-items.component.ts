import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Input,
  OnDestroy,
  AfterViewInit,
  ViewEncapsulation,
} from '@angular/core';
import {
  UntypedFormControl,
  UntypedFormGroup,
  ValidationErrors,
} from '@angular/forms';
import {
  distinctUntilChanged,
  map,
  Observable,
  shareReplay,
  Subject,
  takeUntil,
} from 'rxjs';
import { Price } from '@spartacus/core';
import { AdnocOrderEntry } from '../../../../../../core/model/adnoc-cart.model';
import { AdnocOrderAmendService } from '../../adnoc-amend-order.service';
import { IReturnReason } from '../../../../../../core/model/adnoc-users.model';

const cxNoSelectedItemToCancel = {
  cxNoSelectedItemToCancel: true,
};
const cxNoSelectedCancelReason = {
  cxNoSelectedCancelReason: true,
};
const cxNoSelectedReturnReason = {
  cxNoSelectedReturnReason: false,
};
@Component({
  selector: 'cx-amend-order-items',
  templateUrl: './adnoc-amend-order-items.component.html',
  styleUrls: ['./adnoc-amend-order-items.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
  host: { class: 'adnoc-amend-order-items' },
  standalone: false,
})
export class AdnocsCancelOrReturnItemsComponent
  implements AfterViewInit, OnDestroy
{
  @Input() entries!: AdnocOrderEntry[];
  @Input() isConfirmation = false;
  @Input() orderNumber!: string;
  @Input() isCancelOrder = false;
  cancellableItemsCount: number = 0;
  form$: Observable<UntypedFormGroup>;
  forms!: UntypedFormGroup;
  cancelReasons$!: Observable<IReturnReason[]>;

  protected destroy$ = new Subject<void>();

  constructor(
    protected orderAmendService: AdnocOrderAmendService,
    protected cdr: ChangeDetectorRef
  ) {
    this.form$ = this.orderAmendService.getForm();
    this.cancelReasons$ = this.orderAmendService.getCancelReason().pipe(
      map((data) => data.cacelReasons),
      shareReplay(1)
    );
  }

  ngOnInit(): void {
    // Calculate cancellable items count when the component initializes
    this.entries = this.entries.filter((item) => item.isCancellable === true);
    this.cancellableItemsCount = this.entries.filter(
      (entry: any) => entry.isCancellable === true
    ).length;
  }

  getControl(form: UntypedFormGroup, entry: AdnocOrderEntry) {
    this.forms = form;
    const control = <UntypedFormControl>(
      form.get('entries')?.get(entry.entryNumber?.toString() ?? '')
    );

    const cancelReasonControl = <UntypedFormControl>(
      form.get('cancelReason')?.get(entry.entryNumber?.toString() ?? '')
    );
    return { control, cancelReasonControl };
  }

  setAll(form: UntypedFormGroup): void {
    this.entries.forEach((entry) => {
      this.getControl(form, entry).control.setValue(
        this.getCancelMaxAmendQuantity(entry)
      );
    });
  }

  getItemPrice(entry: AdnocOrderEntry): Price {
    return this.orderAmendService.getAmendedPrice(entry);
  }

  getMaxAmendQuantity(entry: AdnocOrderEntry) {
    return this.orderAmendService.getMaxAmendQuantity(entry);
  }

  getCancelMaxAmendQuantity(entry: AdnocOrderEntry) {
    return (entry as any)?.isCancellable ? entry?.quantityPending ?? 0 : 0;
  }

  isCancellation() {
    return this.orderAmendService.isCancellation();
  }

  ngAfterViewInit(): void {
    const entriesControl = this.forms.get('returnReason');
    this.clearControlError(entriesControl as UntypedFormControl);
    this.forms
      .get('cancelReason')
      ?.valueChanges.pipe(distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => {
        const isValid = this.orderAmendService.validateObjects(this.forms);

        if (!isValid) {
          const entriesControl = this.forms.get('entries');
          this.setControlError(
            entriesControl as UntypedFormControl,
            cxNoSelectedItemToCancel
          );
          this.forms.invalid;
        } else {
          const entriesControl = this.forms.get('entries');
          this.clearControlError(entriesControl as UntypedFormControl);
        }
        this.cdr.detectChanges();
      });

    this.forms
      .get('entries')
      ?.valueChanges.pipe(distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => {
        const isValid = this.orderAmendService.validateObjects(this.forms);

        if (!isValid) {
          const entriesControl = this.forms.get('cancelReason');
          if (entriesControl) {
            this.setControlError(
              entriesControl as UntypedFormControl,
              cxNoSelectedCancelReason
            );
          }
        } else {
          const entriesControl = this.forms.get('cancelReason');
          this.clearControlError(entriesControl as UntypedFormControl);
        }
        this.cdr.detectChanges();
      });
  }

  setControlError(control: UntypedFormControl, error: ValidationErrors): void {
    control.setErrors(error);
  }

  clearControlError(control: UntypedFormControl): void {
    control.setErrors(null);
  }

  onCheckboxChange(event: Event, item: any, form: UntypedFormGroup): void {
    const checkbox = event.target as HTMLInputElement;
    const control = this.getControl(form, item).control;
    const cancelReasonGroup = this.forms.get('cancelReason');
    const entryKey = item.entryNumber?.toString() ?? '';
    if (checkbox.checked) {
      control.setValue(this.getCancelMaxAmendQuantity(item));
    } else {
      if (cancelReasonGroup) {
        if (cancelReasonGroup.get(entryKey)) {
          cancelReasonGroup.get(entryKey)?.setValue('');
        }
      }
      control.setValue(0);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
