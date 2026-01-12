import type { TProductCreateDTO, TProductDTO } from '@/DTO/product.types';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { useState } from 'react';

type TProductFormProps = {
  product: TProductDTO;
  onSubmit: (values: TProductCreateDTO) => void;
};

export const ProductForm = ({ product, onSubmit }: TProductFormProps) => {
  const [values, setValues] = useState<TProductCreateDTO>({
    name: product.name,
    images: product.images,
    description: product.description,
    price: product.price,
    stock: product.stock,
    discount: product.discount,
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setValues({ ...values, [name]: name === 'name' || name === 'description' ? value : Number(value) });
  };

  return (
    <form
      className="space-y-4"
      onSubmit={(e) => {
        e.preventDefault();
        onSubmit(values);
      }}
    >
      <div>
        <Label>Name</Label>
        <Input
          name="name"
          value={values.name}
          onChange={handleChange}
        />
      </div>

      <div>
        <Label>Image URLs (comma separated)</Label>
        <Input
          name="imageUrls"
          value={values.images.join(', ')}
          onChange={(e) => setValues({ ...values, images: e.target.value.split(',').map((url) => url.trim()) })}
        />
      </div>

      <div>
        <Label>Description</Label>
        <Input
          name="description"
          value={values.description}
          onChange={handleChange}
        />
      </div>

      <div>
        <Label>Price</Label>
        <Input
          type="number"
          name="price"
          value={values.price}
          onChange={handleChange}
        />
      </div>

      <div>
        <Label>Stock</Label>
        <Input
          type="number"
          name="stock"
          value={values.stock}
          onChange={handleChange}
        />
      </div>

      <div>
        <Label>Discount (0–1)</Label>
        <Input
          type="number"
          step="0.01"
          name="discount"
          value={values.discount}
          onChange={handleChange}
        />
      </div>

      <Button
        type="submit"
        className="w-full"
      >
        Save
      </Button>
    </form>
  );
};
