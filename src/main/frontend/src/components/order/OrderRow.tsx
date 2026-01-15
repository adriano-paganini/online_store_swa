'use client';

import { ChevronDown } from 'lucide-react';
import { useState } from 'react';

import { OrderStatus, type TOrderDTO } from '@/DTO/order.types';
import { cn } from '@/lib/utils';
import { OrderStatusLabels } from '@/utilities/orderUtils';
import { ROUTES } from '@/utilities/routes.paths';
import { Link } from 'react-router-dom';
import { Button } from '../ui/button';

type TOrderRowProps = {
  order: TOrderDTO;
};

export function OrderRow({ order }: TOrderRowProps) {
  const [open, setOpen] = useState(false);

  const actualTotal = order.items.reduce((sum, item) => {
    const discountedPrice = item.priceAtPurchase * (1 - item.appliedDiscount);

    return sum + discountedPrice * item.quantity;
  }, 0);

  return (
    <div className="border-b">
      <button
        onClick={() => setOpen((v) => !v)}
        className="flex w-full items-center gap-4 px-4 py-4 text-left hover:bg-muted"
      >
        <div className="flex-1">
          <div className="font-medium">Order #{order.orderNumber}</div>
          <div className="text-sm text-muted-foreground">{new Date(order.timestamp).toLocaleDateString()}</div>
        </div>

        <div className="flex items-center gap-2 text-sm">
          {OrderStatusLabels[order.status]}
          {order.status === OrderStatus.PENDING && (
            <Link to={`${ROUTES.PAYMENT}/${order.orderNumber}`}>
              <Button
                size="sm"
                variant="outline"
              >
                Pay Now
              </Button>
            </Link>
          )}
        </div>

        <div className="text-sm font-semibold">${actualTotal.toFixed(2)}</div>

        <ChevronDown className={cn('h-4 w-4 transition-transform', open && 'rotate-180')} />
      </button>

      {open && (
        <div className="space-y-2 bg-muted/40 px-6 py-4 text-sm">
          {order.items.map((item) => (
            <div
              key={item.productId}
              className="flex justify-between"
            >
              <span>
                {item.productName} x {item.quantity}
              </span>
              <span>${(item.priceAtPurchase * (1 - item.appliedDiscount) * item.quantity).toFixed(2)}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
