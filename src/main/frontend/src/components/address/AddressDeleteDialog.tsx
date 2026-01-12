import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import type { TAddressDTO } from '@/DTO/address.types';

type TAddressDeleteDialogProps = {
  open: boolean;
  address: TAddressDTO;
  onClose: () => void;
  onConfirm: () => void;
};

export function AddressDeleteDialog({ open, address, onClose, onConfirm }: TAddressDeleteDialogProps) {
  return (
    <Dialog
      open={open}
      onOpenChange={onClose}
    >
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Delete address</DialogTitle>
        </DialogHeader>

        <p>
          Delete address at{' '}
          <strong>
            {address.street} {address.number}, {address.city}
          </strong>
          ?
        </p>

        <DialogFooter>
          <Button
            variant="outline"
            onClick={onClose}
          >
            Cancel
          </Button>
          <Button
            variant="destructive"
            onClick={onConfirm}
          >
            Delete
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
