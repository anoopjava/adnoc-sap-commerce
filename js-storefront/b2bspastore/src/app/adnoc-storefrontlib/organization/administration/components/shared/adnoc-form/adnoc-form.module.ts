import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { I18nModule } from '@spartacus/core';
import { AdnocFormComponent } from './adnoc-form.component';
import { KeyboardFocusModule } from '@spartacus/storefront';
import { ItemActiveModule } from '../item-active.module';
import { AdnocCardModule } from '../adnoc-card';
import { MessageService } from '../message/service/message.service';
import { MessageModule } from '../message/message.module';
import { SpinnerModule } from '@spartacus/storefront';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    I18nModule,
    RouterModule,
    AdnocCardModule,
    MessageModule,
    ItemActiveModule,
    KeyboardFocusModule,
    SpinnerModule,
  ],
  declarations: [AdnocFormComponent],
  providers: [MessageService],
  exports: [AdnocFormComponent],
})
export class AdnocFormModule {}
