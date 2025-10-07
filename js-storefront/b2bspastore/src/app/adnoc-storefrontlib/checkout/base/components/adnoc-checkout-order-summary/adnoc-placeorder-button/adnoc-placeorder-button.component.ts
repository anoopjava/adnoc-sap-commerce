import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { CheckoutPaymentTypeService } from '../../../../b2b/core/facade/checkout-payment-type.service';
import { distinctUntilChanged, Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'adnoc-placeorder-button',
  templateUrl: './adnoc-placeorder-button.component.html',
  styleUrl: './adnoc-placeorder-button.component.scss',
  standalone: false,
})
export class AdnocPlaceorderButtonComponent {
  protected checkoutPaymentTypeService = inject(CheckoutPaymentTypeService);
  protected readonly destroy$ = new Subject<void>();
  isbtnDisabled = true; // Default to disabled
  errorMsg = '';

  constructor(protected cd: ChangeDetectorRef) {
    this.checkoutPaymentTypeService.validatePaymentTypes$
      .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.isbtnDisabled = !data.isValid;
          this.errorMsg = data.error;
          this.cd.markForCheck();
        },
      });
  }

  back() {
    this.checkoutPaymentTypeService.proceedToBack$.next(true);
  }

  next() {
    this.checkoutPaymentTypeService.proceedToNext$.next(true);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
