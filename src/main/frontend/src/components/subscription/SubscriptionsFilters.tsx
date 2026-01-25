'use client';

import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';

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
  const selectedType = types[0] ?? 'all';
  const selectedChannel = channels[0] ?? 'all';

  return (
    <aside className="w-full space-y-6 lg:w-64">
      <div className="space-y-4 rounded-lg border p-4">
        <div>
          <Label className="mb-2 block">Subscription Types</Label>

          <RadioGroup
            value={selectedType}
            onValueChange={(value) => (value === 'all' ? setTypes([]) : setTypes([value as SubscriptionType]))}
          >
            <div className="flex items-center gap-2">
              <RadioGroupItem value="all" />
              <span className="text-sm font-medium">All</span>
            </div>

            {Object.values(SubscriptionType).map((type) => (
              <div
                key={type}
                className="flex items-center gap-2"
              >
                <RadioGroupItem value={type} />
                <span className="text-sm">{SubscriptionTypeLabels[type]}</span>
              </div>
            ))}
          </RadioGroup>
        </div>

        <div>
          <Label className="mb-2 block">Channels</Label>

          <RadioGroup
            value={selectedChannel}
            onValueChange={(value) => (value === 'all' ? setChannels([]) : setChannels([value as NotificationType]))}
          >
            <div className="flex items-center gap-2">
              <RadioGroupItem value="all" />
              <span className="text-sm font-medium">All</span>
            </div>

            {Object.values(NotificationType).map((channel) => (
              <div
                key={channel}
                className="flex items-center gap-2"
              >
                <RadioGroupItem value={channel} />
                <span className="text-sm">{NotificationTypeLabels[channel]}</span>
              </div>
            ))}
          </RadioGroup>
        </div>

        <div>
          <Label className="mb-2 block">Sort By</Label>

          <Select
            value={sort}
            onValueChange={setSort}
          >
            <SelectTrigger className="w-full">
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="id,desc">Newest subscriptions</SelectItem>
              <SelectItem value="id,asc">Oldest subscriptions</SelectItem>
            </SelectContent>
          </Select>
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
