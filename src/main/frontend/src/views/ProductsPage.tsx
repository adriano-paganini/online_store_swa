'use client';

import { Pagination } from '@/components/general/Pagination';
import { ProductFilters } from '@/components/product/ProductFilters';
import { ProductGrid } from '@/components/product/ProductGrid';
import { useCallback, useEffect, useState } from 'react';
import type { TProductDTO } from '../DTO/product.types';
import { ProductApi } from '../utilities/productApi';

export default function ProductsPage() {
  const [products, setProducts] = useState<TProductDTO[]>([]);
  const [loading, setLoading] = useState(true);

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [priceRange, setPriceRange] = useState<[number, number]>([0, 1000]);
  const [inStockOnly, setInStockOnly] = useState(false);
  const [minRating, setMinRating] = useState(0);

  const loadProducts = useCallback(async () => {
    try {
      setLoading(true);

      const response = await ProductApi.fetchProducts({
        page,
        limit: 12,
        minPrice: priceRange[0],
        maxPrice: priceRange[1],
        inStock: inStockOnly || undefined,
        minRating: minRating || undefined,
      });

      setProducts(response.data);
      setTotalPages(response.totalPages);
    } catch (err) {
      console.error('Failed to load products', err);
    } finally {
      setLoading(false);
    }
  }, [page, priceRange, inStockOnly, minRating]);

  useEffect(() => {
    void loadProducts();
  }, [loadProducts]);

  const handleApplyFilters = () => {
    setPage(0);
    void loadProducts();
  };

  return (
    <>
      <h1 className="mb-8 text-3xl font-bold">All Products</h1>

      <div className="flex flex-col gap-8 lg:flex-row">
        <ProductFilters
          priceRange={priceRange}
          setPriceRange={setPriceRange}
          inStockOnly={inStockOnly}
          setInStockOnly={setInStockOnly}
          minRating={minRating}
          setMinRating={setMinRating}
          onApplyFilters={handleApplyFilters}
        />

        <div className="flex-1">
          <ProductGrid
            products={products}
            loading={loading}
          />

          <Pagination
            page={page}
            totalPages={totalPages}
            onPageChange={setPage}
          />
        </div>
      </div>
    </>
  );
}
