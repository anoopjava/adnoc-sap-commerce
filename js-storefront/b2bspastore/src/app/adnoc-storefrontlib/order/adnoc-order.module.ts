import { NgModule } from '@angular/core';
import { OrderCoreModule } from '@spartacus/order/core';
import { AdnocOrderOccModule } from './occ/order-occ.module'; 
import { AdnocOrderComponentsModule } from './components/adnoc-order-components.module';

@NgModule({
  imports: [OrderCoreModule, AdnocOrderOccModule, AdnocOrderComponentsModule],
})
export class AdnocOrderModule {}
