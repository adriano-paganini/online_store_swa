import type { TAddressDTO } from './address.types';

export enum OrderStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  SHIPPING = 'SHIPPING',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
}

export type TOrderItemDTO = {
  productId: number;
  productName: string;
  quantity: number;
  priceAtPurchase: number;
  appliedDiscount: number;
};

export type TOrderDTO = {
  orderNumber: number;
  status: OrderStatus;
  total: number;
  timestamp: string;
  items: TOrderItemDTO[];
};

export type TOrderCreateDTO = {
  shippingAddress: TAddressDTO;
  billingAddress: TAddressDTO;
  shippingAddressId?: number;
  billingAddressId?: number;
};
