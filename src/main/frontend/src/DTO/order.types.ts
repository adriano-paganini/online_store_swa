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
  shippingAddressId: number;
  billingAddressId: number;
};
