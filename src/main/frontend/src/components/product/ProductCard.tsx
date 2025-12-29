'use client';

import { ShoppingCartIcon, Star } from 'lucide-react';
import type React from 'react';
import { Link } from 'react-router-dom';

import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Separator } from '@/components/ui/separator';
import { cn } from '@/lib/utils';

import { useCart } from '@/Contexts/cartContext';
import { toast } from 'sonner';
import type { TProductDTO } from '../../DTO/product.types';

type TProductCardProps = {
  product: TProductDTO;
};

export function ProductCard({ product }: TProductCardProps) {
  const { addItem, loading } = useCart();

  const hasDiscount = product.discount > 0;
  const discountedPrice = product.price * (1 - product.discount);

  const handleAddToCart = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    e.stopPropagation();

    if (product.stock === 0) {
      toast.error('This product is out of stock');
      return;
    }

    void addItem({
      productId: product.id,
      quantity: 1,
    });
  };

  return (
    <Link to={`/products/${product.id}`}>
      <Card
        className={cn('group border-none bg-muted/40 shadow-sm transition-all', 'hover:bg-muted/60 hover:shadow-md')}
      >
        <CardContent className="flex flex-col gap-2 p-4">
          <div className="relative overflow-hidden rounded-md bg-background">
            <div className="absolute left-3 top-3 z-10 flex items-center gap-1 px-2.5 py-0.5 text-xs">
              <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
              <span className="font-medium text-foreground">{product.avgScore.toFixed(1)}</span>
              <span className="text-muted-foreground">/ 5</span>
            </div>

            {hasDiscount && (
              <Badge className="absolute right-3 top-3 z-10 bg-destructive/80 text-destructive-foreground">
                {Math.round(product.discount * 100)}% OFF
              </Badge>
            )}

            {product.stock === 0 && (
              <Badge className="absolute bottom-3 left-1/2 z-10 -translate-x-1/2 bg-orange-400/90 text-sm text-white">
                Out of Stock
              </Badge>
            )}

            <img
              src={
                product.images[0] || `/placeholder.svg?height=400&width=400&query=${encodeURIComponent(product.name)}`
              }
              alt={product.name}
              className="aspect-square w-full object-cover transition-transform duration-300 group-hover:scale-105"
            />

            <div
              className={cn(
                'absolute inset-0 flex items-center justify-center p-4 text-center',
                'bg-background/80 backdrop-blur-sm',
                'translate-y-full transition-transform duration-300 ease-out',
                'group-hover:translate-y-0'
              )}
            >
              <p className="line-clamp-6">{product.description}</p>
            </div>
          </div>

          <div className="space-y-2">
            <h3 className="text-center text-lg font-semibold">{product.name}</h3>

            <Separator />

            <div className="flex items-center justify-between">
              {!hasDiscount && <span className="text-xl font-semibold">${product.price.toFixed(2)}</span>}

              {hasDiscount && (
                <div className="flex items-center gap-2">
                  <span className="text-xl font-semibold">${discountedPrice.toFixed(2)}</span>
                  <span className="text-sm font-medium text-muted-foreground line-through">
                    ${product.price.toFixed(2)}
                  </span>
                </div>
              )}

              <Button
                variant="ghost"
                size="icon"
                aria-label="Add to cart"
                disabled={product.stock === 0 || loading}
                onClick={handleAddToCart}
              >
                <ShoppingCartIcon />
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>
    </Link>
  );
}
