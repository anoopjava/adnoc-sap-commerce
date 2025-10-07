/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  Optional,
  inject,
} from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { CartOutlets, DeliveryMode } from '@spartacus/cart/base/root';
import { FeatureConfigService, GlobalMessageType } from '@spartacus/core';
import { BehaviorSubject, Observable, combineLatest } from 'rxjs';
import {
  distinctUntilChanged,
  filter,
  map,
  take,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { CheckoutConfigService } from '../services/checkout-config.service';
import { CheckoutStepService } from '../services/checkout-step.service';
import { AdnocActiveCartFacade } from '../../../../cart/base/root/facade/adnoc-active-cart.facade';
import { CheckoutDeliveryModesFacade } from '../../root/facade/checkout-delivery-modes.facade';
import { AdnocGlobalMessageService } from '../../../../../core/global-message/facade/adnoc-global-message.service';

@Component({
  selector: 'cx-delivery-mode',
  templateUrl: './checkout-delivery-mode.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class CheckoutDeliveryModeComponent {
  protected globalMessageService = inject(AdnocGlobalMessageService);
  protected busy$ = new BehaviorSubject(false);
  protected readonly isSetDeliveryModeHttpErrorSub = new BehaviorSubject(false);

  readonly CartOutlets = CartOutlets;

  @Optional() featureConfigService = inject(FeatureConfigService, {
    optional: true,
  });

  isSetDeliveryModeHttpError$ =
    this.isSetDeliveryModeHttpErrorSub.asObservable();

  selectedDeliveryModeCode$: Observable<string | undefined>;

  supportedDeliveryModes$: Observable<DeliveryMode[]>;

  backBtnText: string;

  mode: UntypedFormGroup;

  isUpdating$: Observable<boolean>;

  get deliveryModeInvalid(): boolean {
    return this.mode.controls['deliveryModeId'].invalid;
  }

  constructor(
    protected fb: UntypedFormBuilder,
    protected checkoutConfigService: CheckoutConfigService,
    protected activatedRoute: ActivatedRoute,
    protected checkoutStepService: CheckoutStepService,
    protected checkoutDeliveryModesFacade: CheckoutDeliveryModesFacade,
    protected activeCartFacade: AdnocActiveCartFacade
  ) {
    this.mode = this.fb.group({
      deliveryModeId: ['', Validators.required],
    });
    this.selectedDeliveryModeCode$ = this.checkoutDeliveryModesFacade
      .getSelectedDeliveryModeState()
      .pipe(
        filter((state) => !state.loading),
        map((state) => state.data),
        map((deliveryMode) => deliveryMode?.code)
      );

    this.supportedDeliveryModes$ = this.checkoutDeliveryModesFacade
      .getSupportedDeliveryModes()
      .pipe(
        filter((deliveryModes) => !!deliveryModes?.length),
        withLatestFrom(this.selectedDeliveryModeCode$),
        tap(([deliveryModes, code]) => {
          if (
            !code ||
            !deliveryModes.find((deliveryMode) => deliveryMode.code === code)
          ) {
            code =
              this.checkoutConfigService.getPreferredDeliveryMode(
                deliveryModes
              );
          }
          if (code) {
            this.mode.controls['deliveryModeId'].setValue(code);
            this.changeMode(code);
          }
        }),
        map(([deliveryModes]) =>
          deliveryModes.filter((mode) => mode.code !== 'pickup')
        )
      );

    this.backBtnText = this.checkoutStepService.getBackBntText(
      this.activatedRoute
    );
    this.isUpdating$ = combineLatest([
      this.busy$,
      this.checkoutDeliveryModesFacade
        .getSelectedDeliveryModeState()
        .pipe(map((state) => state.loading)),
    ]).pipe(
      map(([busy, loading]) => busy || loading),
      distinctUntilChanged()
    );
  }

  changeMode(code: string | undefined, event?: Event): void {
    if (!code) {
      return;
    }
    const lastFocusedId = (<HTMLElement>event?.target)?.id;
    this.busy$.next(true);
    this.checkoutDeliveryModesFacade.setDeliveryMode(code).subscribe({
      complete: () => this.onSuccess(),
      error: () => this.onError(),
    });

    // TODO: (CXSPA-6599) - Remove feature flag next major release
    if (this.featureConfigService?.isEnabled('a11yCheckoutDeliveryFocus')) {
      const isTriggeredByKeyboard = (<MouseEvent>event)?.screenX === 0;
      if (isTriggeredByKeyboard) {
        this.restoreFocus(lastFocusedId, code);
        return;
      }
      this.mode.setValue({ deliveryModeId: code });
    }
  }

  next(): void {
    this.checkoutStepService.next(this.activatedRoute);
  }

  back(): void {
    this.checkoutStepService.back(this.activatedRoute);
  }

  getAriaChecked(code: string | undefined): boolean {
    return code === this.mode.controls['deliveryModeId'].value;
  }

  protected onSuccess(): void {
    this.isSetDeliveryModeHttpErrorSub.next(false);
    this.busy$.next(false);
  }

  protected onError(): void {
    this.globalMessageService?.add(
      { key: 'setDeliveryMode.unknownError' },
      GlobalMessageType.MSG_TYPE_ERROR
    );

    this.isSetDeliveryModeHttpErrorSub.next(true);
    this.busy$.next(false);
  }

  /**
   * Restores focus after data is updated.
   */
  protected restoreFocus(lastFocusedId: string, code: string): void {
    this.isUpdating$
      .pipe(
        filter((isUpdating) => !isUpdating),
        take(1)
      )
      .subscribe(() => {
        setTimeout(() => {
          document.querySelector('main')?.classList.remove('mouse-focus');
          this.mode.setValue({ deliveryModeId: code });
          document.getElementById(lastFocusedId)?.focus();
        }, 0);
      });
  }
}
