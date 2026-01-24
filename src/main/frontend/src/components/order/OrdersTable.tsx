'use client';

import { Skeleton } from '@/components/ui/skeleton';
import type { TOrderDTO } from '@/DTO/order.types';
import { OrderRow } from './OrderRow';

type TOrdersTableProps = {
  orders: TOrderDTO[];
  loading: boolean;
};

export function OrdersTable({ orders, loading }: TOrdersTableProps) {
  if (loading && !orders) {
    return (
      <div className="space-y-2 rounded-lg border">
        {Array.from({ length: 6 }).map((_, i) => (
          <Skeleton
            key={i}
            className="h-20 w-full rounded-none first:rounded-t-lg last:rounded-b-lg"
          />
        ))}
      </div>
    );
  }

  if (orders.length === 0) {
    return <p className="text-muted-foreground">No orders found.</p>;
  }

  return (
    <div className="overflow-hidden rounded-lg">
      {orders.map((order) => (
        <OrderRow
          key={order.orderNumber}
          order={order}
        />
      ))}
    </div>
  );
}
