'use client';

import { MoreHorizontal, PenLine, Trash2 } from 'lucide-react';
import { useState } from 'react';
import { toast } from 'sonner';

import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';

import { NotificationTypeLabels } from '@/utilities/notificationUtils';
import { SubscriptionApi } from '@/utilities/subscriptionApi';
import { SubscriptionTypeLabels } from '@/utilities/subscriptionUtils';

import type { TPopulatedSubscriptionDTO, TSubscriptionUpdateDTO } from '@/DTO/subscription.types';

import { SubscriptionDeleteDialog } from '@/components/subscription/SubscriptionDeleteDialog';
import { SubscriptionDialog } from '@/components/subscription/SubscriptionDialog';
import { getInitials } from '@/lib/utils';
import { Avatar, AvatarFallback, AvatarImage } from '../ui/avatar';

type TSubscriptionRowProps = {
  subscription: TPopulatedSubscriptionDTO;
  onChanged: () => void;
};

export function SubscriptionRow({ subscription, onChanged }: TSubscriptionRowProps) {
  const { product } = subscription;

  const [editOpen, setEditOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleUpdate = async (values: TSubscriptionUpdateDTO) => {
    try {
      setLoading(true);
      await SubscriptionApi.updateSubscription(subscription.id, values);
      toast.success('Subscription updated');
      setEditOpen(false);
      onChanged();
    } catch {
      toast.error('Failed to update subscription');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    try {
      setLoading(true);
      await SubscriptionApi.deleteSubscription(subscription.id);
      toast.success('Unsubscribed successfully');
      setDeleteOpen(false);
      onChanged();
    } catch {
      toast.error('Failed to unsubscribe');
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <div className="flex items-center gap-3 border-b px-4 py-3 text-sm transition-opacity hover:bg-muted">
        <Avatar className="h-16 w-16 rounded-md">
          <AvatarImage
            src={product.images?.[0]}
            alt={product.name}
            className="object-cover"
          />
          <AvatarFallback className="rounded-md bg-muted text-sm font-medium">
            {getInitials(product.name)}
          </AvatarFallback>
        </Avatar>

        <div className="min-w-0 flex-1 space-y-1">
          <div className="truncate font-medium">{product.name}</div>

          <div className="text-sm text-muted-foreground">
            Track: {subscription.types.map((t) => SubscriptionTypeLabels[t]).join(', ')}
          </div>

          <div className="text-sm text-muted-foreground">
            Send by: {subscription.channels.map((channel) => NotificationTypeLabels[channel]).join(', ')}
          </div>
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
              Unsubscribe
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>

      <SubscriptionDialog
        open={editOpen}
        subscription={subscription}
        product={product}
        loading={loading}
        onClose={() => setEditOpen(false)}
        onSubmit={(e) => void handleUpdate(e)}
      />

      <SubscriptionDeleteDialog
        open={deleteOpen}
        product={product}
        onClose={() => setDeleteOpen(false)}
        onConfirm={() => void handleDelete()}
      />
    </>
  );
}
