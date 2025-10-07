import { Order } from '@spartacus/order/root';
export interface IReturnReason {
  code: string;
  name: string;
}

export type IReturnReasons = {
  returnReasons: IReturnReason[];
};

export type ICancelReasons = {
  cacelReasons: IReturnReason[];
};

export interface AdnocOrder extends Order {
  replenishmentOrderCode?: any;
  returnReason?: IReturnReason[];
  cancelReason?: IReturnReason[];
}

export interface CancelOrReturnRequestEntryInput {
  orderEntryNumber?: number;
  quantity?: number;
}
export interface ICancellationRequestEntryInputs {
  orderEntryNumber: number;
  quantity: number;
  cancelReason: IReturnReason[];
}
