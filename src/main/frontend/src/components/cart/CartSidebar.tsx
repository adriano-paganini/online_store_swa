'use client';

import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { useCart } from '@/Contexts/cartContext';
import { mockProducts } from '@/mocks/product/mockProducts';
import { CartItemRow } from './CartItemRow';

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

          return (
            <CartItemRow
              key={item.id}
              item={item}
              product={product}
              isLoading={isItemLoading}
              onIncrement={() => void incrementItem(item.id)}
              onDecrement={() => void decrementItem(item.id)}
              onRemove={() => void removeItem(item.id)}
            />
          );
        })}
      </div>

      <Separator className="my-4" />

      <div className="space-y-3 px-4">
        <div className="flex items-center justify-between text-sm font-medium">
          <span>Subtotal</span>
          <span>${subtotal.toFixed(2)}</span>
        </div>

        <Button className="w-full">Checkout</Button>

        <Button
          variant="outline"
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
