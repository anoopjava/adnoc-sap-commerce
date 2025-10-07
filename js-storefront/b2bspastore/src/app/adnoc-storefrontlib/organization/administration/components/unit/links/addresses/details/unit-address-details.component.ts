/*
 * SPDX-FileCopyrightText: 2024 SAP Spartacus team <spartacus-team@sap.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Address, B2BUnit, Country, UserAddressService } from '@spartacus/core';
import { Observable } from 'rxjs';
import {
  map,
  shareReplay,
  switchMap,
  tap,
  withLatestFrom,
} from 'rxjs/operators';
import { ItemService } from '../../../../shared/item.service';
import { UnitAddressItemService } from '../services/unit-address-item.service';
import { AdnocCurrentUnitService } from '../../../services/adnoc-current-unit.service';

@Component({
    selector: 'cx-org-unit-address-details',
    templateUrl: './unit-address-details.component.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    host: { class: 'content-wrapper' },
    providers: [
        {
            provide: ItemService,
            useExisting: UnitAddressItemService,
        },
    ],
    standalone: false
})
export class UnitAddressDetailsComponent {
  unit$: Observable<B2BUnit | undefined>;

  model$: Observable<Address>;

  getCountry(isoCode: string | undefined): Observable<Country | undefined> {
    return this.userAddressService.getDeliveryCountries().pipe(
      tap((countries: Country[]) => {
        if (Object.keys(countries).length === 0) {
          this.userAddressService.loadDeliveryCountries();
        }
      }),
      map((countries) =>
        countries.find((country) => country.isocode === isoCode)
      )
    );
  }

  constructor(
    protected itemService: ItemService<Address>,
    protected currentUnitService: AdnocCurrentUnitService,
    protected userAddressService: UserAddressService
  ) {
    this.unit$ = this.currentUnitService.item$;
    this.model$ = this.itemService.key$.pipe(
      withLatestFrom(this.unit$),
      switchMap(([code, unit]) => this.itemService.load(unit?.uid, code)),
      shareReplay({ bufferSize: 1, refCount: true })
    );
  }
}
