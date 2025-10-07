import { NgModule } from '@angular/core';
import {
  MyAccountV2OrdersModule,
  OrderDetailsOrderEntriesContext,
  ReplenishmentOrderDetailsModule,
  ReplenishmentOrderHistoryModule,
} from '@spartacus/order/components';
import { OrderDetailsOrderEntriesContextToken } from '@spartacus/order/root';
import { AdnocOrderCancellationModule } from './amend-order/cancellations/adnoc-order-cancellation.module';
import { AdnocOrderDetailsModule } from './order-details/adnoc-order-details.module';
import { AdnocOrderConfirmationModule } from './order-confirmation/adnoc-order-confirmation.module';
import { OrderReturnModule } from './amend-order/returns/order-return.module';
import { AdnocReturnRequestListModule } from './adnoc-return-request-list/adnoc-order-return-request-list.module';
import { AdnocReturnRequestDetailModule } from './return-request-detail/adnoc-return-request-detail.module';
import { AdnocOrderHistoryModule } from './order-history';

@NgModule({
  imports: [
    AdnocOrderHistoryModule,
    AdnocOrderDetailsModule,
    ReplenishmentOrderDetailsModule,
    AdnocOrderCancellationModule,
    OrderReturnModule,
    ReplenishmentOrderHistoryModule,
    AdnocReturnRequestListModule,
    AdnocReturnRequestDetailModule,
    AdnocOrderConfirmationModule,
    MyAccountV2OrdersModule,
  ],
  providers: [
    {
      provide: OrderDetailsOrderEntriesContextToken,
      useExisting: OrderDetailsOrderEntriesContext,
    },
  ],
})
export class AdnocOrderComponentsModule {}
