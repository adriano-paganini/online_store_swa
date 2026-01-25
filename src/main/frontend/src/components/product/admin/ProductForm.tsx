import type { TProductCreateDTO, TProductDTO } from '@/DTO/product.types';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { ArrowLeft, ArrowRight, Trash2 } from 'lucide-react';
import { useState } from 'react';

type TProductFormProps = {
  product: TProductDTO;
  onSubmit: (values: TProductCreateDTO) => void;
};

export const ProductForm = ({ product, onSubmit }: TProductFormProps) => {
  const [values, setValues] = useState<TProductCreateDTO>({
    name: product.name,
    images: product.images ?? [],
    description: product.description,
    price: product.price,
    stock: product.stock,
    discount: product.discount,
  });

  const [imageInput, setImageInput] = useState('');
  const [errors, setErrors] = useState<Partial<Record<keyof TProductCreateDTO | 'images', string>>>({});

  const validate = (): boolean => {
    const nextErrors: Partial<Record<keyof TProductCreateDTO | 'images', string>> = {};

    if (!values.name.trim()) nextErrors.name = 'Name is required';
    if (!values.description.trim()) nextErrors.description = 'Description is required';
    if (values.price <= 0) nextErrors.price = 'Price must be greater than 0';
    if (values.stock < 0) nextErrors.stock = 'Stock cannot be negative';
    if (values.discount < 0 || values.discount > 1) nextErrors.discount = 'Discount must be between 0 and 1';
    if (values.images.length === 0) nextErrors.images = 'At least one image is required';

    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const handleSubmit = () => {
    if (!validate()) return;
    onSubmit(values);
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;

    setValues({
      ...values,
      [name]: name === 'name' || name === 'description' ? value : Number(value),
    });

    setErrors((prev) => ({ ...prev, [name]: undefined }));
  };

  const addImage = () => {
    const url = imageInput.trim();
    if (!url) return;

    setValues((prev) => ({
      ...prev,
      images: [...prev.images, url],
    }));

    setImageInput('');
    setErrors((prev) => ({ ...prev, images: undefined }));
  };

  const removeImage = (index: number) => {
    setValues((prev) => ({
      ...prev,
      images: prev.images.filter((_, i) => i !== index),
    }));
  };

  const moveImage = (from: number, to: number) => {
    setValues((prev) => {
      const next = [...prev.images];
      const [moved] = next.splice(from, 1);
      next.splice(to, 0, moved);
      return { ...prev, images: next };
    });
  };

  return (
    <form
      id="product-form"
      className="space-y-4"
      onSubmit={(e) => {
        e.preventDefault();
        handleSubmit();
      }}
    >
      <div className="space-y-1">
        <Label>Name</Label>
        <Input
          name="name"
          value={values.name}
          onChange={handleChange}
        />
        {errors.name && <p className="text-xs text-destructive">{errors.name}</p>}
      </div>

      <div className="space-y-2">
        <Label>Images</Label>

        <div className="flex gap-2">
          <Input
            placeholder="Paste image URL"
            value={imageInput}
            onChange={(e) => setImageInput(e.target.value)}
          />
          <Button
            type="button"
            variant="outline"
            onClick={addImage}
          >
            Add
          </Button>
        </div>

        {errors.images && <p className="text-xs text-destructive">{errors.images}</p>}

        {values.images.length > 0 && (
          <div className="grid grid-cols-3 gap-2">
            {values.images.map((src, index) => (
              <div
                key={index}
                className="group relative overflow-hidden rounded-md border"
              >
                <img
                  src={src}
                  alt={`Product image ${index + 1}`}
                  className="aspect-square w-full object-cover"
                />

                <div className="absolute inset-x-1 top-1 flex justify-between opacity-0 transition group-hover:opacity-100">
                  <Button
                    type="button"
                    size="icon"
                    variant="secondary"
                    disabled={index === 0}
                    onClick={() => moveImage(index, index - 1)}
                  >
                    <ArrowLeft className="h-4 w-4" />
                  </Button>

                  <Button
                    type="button"
                    size="icon"
                    variant="secondary"
                    disabled={index === values.images.length - 1}
                    onClick={() => moveImage(index, index + 1)}
                  >
                    <ArrowRight className="h-4 w-4" />
                  </Button>
                </div>

                <button
                  type="button"
                  onClick={() => removeImage(index)}
                  className="absolute bottom-1 right-1 rounded-full bg-background/80 p-1 opacity-0 transition group-hover:opacity-100"
                >
                  <Trash2 className="h-4 w-4 text-destructive" />
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      <div className="space-y-1">
        <Label>Description</Label>
        <Input
          name="description"
          value={values.description}
          onChange={handleChange}
        />
        {errors.description && <p className="text-xs text-destructive">{errors.description}</p>}
      </div>

      <div className="space-y-1">
        <Label>Price</Label>
        <Input
          type="number"
          name="price"
          value={values.price}
          onChange={handleChange}
        />
        {errors.price && <p className="text-xs text-destructive">{errors.price}</p>}
      </div>

      <div className="space-y-1">
        <Label>Stock</Label>
        <Input
          type="number"
          name="stock"
          value={values.stock}
          onChange={handleChange}
        />
        {errors.stock && <p className="text-xs text-destructive">{errors.stock}</p>}
      </div>

      <div className="space-y-1">
        <Label>Discount (0–1)</Label>
        <Input
          type="number"
          step="0.01"
          name="discount"
          value={values.discount}
          onChange={handleChange}
        />
        {errors.discount && <p className="text-xs text-destructive">{errors.discount}</p>}
      </div>
    </form>
  );
};
