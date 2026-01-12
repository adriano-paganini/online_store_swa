'use client';

import { Plus } from 'lucide-react';
import { useCallback, useEffect, useState } from 'react';
import { toast } from 'sonner';

import type { TProductCreateDTO, TProductDTO } from '@/DTO/product.types';
import { ProductApi } from '@/utilities/productApi';

import { Pagination } from '@/components/general/Pagination';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

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
  const [limit, setLimit] = useState(12);
  const [totalPages, setTotalPages] = useState(0);
  const [sort, setSort] = useState('id,asc');

  const [search, setSearch] = useState('');

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
        sort,
      });

      const filtered = search
        ? res.data.filter(
            (p) =>
              p.name.toLowerCase().includes(search.toLowerCase()) ||
              p.description.toLowerCase().includes(search.toLowerCase())
          )
        : res.data;

      setProducts(filtered);
      setTotalPages(res.totalPages);
    } catch {
      toast.error('Failed to load products');
    } finally {
      setLoading(false);
    }
  }, [page, limit, sort, search]);

  useEffect(() => {
    void loadProducts();
  }, [loadProducts]);

  useEffect(() => {
    setPage(0);
  }, [search, sort, limit]);

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
    } catch {
      toast.error('Failed to save product');
    }
  };

  const handleDelete = async () => {
    if (!selectedProduct) return;
    try {
      await ProductApi.deleteProduct(selectedProduct.id);
      toast.success('Product deleted');
      await loadProducts();
    } catch {
      toast.error('Failed to delete product');
    } finally {
      setDeleteOpen(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center gap-4">
        <Input
          placeholder="Search by name or description…"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="max-w-sm"
        />

        <select
          value={sort}
          onChange={(e) => setSort(e.target.value)}
          className="rounded-md border px-3 py-2 text-sm"
        >
          <option value="id,asc">Default</option>
          <option value="name,asc">Name A–Z</option>
          <option value="name,desc">Name Z–A</option>
          <option value="price,asc">Price ↑</option>
          <option value="price,desc">Price ↓</option>
          <option value="stock,asc">Stock ↑</option>
          <option value="stock,desc">Stock ↓</option>
          <option value="discount,desc">Discount ↓</option>
        </select>

        <Button
          className="ml-auto"
          onClick={openCreate}
        >
          <Plus className="mr-2 h-4 w-4" />
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
