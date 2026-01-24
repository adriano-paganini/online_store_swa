'use client';

import { ChevronDown, Trash2, X } from 'lucide-react';
import { useState } from 'react';
import { Link } from 'react-router-dom';

import { OrderStatus, type TOrderDTO } from '@/DTO/order.types';
import { cn, toastApiError } from '@/lib/utils';
import { OrderApi } from '@/utilities/orderApi';
import { calculateOrderTotal, OrderStatusBgClasses, OrderStatusLabels } from '@/utilities/orderUtils';

import { TAddressDTO } from '@/DTO/address.types';
import { getShippingMethodLabel } from '@/utilities/shippingUtils';
import { toast } from 'sonner';
import { Button } from '../ui/button';
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from '../ui/dialog';
import { Separator } from '../ui/separator';
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '../ui/tooltip';

const formatAddress = (address: TAddressDTO): string[] => {
  const lines = [
    `${address.street} ${address.number}`,
    address.extra,
    `${address.postalCode} ${address.city}`,
    address.country,
  ];

  return lines.filter(Boolean) as string[];
};

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
    } catch (err) {
      toastApiError(err);
    } finally {
      setCancelling(false);
    }
  };

  return (
    <div className="w-full">
      <button
        onClick={() => setRowOpen((v) => !v)}
        className="flex w-full items-center justify-between gap-3 border-b px-4 py-3 text-sm transition-opacity hover:bg-muted"
      >
        <div className="space-y-1">
          <div className="flex items-center gap-2 font-medium">
            <TooltipProvider>
              <Tooltip>
                <TooltipTrigger asChild>
                  <span className={cn('inline-block h-3 w-3 rounded-full', OrderStatusBgClasses[order.status])} />
                </TooltipTrigger>
                <TooltipContent>{OrderStatusLabels[order.status]}</TooltipContent>
              </Tooltip>
            </TooltipProvider>

            <span>Order #{order.orderNumber}</span>
          </div>

          <div className="text-sm text-muted-foreground">
            {new Date(order.timestamp).toLocaleDateString()} · {getShippingMethodLabel(order.shippingMethod)}
          </div>
        </div>

        <div className="flex items-center gap-3">
          <div className="flex items-center gap-2">
            {order.status === OrderStatus.PENDING && (
              <Link to={`/payment/${order.orderNumber}`}>
                <Button
                  size="sm"
                  variant="outline"
                  onClick={(e) => e.stopPropagation()}
                >
                  Pay now
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

          <div className="text-right text-sm font-semibold">${actualTotal.toFixed(2)}</div>

          <ChevronDown className={cn('h-4 w-4 transition-transform', rowOpen && 'rotate-180')} />
        </div>
      </button>

      {rowOpen && (
        <div className="space-y-4 bg-muted/40 px-6 py-4 text-sm">
          <div className="space-y-2">
            {order.items.map((item) => (
              <div
                key={item.productId}
                className="flex justify-between"
              >
                <span>
                  {item.productName} × {item.quantity}
                </span>
                <span>${(item.priceAtPurchase * (1 - item.appliedDiscount) * item.quantity).toFixed(2)}</span>
              </div>
            ))}
          </div>

          <Separator />

          <div className="flex items-start justify-around gap-4">
            <div>
              <div className="font-medium">Shipping address</div>

              {order.shippingAddress ? (
                <div className="mt-1 text-sm text-muted-foreground">
                  {formatAddress(order.shippingAddress).map((line, i) => (
                    <div key={i}>{line}</div>
                  ))}
                </div>
              ) : (
                <div className="text-muted-foreground">—</div>
              )}
            </div>

            <div>
              <div className="font-medium">Billing address</div>

              {order.billingAddress ? (
                <div className="mt-1 text-sm text-muted-foreground">
                  {formatAddress(order.billingAddress).map((line, i) => (
                    <div key={i}>{line}</div>
                  ))}
                </div>
              ) : (
                <div className="text-muted-foreground">—</div>
              )}
            </div>
          </div>

          {order.status !== OrderStatus.CANCELED && order.status !== OrderStatus.PENDING && (
            <>
              <Separator />

              <div className="text-center text-muted-foreground">
                Paid on {new Date(order.paidAt).toLocaleDateString()} · Transaction ID: {order.transactionId}
              </div>
            </>
          )}
        </div>
      )}
    </div>
  );
}
