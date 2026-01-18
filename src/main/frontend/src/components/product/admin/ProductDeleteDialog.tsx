import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import type { TProductDTO } from '@/DTO/product.types';
import { Trash2, X } from 'lucide-react';

type TProductDeleteDialogProps = {
  open: boolean;
  product: TProductDTO | null;
  onClose: () => void;
  onConfirm: () => void;
};

export const ProductDeleteDialog = ({ open, product, onClose, onConfirm }: TProductDeleteDialogProps) => {
  if (!product) return null;

  return (
    <Dialog
      open={open}
      onOpenChange={onClose}
    >
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Delete product</DialogTitle>
        </DialogHeader>

        <p>
          Are you sure you want to delete <strong>{product.name}</strong>?
        </p>

        <DialogFooter>
          <Button
            variant="outline"
            onClick={onClose}
          >
            <X />
            Cancel
          </Button>
          <Button
            variant="destructive"
            onClick={onConfirm}
          >
            <Trash2 />
            Delete
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};
