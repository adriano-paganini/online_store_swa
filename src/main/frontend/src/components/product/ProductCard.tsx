'use client';

import type React from 'react';

import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardFooter } from '@/components/ui/card';
import { ShoppingCart, Star } from 'lucide-react';
import { Link } from 'react-router-dom';
import { toast } from 'sonner';
import type { TProductDTO } from '../../DTO/product.types';

type TProductCardProps = {
  product: TProductDTO;
};

export function ProductCard({ product }: TProductCardProps) {
  const discountedPrice = product.price * (1 - product.discount);
  const hasDiscount = product.discount > 0;

  const handleAddToCart = (e: React.MouseEvent) => {
    e.preventDefault();
    toast.success('Action to be implemented');
  };

  return (
    <Link to={`/products/${product.id}`}>
      <Card className="group overflow-hidden transition-shadow hover:shadow-lg">
        <div className="relative aspect-square overflow-hidden bg-muted">
          <img
            src={product.images[0] || `/placeholder.svg?height=400&width=400&query=${encodeURIComponent(product.name)}`}
            alt={product.name}
            className="h-full w-full object-cover transition-transform group-hover:scale-105"
          />

          {hasDiscount && (
            <Badge className="absolute right-2 top-2 bg-destructive text-destructive-foreground">
              {Math.round(product.discount * 100)}% OFF
            </Badge>
          )}

          {product.stock === 0 && (
            <Badge className="absolute right-2 top-2 bg-muted text-muted-foreground">Out of Stock</Badge>
          )}
        </div>

        <CardContent className="p-4">
          <h3 className="mb-2 line-clamp-2 font-semibold">{product.name}</h3>
          <p className="mb-3 line-clamp-2 text-sm text-muted-foreground">{product.description}</p>

          <div className="mb-2 flex items-center gap-2">
            <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
            <span className="text-sm font-medium">{product.avgScore.toFixed(1)}</span>
          </div>

          <div className="flex items-center gap-2">
            {hasDiscount ? (
              <>
                <span className="text-lg font-bold">${discountedPrice.toFixed(2)}</span>
                <span className="text-sm text-muted-foreground line-through">${product.price.toFixed(2)}</span>
              </>
            ) : (
              <span className="text-lg font-bold">${product.price.toFixed(2)}</span>
            )}
          </div>
        </CardContent>

        <CardFooter className="p-4 pt-0">
          <Button
            className="w-full"
            disabled={product.stock === 0}
            onClick={handleAddToCart}
          >
            <ShoppingCart className="mr-2 h-4 w-4" />
            {product.stock === 0 ? 'Out of Stock' : 'Add to Cart'}
          </Button>
        </CardFooter>
      </Card>
    </Link>
  );
}
