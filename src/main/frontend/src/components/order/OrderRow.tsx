'use client';

import { ChevronDown, Trash2, X } from 'lucide-react';
import { useState } from 'react';
import { Link } from 'react-router-dom';

import { OrderStatus, type TOrderDTO } from '@/DTO/order.types';
import { cn, toastApiError } from '@/lib/utils';
import { OrderApi } from '@/utilities/orderApi';
import { calculateOrderTotal, OrderStatusBgClasses, OrderStatusLabels } from '@/utilities/orderUtils';

import { toast } from 'sonner';
import { Button } from '../ui/button';
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from '../ui/dialog';
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '../ui/tooltip';

type TOrderRowProps = {
  order: TOrderDTO;
};

export function OrderRow({ order }: TOrderRowProps) {
  const [rowOpen, setRowOpen] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [cancelling, setCancelling] = useState(false);

  const actualTotal = calculateOrderTotal(order.items);

  const handleCancel = async () => {
    setCancelling(true);

    try {
      await OrderApi.cancelOrder(order.orderNumber);
      toast.success('Order cancelled successfully');
      setDialogOpen(false);
      window.location.reload();
    } catch (err: unknown) {
      toastApiError(err);
    } finally {
      setCancelling(false);
    }
  };

  return (
    <div className="border-b">
      <button
        onClick={() => setRowOpen((v) => !v)}
        className="flex w-full items-center gap-4 px-4 py-4 text-left hover:bg-muted"
      >
        <div className="flex-1">
          <div className="flex items-center font-medium">
            <TooltipProvider>
              <Tooltip>
                <TooltipTrigger asChild>
                  <span className={cn('mr-2 inline-block h-3 w-3 rounded-full', OrderStatusBgClasses[order.status])} />
                </TooltipTrigger>

                <TooltipContent side="top">{OrderStatusLabels[order.status]}</TooltipContent>
              </Tooltip>
            </TooltipProvider>
            Order #{order.orderNumber}
          </div>

          <div className="text-sm text-muted-foreground">{new Date(order.timestamp).toLocaleDateString()}</div>
        </div>

        <div className="flex items-center gap-2 text-sm">
          {order.status === OrderStatus.PENDING && (
            <Link to={`/payment/${order.orderNumber}`}>
              <Button
                size="sm"
                variant="outline"
                onClick={(e) => e.stopPropagation()}
              >
                Pay Now
              </Button>
            </Link>
          )}

          {(order.status === OrderStatus.PENDING || order.status === OrderStatus.PAID) && (
            <Dialog
              open={dialogOpen}
              onOpenChange={setDialogOpen}
            >
              <DialogTrigger asChild>
                <Button
                  size="sm"
                  variant="destructive"
                  onClick={(e) => e.stopPropagation()}
                >
                  Cancel
                </Button>
              </DialogTrigger>

              <DialogContent onClick={(e) => e.stopPropagation()}>
                <DialogHeader>
                  <DialogTitle>Cancel order?</DialogTitle>
                </DialogHeader>

                <p className="text-sm text-muted-foreground">
                  Are you sure you want to cancel order <strong>#{order.orderNumber}</strong>? This action cannot be
                  undone.
                </p>

                <DialogFooter>
                  <Button
                    variant="outline"
                    onClick={() => setDialogOpen(false)}
                    disabled={cancelling}
                  >
                    <X className="h-4 w-4" />
                    Keep order
                  </Button>

                  <Button
                    variant="destructive"
                    onClick={() => void handleCancel()}
                    disabled={cancelling}
                  >
                    <Trash2 className="h-4 w-4" />
                    {cancelling ? 'Cancelling…' : 'Confirm cancel'}
                  </Button>
                </DialogFooter>
              </DialogContent>
            </Dialog>
          )}
        </div>

        <div className="text-sm font-semibold">${actualTotal.toFixed(2)}</div>

        <ChevronDown className={cn('h-4 w-4 transition-transform', rowOpen && 'rotate-180')} />
      </button>

      {rowOpen && (
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
