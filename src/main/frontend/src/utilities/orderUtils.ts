import { OrderStatus } from '@/DTO/order.types';

export const OrderStatusLabels: Record<OrderStatus, string> = {
  [OrderStatus.CANCELLED]: 'Cancelled',
  [OrderStatus.DELIVERED]: 'Delivered',
  [OrderStatus.PAID]: 'Paid',
  [OrderStatus.PENDING]: 'Pending',
  [OrderStatus.SHIPPING]: 'Shipping',
};
