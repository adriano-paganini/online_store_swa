import { useState } from 'react';

import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';

import { NotificationType } from '@/DTO/notification.types';
import type { TProductDTO } from '@/DTO/product.types';
import type { TPopulatedSubscriptionDTO, TSubscriptionCreateDTO } from '@/DTO/subscription.types';
import { SubscriptionType } from '@/DTO/subscription.types';
import { NotificationTypeLabels } from '@/utilities/notificationUtils';
import { SubscriptionTypeLabels } from '@/utilities/subscriptionUtils';

type TSubscriptionFormProps = {
  product: TProductDTO;
  subscription: TPopulatedSubscriptionDTO | null;
  onSubmit: (values: TSubscriptionCreateDTO) => void;
};

export const SubscriptionForm = ({ product, subscription, onSubmit }: TSubscriptionFormProps) => {
  const [types, setTypes] = useState<SubscriptionType[]>(subscription?.types ?? [SubscriptionType.RESTOCK]);
  const [channels, setChannels] = useState<NotificationType[]>(subscription?.channels ?? [NotificationType.EMAIL]);

  const toggle = <T,>(value: T, list: T[], setList: (v: T[]) => void) => {
    setList(list.includes(value) ? list.filter((v) => v !== value) : [...list, value]);
  };

  return (
    <form
      id="subscription-form"
      className="space-y-6"
      onSubmit={(e) => {
        e.preventDefault();
        onSubmit({
          productId: product.id,
          types,
          channels,
        });
      }}
    >
      <div className="space-y-2">
        <Label>Notify me about</Label>
        {Object.values(SubscriptionType).map((type) => (
          <div
            key={type}
            className="flex items-center gap-2"
          >
            <Checkbox
              checked={types.includes(type)}
              onCheckedChange={() => toggle(type, types, setTypes)}
            />
            <span>{SubscriptionTypeLabels[type]}</span>
          </div>
        ))}
      </div>

      <div className="space-y-2">
        <Label>Notification channels</Label>
        {Object.values(NotificationType).map((channel) => (
          <div
            key={channel}
            className="flex items-center gap-2"
          >
            <Checkbox
              checked={channels.includes(channel)}
              onCheckedChange={() => toggle(channel, channels, setChannels)}
            />
            <span>{NotificationTypeLabels[channel]}</span>
          </div>
        ))}
      </div>
    </form>
  );
};
