import type { TAddressDTO } from './address.types';

export enum OrderStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  SHIPPING = 'SHIPPING',
  DELIVERED = 'DELIVERED',
  CANCELED = 'CANCELED',
}

export enum ShippingMethod {
  FAIRY_DUST_DISPATCH = 'FAIRY_DUST_DISPATCH',
  CARRIER_PIGEON = 'CARRIER_PIGEON',
  WELL_FIGURE_IT_OUT = 'WELL_FIGURE_IT_OUT',
}

export type TOrderItemDTO = {
  productId: number;
  productName: string;
  quantity: number;
  priceAtPurchase: number;
  appliedDiscount: number;
};

export type TOrderDTO = {
  orderNumber: string;
  status: OrderStatus;
  total: number;
  timestamp: string;
  items: TOrderItemDTO[];
  shippingAddress: TAddressDTO | null;
  billingAddress: TAddressDTO | null;
  shippingMethod: ShippingMethod;
  transactionId: string;
  paidAt: string;
};

export type TOrderCreateDTO = {
  shippingAddressId: number;
  billingAddressId: number;
  shippingMethod: ShippingMethod;
};
