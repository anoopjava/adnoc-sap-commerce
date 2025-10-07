/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Input,
  OnDestroy,
  OnInit,
  ViewEncapsulation,
} from '@angular/core';
import { FormGroup, UntypedFormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { FocusConfig, LaunchDialogService } from '@spartacus/storefront';
import { AdnocOtploginComponentService } from '../adnoc-otp-login/adnoc-otp-login-service';
import { distinctUntilChanged, Subject, takeUntil } from 'rxjs';
import { payerInfo } from '../../../core/model/adnoc-cart.model';
import { AdnocActiveCartService } from '../../cart/base/core/facade/adnoc-active-cart.service';
import { TranslationService, WindowRef } from '@spartacus/core';
import { AdnocUserCreditOrderInfoComponentService } from '../../user/profile/components/adnoc-user-credit-order-info/adnoc-user-credit-order-info.component.service';

@Component({
  selector: 'adnoc-suggested-payers-dialog',
  templateUrl: './adnoc-suggested-payers-dialog.component.html',
  styleUrl: './adnoc-suggested-payer-dialog.scss',
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  standalone: false,
})
export class AdnocSuggestedPayersDialogComponent implements OnInit, OnDestroy {
  focusConfig: FocusConfig = {
    trap: true,
    block: true,
    autofocus: false,
    focusOnEscape: false,
    lock: true,
    disableMouseFocus: true,
  };

  destroy$ = new Subject<void>();
  @Input() payers!: payerInfo[];
  form!: FormGroup;
  selectedPayerObject!: any;
  isDisabled: boolean = true;
  selectedPayerId = '';
  isError: string = '';

  constructor(
    protected launchDialogService: LaunchDialogService,
    protected el: ElementRef,
    protected router: Router,
    protected fb: UntypedFormBuilder,
    protected payerService: AdnocOtploginComponentService,
    protected createPayerService: AdnocActiveCartService,
    protected translationService: TranslationService,
    protected adnocUserCreditOrderInfoService: AdnocUserCreditOrderInfoComponentService,
    protected winRef: WindowRef
  ) {}

  ngOnInit(): void {
    this.payerService.payerInfo$
      .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.payers = data;
          this.isError = '';
        },
        error: (err) => {
          this.isError = err?.error?.errors[0]?.message;
        },
      });
  }

  closeModal(reason: string): void {
    this.launchDialogService.closeDialog(reason);
    this.router.navigateByUrl('/logout');
  }

  onPayerSubmit(): void {
    this.createPayerService
      .createPayer(this.selectedPayerId)
      .pipe(distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.isError = '';
        },
        complete: () => {
          const window = this.winRef.nativeWindow;
          this.adnocUserCreditOrderInfoService.notifyPayerUpdated();
          this.launchDialogService.closeDialog('Close Dialog');
          if (window) {
            window.location.reload();
          }
        },
        error: (err: any) => {
          this.translationService
            .translate('common.suggesstedPayer.serviceError')
            .subscribe((translation) => {
              this.isError = err?.error?.errors[0]?.message || translation;
            });
        },
      });
  }

  onPayerIdChange(event: Event): void {
    this.selectedPayerId = (event.target as HTMLInputElement).value;
    this.selectedPayerObject = this.payers.filter(
      (payer) => payer.uid === this.selectedPayerId || null
    );
    this.isDisabled = this.selectedPayerId ? false : true;
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
