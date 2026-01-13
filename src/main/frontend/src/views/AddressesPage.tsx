'use client';

import { useCallback, useEffect, useState } from 'react';
import { toast } from 'sonner';

import { AddressDialog } from '@/components/address/AddressDialog';
import { AddressTable } from '@/components/address/AddressTable';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import type { TAddressDTO } from '@/DTO/address.types';
import { AddressApi } from '@/utilities/addressApi';
import { Plus, Search } from 'lucide-react';

export default function AddressesPage() {
  const [addresses, setAddresses] = useState<TAddressDTO[]>([]);
  const [search, setSearch] = useState('');
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

  const filteredAddresses = addresses.filter((address) => {
    const query = search.toLowerCase();

    return (
      address.street.toLowerCase().includes(query) ||
      address.number.toLowerCase().includes(query) ||
      address.city.toLowerCase().includes(query) ||
      address.postalCode.toLowerCase().includes(query) ||
      address.country.toLowerCase().includes(query) ||
      address.extra?.toLowerCase().includes(query)
    );
  });

  useEffect(() => {
    if (!createOpen) setSearch('');
  }, [createOpen]);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div className="relative">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Search addresses…"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="pl-9"
          />
        </div>

        <Button onClick={() => setCreateOpen(true)}>
          <Plus />
          Add address
        </Button>
      </div>

      <AddressTable
        addresses={filteredAddresses}
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
