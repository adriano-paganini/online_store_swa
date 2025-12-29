'use client';

import { ShoppingCartIcon, Star } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';

import { useCart } from '@/Contexts/cartContext';
import { ProductApi } from '@/utilities/productApi';
import { toast } from 'sonner';

import { ReviewsList } from '@/components/review/ReviewsList';
import type { TProductDTO } from '../DTO/product.types';

export default function ProductDetailsPage() {
  const { id } = useParams<{ id: string }>();
  const productId = Number(id);

  const [product, setProduct] = useState<TProductDTO | null>(null);
  const [loading, setLoading] = useState(true);

  const { addItem, loading: cartLoading } = useCart();

  useEffect(() => {
    const loadProduct = async () => {
      try {
        setLoading(true);
        const data = await ProductApi.fetchProductById(productId);
        setProduct(data);
      } catch (error) {
        console.error('Failed to load product', error);
        toast.error('Failed to load product');
      } finally {
        setLoading(false);
      }
    };

    if (!Number.isNaN(productId)) {
      void loadProduct();
    }
  }, [productId]);

  const handleAddToCart = async () => {
    if (!product) return;

    if (product.stock === 0) {
      toast.error('This product is out of stock');
      return;
    }

    await addItem({
      productId: product.id,
      quantity: 1,
    });
  };

  if (loading) {
    return <p className="text-center text-muted-foreground">Loading product...</p>;
  }

  if (!product) {
    return <p className="text-center text-muted-foreground">Product not found</p>;
  }

  const hasDiscount = product.discount > 0;
  const discountedPrice = product.price * (1 - product.discount);

  return (
    <div className="mx-auto max-w-6xl space-y-10">
      <div className="grid gap-8 md:grid-cols-2">
        <div className="overflow-hidden rounded-lg bg-muted">
          <img
            src={product.images[0] || `/placeholder.svg?height=600&width=600&query=${encodeURIComponent(product.name)}`}
            alt={product.name}
            className="aspect-square w-full object-cover"
          />
        </div>

        <div className="flex flex-col gap-4">
          <h1 className="text-3xl font-bold">{product.name}</h1>

          <div className="flex items-center gap-4">
            <div className="flex items-center gap-2">
              <Star className="h-5 w-5 fill-yellow-400 text-yellow-400" />
              <span className="font-medium">{product.avgScore.toFixed(1)}</span>
              <span className="text-muted-foreground">/ 5</span>
            </div>

            {hasDiscount && (
              <>
                <Separator
                  orientation="vertical"
                  className="h-6"
                />

                <Badge className="bg-destructive/90 text-sm font-semibold">
                  {Math.round(product.discount * 100)}% OFF
                </Badge>
              </>
            )}

            {product.stock <= 10 && product.stock > 0 && (
              <>
                <Separator
                  orientation="vertical"
                  className="h-6"
                />

                <Badge className="bg-orange-400/90 text-sm font-semibold">Only {product.stock} left in stock!</Badge>
              </>
            )}
          </div>

          <p className="text-muted-foreground">{product.description}</p>

          <div className="mt-auto flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
            <div className="flex items-center gap-4">
              {!hasDiscount && <span className="text-2xl font-semibold">${product.price.toFixed(2)}</span>}

              {hasDiscount && (
                <>
                  <span className="text-2xl font-semibold">${discountedPrice.toFixed(2)}</span>
                  <span className="text-muted-foreground line-through">${product.price.toFixed(2)}</span>
                </>
              )}
            </div>

            <Button
              className="w-full md:w-fit"
              onClick={() => void handleAddToCart()}
              disabled={product.stock === 0 || cartLoading}
            >
              <ShoppingCartIcon className="mr-2 h-5 w-5" />
              {product.stock === 0 ? 'Out of stock' : 'Add to cart'}
            </Button>
          </div>
        </div>
      </div>

      <section className="space-y-4">
        <h2 className="text-2xl font-semibold">Reviews</h2>
        <ReviewsList productId={product.id} />
      </section>
    </div>
  );
}
