import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import type { TProductCreateDTO, TProductDTO } from '@/DTO/product.types';
import { Check, X } from 'lucide-react';
import { ProductForm } from './ProductForm';

type TProductDialogProps = {
  open: boolean;
  product: TProductDTO | null;
  isNew: boolean;
  onClose: () => void;
  onSubmit: (values: TProductCreateDTO) => void;
};

export const ProductDialog = ({ open, product, isNew, onClose, onSubmit }: TProductDialogProps) => {
  if (!product) return null;

  return (
    <Dialog
      open={open}
      onOpenChange={onClose}
    >
      <DialogContent className="max-w-lg">
        <DialogHeader>
          <DialogTitle>{isNew ? 'Create Product' : 'Edit Product'}</DialogTitle>
        </DialogHeader>

        <ProductForm
          product={product}
          onSubmit={onSubmit}
        />

        <DialogFooter>
          <Button
            variant="outline"
            onClick={onClose}
          >
            <X className="h-4 w-4" />
            Cancel
          </Button>

          <Button
            type="submit"
            form="product-form"
          >
            <Check className="h-4 w-4" />
            {isNew ? 'Add' : 'Save'} product
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};
