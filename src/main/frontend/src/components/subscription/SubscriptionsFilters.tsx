'use client';

import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';

import { NotificationType } from '@/DTO/notification.types';
import { SubscriptionType } from '@/DTO/subscription.types';
import { NotificationTypeLabels } from '@/utilities/notificationUtils';
import { SubscriptionTypeLabels } from '@/utilities/subscriptionUtils';

type TSubscriptionFiltersProps = {
  types: SubscriptionType[];
  setTypes: (v: SubscriptionType[]) => void;
  channels: NotificationType[];
  setChannels: (v: NotificationType[]) => void;
  sort: string;
  setSort: (v: string) => void;
  onApplyFilters: () => void;
};

export function SubscriptionFilters({
  types,
  setTypes,
  channels,
  setChannels,
  sort,
  setSort,
  onApplyFilters,
}: TSubscriptionFiltersProps) {
  const toggle = <T,>(value: T, list: T[], set: (v: T[]) => void) => {
    set(list.includes(value) ? list.filter((v) => v !== value) : [...list, value]);
  };

  return (
    <aside className="w-full space-y-6 lg:w-64">
      <div className="space-y-4 rounded-lg border p-4">
        <div>
          <Label className="mb-2 block">Subscription Types</Label>
          <div className="space-y-2">
            {Object.values(SubscriptionType).map((type) => (
              <div
                key={type}
                className="flex items-center gap-2"
              >
                <Checkbox
                  checked={types.includes(type)}
                  onCheckedChange={() => toggle(type, types, setTypes)}
                />
                <span className="text-sm">{SubscriptionTypeLabels[type]}</span>
              </div>
            ))}
          </div>
        </div>

        <div>
          <Label className="mb-2 block">Channels</Label>
          <div className="space-y-2">
            {Object.values(NotificationType).map((channel) => (
              <div
                key={channel}
                className="flex items-center gap-2"
              >
                <Checkbox
                  checked={channels.includes(channel)}
                  onCheckedChange={() => toggle(channel, channels, setChannels)}
                />
                <span className="text-sm">{NotificationTypeLabels[channel]}</span>
              </div>
            ))}
          </div>
        </div>

        <div>
          <Label
            htmlFor="sort"
            className="mb-2 block"
          >
            Sort By
          </Label>

          <select
            id="sort"
            value={sort}
            onChange={(e) => setSort(e.target.value)}
            className="w-full rounded-md border px-3 py-2 text-sm"
          >
            <option value="id,desc">Newest subscriptions</option>
            <option value="id,asc">Oldest subscriptions</option>
          </select>
        </div>

        <Button
          className="w-full"
          onClick={onApplyFilters}
        >
          Apply
        </Button>
      </div>
    </aside>
  );
}
