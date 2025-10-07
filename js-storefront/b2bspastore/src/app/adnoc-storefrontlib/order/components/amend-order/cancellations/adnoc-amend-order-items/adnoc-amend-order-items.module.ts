import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { I18nModule } from '@spartacus/core';
import {
  FormErrorsModule,
  ItemCounterModule,
  MediaModule,
} from '@spartacus/storefront';
import { AdnocsCancelOrReturnItemsComponent } from './adnoc-amend-order-items.component';

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    I18nModule,
    MediaModule,
    ItemCounterModule,
    FormErrorsModule,
  ],
  declarations: [AdnocsCancelOrReturnItemsComponent],
  exports: [AdnocsCancelOrReturnItemsComponent],
})
export class AdnocAmendOrderItemsModule {}
