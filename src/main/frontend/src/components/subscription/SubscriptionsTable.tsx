'use client';

import { Skeleton } from '@/components/ui/skeleton';
import type { TPopulatedSubscriptionDTO } from '@/DTO/subscription.types';
import { SubscriptionRow } from './SubscriptionRow';

type TSubscriptionTableProps = {
  subscriptions: TPopulatedSubscriptionDTO[];
  loading: boolean;
  onChanged: () => void;
};

export function SubscriptionTable({ subscriptions, loading, onChanged }: TSubscriptionTableProps) {
  if (loading) {
    return (
      <div className="space-y-2 rounded-lg border">
        {Array.from({ length: 6 }).map((_, i) => (
          <Skeleton
            key={i}
            className="h-24 w-full rounded-none first:rounded-t-lg last:rounded-b-lg"
          />
        ))}
      </div>
    );
  }

  if (subscriptions.length === 0) {
    return <p className="text-muted-foreground">No subscriptions found.</p>;
  }

  return (
    <div className="divide-y rounded-lg border">
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
