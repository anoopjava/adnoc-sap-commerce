import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Input,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';
import { EMPTY, Observable } from 'rxjs';
import { catchError, first, map, switchMap, take } from 'rxjs/operators';
import { LoadStatus } from '@spartacus/organization/administration/core';
import { GlobalMessageType } from '@spartacus/core';
import { AdnocCardComponent } from '../adnoc-card';
import { ItemService } from '../item.service';
import { AdnocCustomFormService } from './adnoc-custom-form.service';
import { MessageService } from '../message/service/message.service';
import { AdnocGlobalMessageService } from '../../../../../../core/global-message/facade/adnoc-global-message.service';

const DISABLED_STATUS = 'DISABLED';

/**
 * Reusable component for creating and editing organization items. The component does not
 * know anything about form specific.
 */
@Component({
  selector: 'cx-org-form',
  templateUrl: './adnoc-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'content-wrapper adnoc-org-form' },
  standalone: false,
})
export class AdnocFormComponent<T> implements OnInit, OnDestroy {
  /**
   * i18n root for all localizations. The i18n root key is suffixed with
   * either `.edit` or `.create`, depending on the usage of the component.
   */
  @Input()
  i18nRoot!: string;

  @Input() animateBack = true;
  @Input() subtitle?: string;

  /**
   * i18n key for the localizations.
   */
  i18n!: string;

  form$: Observable<UntypedFormGroup | null>;

  /**
   * To handle the case of receiving a negative response during creation an item
   */
  disabled$: Observable<boolean>;
  formSubmitted: boolean = false;

  // Add ViewChild to access the CardComponent
  @ViewChild(AdnocCardComponent) cardComponent!: AdnocCardComponent<any>;

  constructor(
    protected itemService: ItemService<T>,
    protected messageService: MessageService,
    private router: Router,
    protected adnocCustomFormService: AdnocCustomFormService,
    protected globalMessageService: AdnocGlobalMessageService,
    private cdr: ChangeDetectorRef
  ) {
    this.form$ = this.itemService.current$.pipe(
      map((item) => {
        this.setI18nRoot(item);

        if (!item) {
          // we trick the form builder...
          item = {} as any;
        }
        return this.itemService.getForm(item);
      })
    );

    this.disabled$ = this.form$.pipe(
      switchMap((form) => form?.statusChanges ?? EMPTY),
      map((status) => status === DISABLED_STATUS)
    );
  }

  // Prevent form submission on Enter key press
  onKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      event.preventDefault();
    }
  }

  save(form: UntypedFormGroup): void {
    const currentUrl = this.router.url;
    if (currentUrl.includes('units/create')) {
      if (form.invalid) {
        form.markAllAsTouched();
      } else {
        this.formSubmitted = true;
        this.adnocCustomFormService
          .registerPayerorShiptoaddress(form)
          .pipe(
            catchError((error) => {
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
                  { key: 'shared.error.messageToFailedToRegister' },
                  errorType,
                  10000
                );
              }
              this.formSubmitted = false;
              this.cdr.detectChanges();
              return EMPTY;
            })
          )
          .subscribe({
            next: (res) => {
              let popupMessage = '';
              let messageType = GlobalMessageType.MSG_TYPE_CONFIRMATION;
              popupMessage = 'orgUnit.unitRegistration.successMessage';
              this.globalMessageService.add(
                {
                  key: popupMessage,
                  params: { registrationRequestId: res?.registrationRequestId },
                },
                messageType,
                60000
              );
              this.formSubmitted = false;
              this.router.navigate(['/organization/units']);
            },
          });
      }
    } else if (currentUrl.includes('organization/users/create')) {
      if (form.invalid) {
        form.markAllAsTouched();
      } else {
        this.formSubmitted = true;
        this.adnocCustomFormService
          .createSubUser(form)
          .pipe(
            catchError((error) => {
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
              } else if (error.status == 409) {
                const params = { code: form.value.email };
                this.globalMessageService.add(
                  {
                    key: 'organizationTranslation.httpHandlers.conflict.user',
                    params,
                  },
                  errorType,
                  10000
                );
              } else {
                this.globalMessageService.add(
                  { key: 'shared.error.messageToFailedToRegister' },
                  errorType,
                  10000
                );
              }
              this.formSubmitted = false;
              this.cdr.detectChanges();
              return EMPTY;
            })
          )
          .subscribe({
            next: (response) => {
              let url = 'organization/users/' + response.customerId;
              this.formSubmitted = false;
              this.router.navigate([url]);
            },
          });
      }
    } else {
      this.itemService.key$
        .pipe(
          first(),
          switchMap((key) =>
            this.itemService.save(form, key).pipe(
              take(1),
              map((data) => ({
                item: data.item,
                status: data.status,
                action: key ? 'update' : 'create',
              }))
            )
          )
        )
        .subscribe(({ item, action, status }) => {
          if (status === LoadStatus.SUCCESS) {
            this.itemService.launchDetails(item);
            this.notify(item, action);
          }
          form.enable();
        });
    }
  }

  protected notify(item: T | undefined, action: string) {
    this.messageService.add({
      message: {
        key: `${this.i18nRoot}.messages.${action}`,
        params: {
          item,
        },
      },
    });
  }

  protected setI18nRoot(item?: T): void {
    // concatenate the i18n root with .edit or .create suffix
    this.i18n = this.i18nRoot + (item ? '.edit' : '.create');
  }

  back(event: MouseEvent) {
    if (this.animateBack && this.cardComponent) {
      this.cardComponent.closeView(event);
    }
  }

  get isCreateRoute(): boolean {
    return this.router.url.includes('users/create');
  }

  ngOnInit() {
    this.itemService.setEditMode(true);
  }

  ngOnDestroy() {
    this.itemService.setEditMode(false);
  }
}
