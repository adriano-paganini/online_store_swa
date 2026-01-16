import { OrderStatus, TOrderItemDTO } from '@/DTO/order.types';

export const OrderStatusLabels: Record<OrderStatus, string> = {
  [OrderStatus.CANCELLED]: 'Cancelled',
  [OrderStatus.DELIVERED]: 'Delivered',
  [OrderStatus.PAID]: 'Paid',
  [OrderStatus.PENDING]: 'Pending',
  [OrderStatus.SHIPPING]: 'Shipping',
};

export function calculateOrderTotal(items: TOrderItemDTO[]): number {
  const total = items.reduce((sum, item) => {
    const discounted = item.priceAtPurchase * (1 - item.appliedDiscount);
    return sum + discounted * item.quantity;
  }, 0);

  return Math.round(total * 100) / 100;
}
