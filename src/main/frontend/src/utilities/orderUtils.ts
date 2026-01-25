import { OrderStatus, TOrderItemDTO } from '@/DTO/order.types';

export const OrderStatusLabels: Record<OrderStatus, string> = {
  [OrderStatus.CANCELED]: 'Canceled',
  [OrderStatus.DELIVERED]: 'Delivered',
  [OrderStatus.PAID]: 'Paid',
  [OrderStatus.PENDING]: 'Pending',
  [OrderStatus.SHIPPING]: 'Shipping',
};

export const OrderStatusBgClasses: Record<OrderStatus, string> = {
  [OrderStatus.CANCELED]: 'bg-destructive',
  [OrderStatus.DELIVERED]: 'bg-emerald-500',
  [OrderStatus.PAID]: 'bg-blue-500',
  [OrderStatus.PENDING]: 'bg-yellow-500',
  [OrderStatus.SHIPPING]: 'bg-indigo-500',
};

/**
 * Calculates the total money of an order.
 *
 * For each order item, the total is computed using the price at the time of
 * purchase, adjusted by any applied discount, and multiplied by the item quantity.
 * The final sum is rounded to two decimal points to avoid floating-point precision issues.
 *
 * @param items - The list of order items to include in the total calculation
 * @returns The final order total rounded to two decimal places
 */
export function calculateOrderTotal(items: TOrderItemDTO[]): number {
  const total = items.reduce((sum, item) => {
    const discounted = item.priceAtPurchase * (1 - item.appliedDiscount);
    return sum + discounted * item.quantity;
  }, 0);

  return Math.round(total * 100) / 100;
}
