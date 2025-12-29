'use client';

import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import type { TCartItemDTO } from '@/DTO/cart.types';
import type { TProductDTO } from '@/DTO/product.types';
import { Minus, Plus, Trash } from 'lucide-react';

type TCartItemRowProps = {
  item: TCartItemDTO;
  product: TProductDTO;
  isLoading: boolean;
  onIncrement: () => void;
  onDecrement: () => void;
  onRemove: () => void;
};

export function CartItemRow({ item, product, isLoading, onIncrement, onDecrement, onRemove }: TCartItemRowProps) {
  const discountedPrice = item.currentPrice - (item.appliedDiscount ?? 0);

  const hasDiscount = item.appliedDiscount !== null && item.appliedDiscount > 0;

  return (
    <div className="flex gap-4 rounded-md border p-3">
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
            disabled={isLoading}
            onClick={onDecrement}
          >
            <Minus className="h-3 w-3" />
          </Button>

          <span className="w-6 text-center text-sm">{item.quantity}</span>

          <Button
            size="icon"
            variant="outline"
            disabled={isLoading}
            onClick={onIncrement}
          >
            <Plus className="h-3 w-3" />
          </Button>
        </div>
      </div>

      <Button
        size="icon"
        variant="ghost"
        disabled={isLoading}
        onClick={onRemove}
      >
        <Trash className="h-4 w-4 text-destructive" />
      </Button>
    </div>
  );
}
