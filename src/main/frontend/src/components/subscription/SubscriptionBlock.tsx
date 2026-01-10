'use client';

import { useEffect, useState } from 'react';
import { toast } from 'sonner';

import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';

import type { TProductDTO } from '@/DTO/product.types';
import type { TPopulatedSubscriptionDTO, TSubscriptionCreateDTO } from '@/DTO/subscription.types';

import { SubscriptionApi } from '@/utilities/subscriptionApi';

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
        const page = await SubscriptionApi.getUserSubscriptionsPagePopulated({
          page: 0,
          limit: 50, // safe upper bound for "single product lookup"
        });

        const existing = page.data.find((s) => s.product.id === product.id);

        setSubscription(existing ?? null);
      } catch {
        // Non-blocking: product page must still work
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
          <div className="space-y-3 rounded-lg border p-4">
            <p className="text-sm text-muted-foreground">You are subscribed to updates for this product.</p>

            <div className="flex flex-wrap gap-2">
              {subscription.types.map((type) => (
                <Badge
                  key={type}
                  variant="secondary"
                >
                  {type}
                </Badge>
              ))}
              {subscription.channels.map((channel) => (
                <Badge key={channel}>{channel}</Badge>
              ))}
            </div>

            <div className="flex gap-2">
              <Button
                size="sm"
                variant="outline"
                onClick={() => setDialogOpen(true)}
              >
                Edit
              </Button>
              <Button
                size="sm"
                variant="destructive"
                onClick={() => setDeleteOpen(true)}
              >
                Unsubscribe
              </Button>
            </div>
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
