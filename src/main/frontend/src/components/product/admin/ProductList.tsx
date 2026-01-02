import type { TProductDTO } from '@/DTO/product.types';
import { ProductRow } from './ProductRow';

type TProductListProps = {
  products: TProductDTO[];
  loading: boolean;
  onEdit: (p: TProductDTO) => void;
  onDelete: (p: TProductDTO) => void;
};

export const ProductList = ({ products, loading, onEdit, onDelete }: TProductListProps) => {
  if (loading) return <p className="text-muted-foreground">Loading…</p>;
  if (products.length === 0) return <p className="text-muted-foreground">No products found.</p>;

  return (
    <div className="overflow-hidden rounded-lg">
      {products.map((p) => (
        <ProductRow
          key={p.id}
          product={p}
          onEdit={onEdit}
          onDelete={onDelete}
        />
      ))}
    </div>
  );
};
