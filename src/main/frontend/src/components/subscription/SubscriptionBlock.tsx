'use client';

import { useEffect, useState } from 'react';
import { toast } from 'sonner';

import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';

import type { TProductDTO } from '@/DTO/product.types';
import type { TPopulatedSubscriptionDTO, TSubscriptionCreateDTO } from '@/DTO/subscription.types';

import { SubscriptionApi } from '@/utilities/subscriptionApi';

import { NotificationTypeLabels } from '@/utilities/notificationUtils';
import { SubscriptionTypeLabels } from '@/utilities/subscriptionUtils';
import { MoreHorizontal, PenLine, Trash2 } from 'lucide-react';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from '../ui/dropdown-menu';
import { SubscriptionDeleteDialog } from './SubscriptionDeleteDialog';
import { SubscriptionDialog } from './SubscriptionDialog';

type TSubscriptionBlockProps = {
  product: TProductDTO;
};

export const SubscriptionBlock = ({ product }: TSubscriptionBlockProps) => {
  const [subscription, setSubscription] = useState<TPopulatedSubscriptionDTO | null>(null);
  const [loading, setLoading] = useState(false);

  const [dialogOpen, setDialogOpen] = useState(false);
  const [deleteOpen, setDeleteOpen] = useState(false);

  useEffect(() => {
    const loadSubscription = async () => {
      try {
        // ToDo: change to get by productId enpoint when available
        const page = await SubscriptionApi.getUserSubscriptionsPagePopulated({
          page: 0,
          limit: 50,
        });

        const existing = page.data.find((s) => s.product.id === product.id);

        setSubscription(existing ?? null);
      } catch {
        setSubscription(null);
      }
    };

    void loadSubscription();
  }, [product.id]);

  const handleCreateOrUpdate = async (values: TSubscriptionCreateDTO) => {
    try {
      setLoading(true);

      if (subscription) {
        await SubscriptionApi.updateSubscription(subscription.id, {
          types: values.types,
          channels: values.channels,
        });
      } else {
        await SubscriptionApi.createSubscription(values);
      }

      const page = await SubscriptionApi.getUserSubscriptionsPagePopulated({
        page: 0,
        limit: 50,
      });

      const updated = page.data.find((s) => s.product.id === product.id);

      setSubscription(updated ?? null);

      toast.success(subscription ? 'Subscription updated' : 'Subscription created');
      setDialogOpen(false);
    } catch {
      toast.error('Failed to save subscription');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async () => {
    if (!subscription) return;

    try {
      setLoading(true);
      await SubscriptionApi.deleteSubscription(subscription.id);
      setSubscription(null);
      toast.success('Subscription deleted');
      setDeleteOpen(false);
    } catch {
      toast.error('Failed to delete subscription');
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Separator />

      <div className="space-y-3">
        {!subscription && (
          <Button
            variant="outline"
            onClick={() => setDialogOpen(true)}
          >
            Subscribe to changes of this product
          </Button>
        )}

        {subscription && (
          <div className="flex items-center rounded-lg border p-4">
            <div className="flex flex-1 flex-col gap-2">
              <p className="text-sm">You are subscribed to updates for this product.</p>

              <div className="flex flex-col">
                <div className="text-sm text-muted-foreground">
                  Track: {subscription.types.map((t) => SubscriptionTypeLabels[t]).join(', ')}
                </div>

                <div className="text-sm text-muted-foreground">
                  Send by: {subscription.channels.map((channel) => NotificationTypeLabels[channel]).join(', ')}
                </div>
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
                <DropdownMenuItem onClick={() => setDialogOpen(true)}>
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
        )}
      </div>

      <SubscriptionDialog
        open={dialogOpen}
        subscription={subscription}
        product={product}
        onClose={() => setDialogOpen(false)}
        onSubmit={(e) => void handleCreateOrUpdate(e)}
        loading={loading}
      />

      <SubscriptionDeleteDialog
        open={deleteOpen}
        product={product}
        onClose={() => setDeleteOpen(false)}
        onConfirm={() => void handleDelete()}
      />
    </>
  );
};
