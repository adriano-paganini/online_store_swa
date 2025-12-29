'use client';

import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { useCart } from '@/Contexts/cartContext';
import { mockProducts } from '@/mocks/mockProducts';
import { Minus, Plus, Trash } from 'lucide-react';

export function CartSidebar() {
  const { cart, loading, incrementItem, decrementItem, removeItem, clearCart, itemLoadingIds, clearingCart } =
    useCart();

  if (loading) {
    return <div className="flex h-full items-center justify-center text-sm text-muted-foreground">Loading cart…</div>;
  }

  if (!cart || cart.items.length === 0) {
    return (
      <div className="flex h-full items-center justify-center text-sm text-muted-foreground">Your cart is empty</div>
    );
  }

  const subtotal = cart.items.reduce((sum, item) => {
    const price = item.currentPrice - (item.appliedDiscount ?? 0);
    return sum + price * item.quantity;
  }, 0);

  return (
    <div className="flex h-full flex-col">
      <div className="flex-1 space-y-4 overflow-auto px-4">
        {cart.items.map((item) => {
          const product = mockProducts.find((p): p is (typeof mockProducts)[number] => p.id === item.productId);
          if (!product) return null;

          const isItemLoading = itemLoadingIds.has(item.id);

          const discountedPrice = item.currentPrice - (item.appliedDiscount ?? 0);

          const hasDiscount = item.appliedDiscount !== null && item.appliedDiscount > 0;

          return (
            <div
              key={item.id}
              className="flex gap-4 rounded-md border p-3"
            >
              <img
                src={product.images[0]}
                alt={product.name}
                className="h-16 w-16 rounded-md object-cover"
              />

              <div className="flex flex-1 flex-col gap-1">
                <span className="text-sm font-medium">{product.name}</span>

                <div className="flex items-center gap-2 text-sm">
                  <span className="font-semibold">${discountedPrice.toFixed(2)}</span>

                  {hasDiscount && (
                    <>
                      <span className="text-xs text-muted-foreground line-through">${product.price.toFixed(2)}</span>
                      <Badge
                        variant="destructive"
                        className="text-xs"
                      >
                        -{Math.round(product.discount * 100)}%
                      </Badge>
                    </>
                  )}
                </div>

                <div className="mt-2 flex items-center gap-2">
                  <Button
                    size="icon"
                    variant="outline"
                    disabled={isItemLoading}
                    onClick={() => void decrementItem(item.id)}
                  >
                    <Minus className="h-3 w-3" />
                  </Button>

                  <span className="w-6 text-center text-sm">{item.quantity}</span>

                  <Button
                    size="icon"
                    variant="outline"
                    disabled={isItemLoading}
                    onClick={() => void incrementItem(item.id)}
                  >
                    <Plus className="h-3 w-3" />
                  </Button>
                </div>
              </div>

              <Button
                size="icon"
                variant="ghost"
                disabled={isItemLoading}
                onClick={() => void removeItem(item.id)}
              >
                <Trash className="h-4 w-4 text-destructive" />
              </Button>
            </div>
          );
        })}
      </div>

      <Separator className="my-4" />

      <div className="space-y-3 px-4 pb-4">
        <div className="flex items-center justify-between text-sm font-medium">
          <span>Subtotal</span>
          <span>${subtotal.toFixed(2)}</span>
        </div>

        <Button className="w-full">Checkout</Button>

        <Button
          variant="ghost"
          className="w-full text-destructive"
          disabled={clearingCart}
          onClick={() => void clearCart()}
        >
          {clearingCart ? 'Clearing…' : 'Clear cart'}
        </Button>
      </div>
    </div>
  );
}
