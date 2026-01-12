import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';

import type { TAddressDTO } from '@/DTO/address.types';
import { Check, X } from 'lucide-react';
import { useState } from 'react';
import { AddressForm } from './AddressForm';

type TAddressDialogProps = {
  open: boolean;
  address: TAddressDTO | null;
  onClose: () => void;
  onChanged: () => void;
};

export function AddressDialog({ open, address, onClose, onChanged }: TAddressDialogProps) {
  const [loading, setLoading] = useState(false);

  return (
    <Dialog
      open={open}
      onOpenChange={onClose}
    >
      <DialogContent className="max-w-lg">
        <DialogHeader>
          <DialogTitle>{address ? 'Edit address' : 'Add address'}</DialogTitle>
        </DialogHeader>

        <AddressForm
          address={address}
          onSuccess={() => {
            onClose();
            onChanged();
          }}
          setLoading={setLoading}
        />

        <DialogFooter>
          <Button
            variant="outline"
            onClick={onClose}
            disabled={loading}
          >
            <X />
            Cancel
          </Button>

          <Button
            type="submit"
            form="address-form"
            disabled={loading}
          >
            <Check />
            {address ? 'Save' : 'Add'} address
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
