'use client';

import { useState } from 'react';

import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';

import { SubscriptionDeleteDialog } from '@/components/subscription/SubscriptionDeleteDialog';
import { SubscriptionDialog } from '@/components/subscription/SubscriptionDialog';
import { TPopulatedSubscriptionDTO } from '@/DTO/subscription.types';
import { NotificationTypeLabels } from '@/utilities/notificationUtils';
import { SubscriptionTypeLabels } from '@/utilities/subscriptionUtils';

type TSubscriptionRowProps = {
  subscription: TPopulatedSubscriptionDTO;
  onChanged: () => void;
};

export function SubscriptionRow({ subscription, onChanged }: TSubscriptionRowProps) {
  const [editOpen, setEditOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);

  const { product } = subscription;

  return (
    <>
      <div className="grid grid-cols-6 items-center gap-4 border-b px-4 py-3 text-sm">
        <div className="flex items-center gap-3">
          <img
            src={product.images[0]}
            alt={product.name}
            className="h-10 w-10 rounded object-cover"
          />
          <span className="font-medium">{product.name}</span>
        </div>

        <span>${product.price.toFixed(2)}</span>

        <div className="flex flex-wrap gap-1">
          {subscription.types.map((type) => (
            <Badge
              key={type}
              variant="secondary"
            >
              {SubscriptionTypeLabels[type]}
            </Badge>
          ))}
        </div>

        <div className="flex flex-wrap gap-1">
          {subscription.channels.map((channel) => (
            <Badge key={channel}>{NotificationTypeLabels[channel]}</Badge>
          ))}
        </div>

        <span className="text-muted-foreground">Active</span>

        <div className="flex gap-2">
          <Button
            size="sm"
            variant="outline"
            onClick={() => setEditOpen(true)}
          >
            Edit
          </Button>
          <Button
            size="sm"
            variant="destructive"
            onClick={() => setDeleteOpen(true)}
          >
            Delete
          </Button>
        </div>
      </div>

      <SubscriptionDialog
        open={editOpen}
        subscription={subscription}
        product={product}
        onClose={() => setEditOpen(false)}
        onSubmit={() => {
          setEditOpen(false);
          onChanged();
        }}
        loading={false}
      />

      <SubscriptionDeleteDialog
        open={deleteOpen}
        product={product}
        onClose={() => setDeleteOpen(false)}
        onConfirm={() => {
          setDeleteOpen(false);
          onChanged();
        }}
      />
    </>
  );
}
