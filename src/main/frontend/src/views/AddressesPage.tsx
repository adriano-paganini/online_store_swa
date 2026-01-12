'use client';

import { useCallback, useEffect, useState } from 'react';
import { toast } from 'sonner';

import { AddressDialog } from '@/components/address/AddressDialog';
import { AddressTable } from '@/components/address/AddressTable';
import { Button } from '@/components/ui/button';
import type { TAddressDTO } from '@/DTO/address.types';
import { AddressApi } from '@/utilities/addressApi';
import { Plus } from 'lucide-react';

export default function AddressesPage() {
  const [addresses, setAddresses] = useState<TAddressDTO[]>([]);
  const [loading, setLoading] = useState(true);
  const [createOpen, setCreateOpen] = useState(false);

  const loadAddresses = useCallback(async () => {
    try {
      setLoading(true);
      const data = await AddressApi.fetchAddresses();
      setAddresses(data);
    } catch {
      toast.error('Failed to load addresses');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadAddresses();
  }, [loadAddresses]);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <Button onClick={() => setCreateOpen(true)}>
          <Plus />
          Add address
        </Button>
      </div>

      <AddressTable
        addresses={addresses}
        loading={loading}
        onChanged={() => void loadAddresses()}
      />

      <AddressDialog
        open={createOpen}
        address={null}
        onClose={() => setCreateOpen(false)}
        onChanged={() => void loadAddresses()}
      />
    </div>
  );
}
