'use client';

import { useCallback, useEffect, useState } from 'react';

import { Pagination } from '@/components/general/Pagination';
import { ProductFilters } from '@/components/product/ProductFilters';
import { ProductGrid } from '@/components/product/ProductGrid';
import { MockProductApi as ProductApi } from '@/mocks/mockProductApi'; // change to real api when BE implemented

import { toast } from 'sonner';
import type { TProductDTO } from '../DTO/product.types';

export default function ProductsPage() {
  const [products, setProducts] = useState<TProductDTO[]>([]);
  const [loading, setLoading] = useState(true);

  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [draftPriceRange, setDraftPriceRange] = useState<[number, number]>([0, 1000]);
  const [draftInStockOnly, setDraftInStockOnly] = useState(false);
  const [draftMinRating, setDraftMinRating] = useState(0);

  const [appliedFilters, setAppliedFilters] = useState({
    priceRange: [0, 1000] as [number, number],
    inStockOnly: false,
    minRating: 0,
  });

  const loadProducts = useCallback(async () => {
    try {
      setLoading(true);

      const response = await ProductApi.fetchProducts({
        page,
        limit: 12,
        minPrice: appliedFilters.priceRange[0],
        maxPrice: appliedFilters.priceRange[1],
        inStock: appliedFilters.inStockOnly || undefined,
        minRating: appliedFilters.minRating || undefined,
      });

      setProducts(response.data);
      setTotalPages(response.totalPages);
    } catch (err) {
      console.error('Failed to load products', err);
      toast.error('Failed to load products. Please try again later.');
    } finally {
      setLoading(false);
    }
  }, [page, appliedFilters]);

  useEffect(() => {
    void loadProducts();
  }, [loadProducts]);

  const handleApplyFilters = () => {
    setPage(0);
    setAppliedFilters({
      priceRange: draftPriceRange,
      inStockOnly: draftInStockOnly,
      minRating: draftMinRating,
    });
  };

  return (
    <>
      <h1 className="mb-8 text-3xl font-bold">All Products</h1>

      <div className="flex flex-col gap-8 lg:flex-row">
        <ProductFilters
          priceRange={draftPriceRange}
          setPriceRange={setDraftPriceRange}
          inStockOnly={draftInStockOnly}
          setInStockOnly={setDraftInStockOnly}
          minRating={draftMinRating}
          setMinRating={setDraftMinRating}
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
