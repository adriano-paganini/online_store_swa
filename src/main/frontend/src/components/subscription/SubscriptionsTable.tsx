'use client';

import { Skeleton } from '@/components/ui/skeleton';
import { TPopulatedSubscriptionDTO } from '@/DTO/subscription.types';
import { SubscriptionRow } from './SubscriptionRow';

type TSubscriptionTableProps = {
  subscriptions: TPopulatedSubscriptionDTO[];
  loading: boolean;
  onChanged: () => void;
};

export function SubscriptionTable({ subscriptions, loading, onChanged }: TSubscriptionTableProps) {
  if (loading) {
    return (
      <div className="space-y-2">
        {Array.from({ length: 8 }).map((_, i) => (
          <Skeleton
            key={i}
            className="h-12 w-full rounded-md"
          />
        ))}
      </div>
    );
  }

  if (subscriptions.length === 0) {
    return <p className="text-muted-foreground">No subscriptions found.</p>;
  }

  return (
    <div className="rounded-lg border">
      <div className="grid grid-cols-6 gap-4 border-b px-4 py-2 text-sm font-medium">
        <span>Product</span>
        <span>Price</span>
        <span>Types</span>
        <span>Channels</span>
        <span>Status</span>
        <span>Actions</span>
      </div>

      {subscriptions.map((subscription) => (
        <SubscriptionRow
          key={subscription.id}
          subscription={subscription}
          onChanged={onChanged}
        />
      ))}
    </div>
  );
}
