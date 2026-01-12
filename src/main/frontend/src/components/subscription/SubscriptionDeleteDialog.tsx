import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import type { TProductDTO } from '@/DTO/product.types';
import { Trash2, X } from 'lucide-react';

type TSubscriptionDeleteDialogProps = {
  open: boolean;
  product: TProductDTO;
  onClose: () => void;
  onConfirm: () => void;
};

export const SubscriptionDeleteDialog = ({ open, product, onClose, onConfirm }: TSubscriptionDeleteDialogProps) => {
  return (
    <Dialog
      open={open}
      onOpenChange={onClose}
    >
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Unsubscribe</DialogTitle>
        </DialogHeader>

        <p>
          Stop receiving notifications for <strong>{product.name}</strong>?
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
            Unsubscribe
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};
