import { useCart } from '@/Contexts/cartContext';
import { CartItemRow } from '@/components/cart/CartItemRow';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

export function CheckoutCartItems() {
  const { cart, incrementItem, decrementItem, removeItem, itemLoadingIds } = useCart();

  if (!cart) return null;

  return (
    <Card>
      <CardHeader>
        <CardTitle>Your cart</CardTitle>
      </CardHeader>

      <CardContent className="space-y-3">
        {cart.items.map((item) => (
          <CartItemRow
            key={item.id}
            item={item}
            isLoading={itemLoadingIds.has(item.id)}
            onIncrement={() => void incrementItem(item.id)}
            onDecrement={() => void decrementItem(item.id)}
            onRemove={() => void removeItem(item.id)}
          />
        ))}
      </CardContent>
    </Card>
  );
}
