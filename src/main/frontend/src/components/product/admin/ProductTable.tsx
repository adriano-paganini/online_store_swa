'use client';

import { Plus } from 'lucide-react';
import { useCallback, useEffect, useState } from 'react';
import { toast } from 'sonner';

import type { TProductCreateDTO, TProductDTO } from '@/DTO/product.types';
import { ProductApi } from '@/utilities/productApi';

import { Pagination } from '@/components/general/Pagination';
import { Button } from '@/components/ui/button';

import { toastApiError } from '@/lib/utils';
import { ProductFilters } from '../ProductFilters';
import { ProductDeleteDialog } from './ProductDeleteDialog';
import { ProductDialog } from './ProductDialog';
import { ProductList } from './ProductList';

const emptyProduct = (): TProductCreateDTO => ({
  name: '',
  images: [],
  description: '',
  price: 0,
  stock: 0,
  discount: 0,
});

export const ProductTable = () => {
  const [products, setProducts] = useState<TProductDTO[]>([]);
  const [loading, setLoading] = useState(true);

  const [page, setPage] = useState(0);
  const [limit, setLimit] = useState(10);
  const [totalPages, setTotalPages] = useState(0);

  const [draftPriceRange, setDraftPriceRange] = useState<[number, number | null]>([0, null]);
  const [draftInStockOnly, setDraftInStockOnly] = useState(false);
  const [draftMinRating, setDraftMinRating] = useState(0);
  const [draftSort, setDraftSort] = useState('id,asc');
  const [draftSearch, setDraftSearch] = useState('');
  const [appliedSearch, setAppliedSearch] = useState('');

  const [appliedSort, setAppliedSort] = useState('id,asc');
  const [appliedFilters, setAppliedFilters] = useState({
    priceRange: [0, null] as [number, number | null],
    inStockOnly: false,
    minRating: 0,
  });

  const [selectedProduct, setSelectedProduct] = useState<TProductDTO | null>(null);
  const [isNew, setIsNew] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);

  const loadProducts = useCallback(async () => {
    try {
      setLoading(true);

      const res = await ProductApi.fetchProducts({
        page,
        limit,
        sort: appliedSort,
        minPrice: appliedFilters.priceRange[0],
        maxPrice: appliedFilters.priceRange[1],
        inStock: appliedFilters.inStockOnly || undefined,
        minRating: appliedFilters.minRating || undefined,
        search: appliedSearch || undefined,
      });

      setProducts(res.data);
      setTotalPages(res.totalPages);
    } catch (err) {
      toastApiError(err);
    } finally {
      setLoading(false);
    }
  }, [page, limit, appliedFilters, appliedSort, appliedSearch]);

  useEffect(() => {
    void loadProducts();
  }, [loadProducts]);

  useEffect(() => {
    setPage(0);
  }, [limit]);

  const handleApplyFilters = () => {
    setPage(0);

    setAppliedFilters({
      priceRange: draftPriceRange,
      inStockOnly: draftInStockOnly,
      minRating: draftMinRating,
    });

    setAppliedSort(draftSort);
    setAppliedSearch(draftSearch);
  };

  const openCreate = () => {
    setSelectedProduct(emptyProduct() as TProductDTO);
    setIsNew(true);
    setDialogOpen(true);
  };

  const openEdit = (product: TProductDTO) => {
    setSelectedProduct(product);
    setIsNew(false);
    setDialogOpen(true);
  };

  const openDelete = (product: TProductDTO) => {
    setSelectedProduct(product);
    setDeleteOpen(true);
  };

  const handleSubmit = async (values: TProductCreateDTO) => {
    try {
      if (isNew) {
        await ProductApi.createProduct(values);
        toast.success('Product created');
      } else if (selectedProduct) {
        await ProductApi.updateProduct(selectedProduct.id, values);
        toast.success('Product updated');
      }
      setDialogOpen(false);
      await loadProducts();
    } catch (err) {
      toastApiError(err);
    }
  };

  const handleDelete = async () => {
    if (!selectedProduct) return;
    try {
      await ProductApi.deleteProduct(selectedProduct.id);
      toast.success('Product deleted');
      await loadProducts();
    } catch (err) {
      toastApiError(err);
    } finally {
      setDeleteOpen(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-8 lg:flex-row">
        <ProductFilters
          priceRange={draftPriceRange}
          setPriceRange={setDraftPriceRange}
          inStockOnly={draftInStockOnly}
          setInStockOnly={setDraftInStockOnly}
          minRating={draftMinRating}
          setMinRating={setDraftMinRating}
          sort={draftSort}
          setSort={setDraftSort}
          search={draftSearch}
          setSearch={setDraftSearch}
          onApplyFilters={handleApplyFilters}
        />

        <div className="flex-1 space-y-6">
          <div className="flex flex-wrap items-center gap-4">
            <Button
              className="ml-auto"
              onClick={openCreate}
            >
              <Plus className="h-4 w-4" />
              Add Product
            </Button>
          </div>

          <ProductList
            products={products}
            loading={loading}
            onEdit={openEdit}
            onDelete={openDelete}
          />

          <Pagination
            page={page}
            limit={limit}
            totalPages={totalPages}
            onPageChange={setPage}
            onLimitChange={setLimit}
          />
        </div>
      </div>

      <ProductDialog
        open={dialogOpen}
        product={selectedProduct}
        isNew={isNew}
        onClose={() => setDialogOpen(false)}
        onSubmit={(values) => {
          void handleSubmit(values);
        }}
      />

      <ProductDeleteDialog
        open={deleteOpen}
        product={selectedProduct}
        onClose={() => setDeleteOpen(false)}
        onConfirm={() => void handleDelete()}
      />
    </div>
  );
};
