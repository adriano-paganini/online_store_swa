import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import type { TProductDTO } from '@/DTO/product.types';
import type { TPopulatedSubscriptionDTO, TSubscriptionCreateDTO } from '@/DTO/subscription.types';
import { SubscriptionForm } from './SubscriptionForm';

type TSubscriptionDialogProps = {
  open: boolean;
  subscription: TPopulatedSubscriptionDTO | null;
  product: TProductDTO;
  loading: boolean;
  onClose: () => void;
  onSubmit: (values: TSubscriptionCreateDTO) => void;
};

export const SubscriptionDialog = ({
  open,
  subscription,
  product,
  loading,
  onClose,
  onSubmit,
}: TSubscriptionDialogProps) => {
  return (
    <Dialog
      open={open}
      onOpenChange={onClose}
    >
      <DialogContent className="max-w-lg">
        <DialogHeader>
          <DialogTitle>{subscription ? 'Edit subscription' : 'Create subscription'}</DialogTitle>
        </DialogHeader>

        <SubscriptionForm
          product={product}
          subscription={subscription}
          onSubmit={onSubmit}
        />

        <DialogFooter>
          <Button
            variant="outline"
            onClick={onClose}
            disabled={loading}
          >
            Cancel
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};
