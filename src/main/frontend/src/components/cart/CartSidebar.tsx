'use client';

import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { useCart } from '@/Contexts/cartContext';
import { Minus, Plus, Trash } from 'lucide-react';

export function CartSidebar() {
  const { cart, loading, updateItem, removeItem, clearCart } = useCart();

  if (loading) {
    return <div className="flex h-full items-center justify-center text-sm text-muted-foreground">Loading cart…</div>;
  }

  if (!cart || cart.items.length === 0) {
    return (
      <div className="flex h-full flex-col items-center justify-center gap-4 text-center">
        <span className="text-sm text-muted-foreground">Your cart is empty</span>
      </div>
    );
  }

  const subtotal = cart.items.reduce(
    (sum, item) => sum + item.quantity * (item.currentPrice - (item.appliedDiscount ?? 0)),
    0
  );

  return (
    <div className="flex h-full flex-col">
      <div className="flex-1 space-y-4 overflow-auto pr-2">
        {cart.items.map((item) => {
          const price = item.currentPrice - (item.appliedDiscount ?? 0);

          return (
            <div
              key={item.id}
              className="flex items-start justify-between gap-4"
            >
              <div className="flex flex-col gap-1">
                <span className="text-sm font-medium">Product #{item.productId}</span>

                <span className="text-sm text-muted-foreground">${price.toFixed(2)}</span>

                <div className="mt-2 flex items-center gap-2">
                  <Button
                    size="icon"
                    variant="outline"
                    onClick={() =>
                      void updateItem(item.id, {
                        quantity: Math.max(1, item.quantity - 1),
                      })
                    }
                  >
                    <Minus className="h-3 w-3" />
                  </Button>

                  <span className="w-6 text-center text-sm">{item.quantity}</span>

                  <Button
                    size="icon"
                    variant="outline"
                    onClick={() =>
                      void updateItem(item.id, {
                        quantity: item.quantity + 1,
                      })
                    }
                  >
                    <Plus className="h-3 w-3" />
                  </Button>
                </div>
              </div>

              <Button
                size="icon"
                variant="ghost"
                onClick={() => void removeItem(item.id)}
              >
                <Trash className="h-4 w-4 text-destructive" />
              </Button>
            </div>
          );
        })}
      </div>

      <Separator className="my-4" />

      <div className="space-y-3">
        <div className="flex items-center justify-between text-sm font-medium">
          <span>Subtotal</span>
          <span>${subtotal.toFixed(2)}</span>
        </div>

        <Button className="w-full">Checkout</Button>

        <Button
          variant="ghost"
          className="w-full text-destructive"
          onClick={void clearCart}
        >
          Clear cart
        </Button>
      </div>
    </div>
  );
}
