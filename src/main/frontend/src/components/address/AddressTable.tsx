'use client';

import { Skeleton } from '@/components/ui/skeleton';
import type { TAddressDTO } from '@/DTO/address.types';
import { AddressRow } from './AddressRow';

type TAddressTableProps = {
  addresses: TAddressDTO[];
  loading: boolean;
  onChanged: () => void;
};

export function AddressTable({ addresses, loading, onChanged }: TAddressTableProps) {
  if (loading) {
    return (
      <div className="space-y-2 rounded-lg border">
        {Array.from({ length: 4 }).map((_, i) => (
          <Skeleton
            key={i}
            className="h-20 w-full rounded-none first:rounded-t-lg last:rounded-b-lg"
          />
        ))}
      </div>
    );
  }

  if (addresses.length === 0) {
    return <p className="text-muted-foreground">No addresses added yet.</p>;
  }

  return (
    <div className="divide-y rounded-lg border">
      {addresses.map((address) => (
        <AddressRow
          key={address.id}
          address={address}
          onChanged={onChanged}
        />
      ))}
    </div>
  );
}
