'use client';

import { ProductCard } from '@/components/product/ProductCard';
import { Button } from '@/components/ui/button';
import { ArrowRight, Package, Shield, TrendingUp } from 'lucide-react';
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import type { TProductDTO } from '../DTO/product.types';
import { ProductApi } from '../utilities/productApi';
import { ROUTES } from '../utilities/routes.paths';

export default function HomePage() {
  const [featuredProducts, setFeaturedProducts] = useState<TProductDTO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadProducts = async () => {
      try {
        const response = await ProductApi.fetchProducts({ limit: 4 });
        setFeaturedProducts(response.data);
      } catch (error) {
        console.error('Failed to load products:', error);
      } finally {
        setLoading(false);
      }
    };

    void loadProducts();
  }, []);

  return (
    <>
      <section className="mx-auto py-14 md:py-20">
        <div className="mx-auto max-w-3xl text-center">
          <h1 className="mb-6 text-balance text-4xl font-bold tracking-tight md:text-6xl">
            Discover quality products for your lifestyle
          </h1>
          <p className="mb-8 text-pretty text-lg leading-relaxed text-muted-foreground md:text-xl">
            Transform your shopping experience with curated collections and unbeatable prices on everything you love.
          </p>
          <div className="flex flex-wrap justify-center gap-4">
            <Link to={ROUTES.PRODUCTS}>
              <Button
                size="lg"
                className="gap-2"
              >
                Shop Now
                <ArrowRight className="h-4 w-4" />
              </Button>
            </Link>
          </div>
        </div>
      </section>

      <section className="border-y bg-muted/50 py-12">
        <div className="container mx-auto px-4">
          <div className="grid gap-8 md:grid-cols-3">
            <div className="flex flex-col items-center text-center">
              <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-primary text-primary-foreground">
                <TrendingUp className="h-6 w-6" />
              </div>
              <h3 className="mb-2 font-semibold">Best Prices</h3>
              <p className="text-pretty text-sm text-muted-foreground">
                Competitive pricing on all products with regular discounts
              </p>
            </div>

            <div className="flex flex-col items-center text-center">
              <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-primary text-primary-foreground">
                <Package className="h-6 w-6" />
              </div>
              <h3 className="mb-2 font-semibold">Fast Shipping</h3>
              <p className="text-pretty text-sm text-muted-foreground">Quick and reliable delivery to your doorstep</p>
            </div>

            <div className="flex flex-col items-center text-center">
              <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-primary text-primary-foreground">
                <Shield className="h-6 w-6" />
              </div>
              <h3 className="mb-2 font-semibold">Secure Shopping</h3>
              <p className="text-pretty text-sm text-muted-foreground">Protected payments and buyer guarantee</p>
            </div>
          </div>
        </div>
      </section>

      <section className="mx-auto pt-16">
        <div className="mb-8 flex items-center justify-between">
          <div>
            <h2 className="text-balance text-3xl font-bold">Featured Products</h2>
            <p className="mt-2 text-muted-foreground">Handpicked items just for you</p>
          </div>
          <Link to={ROUTES.PRODUCTS}>
            <Button
              variant="outline"
              className="gap-2 bg-transparent"
            >
              View All
              <ArrowRight className="h-4 w-4" />
            </Button>
          </Link>
        </div>

        {loading ? (
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
            {[1, 2, 3, 4].map((i) => (
              <div
                key={i}
                className="h-96 animate-pulse rounded-lg bg-muted"
              />
            ))}
          </div>
        ) : (
          <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
            {featuredProducts.map((product) => (
              <ProductCard
                key={product.id}
                product={product}
              />
            ))}
          </div>
        )}
      </section>
    </>
  );
}
