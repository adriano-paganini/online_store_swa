'use client';

import { MoreHorizontal, PenLine, Trash2 } from 'lucide-react';
import { useState } from 'react';
import { toast } from 'sonner';

import type { TAddressDTO } from '@/DTO/address.types';
import { AddressApi } from '@/utilities/addressApi';

import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';

import { toastApiError } from '@/lib/utils';
import { AddressDeleteDialog } from './AddressDeleteDialog';
import { AddressDialog } from './AddressDialog';

type TAddressRowProps = {
  address: TAddressDTO;
  onChanged: () => void;
};

export function AddressRow({ address, onChanged }: TAddressRowProps) {
  const [editOpen, setEditOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleDelete = async () => {
    try {
      setLoading(true);
      await AddressApi.deleteAddress(address.id);
      toast.success('Address deleted');
      setDeleteOpen(false);
      onChanged();
    } catch (err) {
      toastApiError(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <div className="flex items-center gap-3 border-b px-4 py-3 text-sm transition-opacity hover:bg-muted">
        <div className="flex-1 space-y-1">
          <div className="font-medium">
            {address.street} {address.number}
          </div>
          <div className="text-sm text-muted-foreground">
            {address.postalCode} {address.city}, {address.country}
          </div>
          {address.extra && <div className="text-sm text-muted-foreground">{address.extra}</div>}
        </div>

        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button
              variant="ghost"
              size="icon"
              disabled={loading}
            >
              <MoreHorizontal className="h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>

          <DropdownMenuContent align="end">
            <DropdownMenuItem onClick={() => setEditOpen(true)}>
              <PenLine className="mr-2 h-4 w-4" />
              Edit
            </DropdownMenuItem>

            <DropdownMenuItem
              className="text-destructive"
              onClick={() => setDeleteOpen(true)}
            >
              <Trash2 className="mr-2 h-4 w-4" />
              Delete
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      <AddressDialog
        open={editOpen}
        address={address}
        onClose={() => setEditOpen(false)}
        onChanged={onChanged}
      />

      <AddressDeleteDialog
        open={deleteOpen}
        address={address}
        onClose={() => setDeleteOpen(false)}
        onConfirm={() => void handleDelete()}
      />
    </>
  );
}
