'use client';

import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';
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
  const allTypesSelected = types.length === 0;
  const allChannelsSelected = channels.length === 0;

  const toggleType = (type: SubscriptionType) => {
    if (types.includes(type)) {
      const next = types.filter((t) => t !== type);
      setTypes(next.length === 0 ? [] : next);
    } else {
      setTypes([...types, type]);
    }
  };

  const toggleAllTypes = () => {
    setTypes([]);
  };

  const toggleChannel = (channel: NotificationType) => {
    if (channels.includes(channel)) {
      const next = channels.filter((c) => c !== channel);
      setChannels(next.length === 0 ? [] : next);
    } else {
      setChannels([...channels, channel]);
    }
  };

  const toggleAllChannels = () => {
    setChannels([]);
  };

  return (
    <aside className="w-full space-y-6 lg:w-64">
      <div className="space-y-4 rounded-lg border p-4">
        <div>
          <Label className="mb-2 block">Subscription Types</Label>

          <div className="space-y-2">
            <div className="flex items-center gap-2">
              <Checkbox
                checked={allTypesSelected}
                onCheckedChange={toggleAllTypes}
              />
              <span className="text-sm font-medium">All</span>
            </div>

            {Object.values(SubscriptionType).map((type) => (
              <div
                key={type}
                className="flex items-center gap-2"
              >
                <Checkbox
                  checked={!allTypesSelected && types.includes(type)}
                  onCheckedChange={() => toggleType(type)}
                />
                <span className="text-sm">{SubscriptionTypeLabels[type]}</span>
              </div>
            ))}
          </div>
        </div>

        <div>
          <Label className="mb-2 block">Channels</Label>

          <div className="space-y-2">
            <div className="flex items-center gap-2">
              <Checkbox
                checked={allChannelsSelected}
                onCheckedChange={toggleAllChannels}
              />
              <span className="text-sm font-medium">All</span>
            </div>

            {Object.values(NotificationType).map((channel) => (
              <div
                key={channel}
                className="flex items-center gap-2"
              >
                <Checkbox
                  checked={!allChannelsSelected && channels.includes(channel)}
                  onCheckedChange={() => toggleChannel(channel)}
                />
                <span className="text-sm">{NotificationTypeLabels[channel]}</span>
              </div>
            ))}
          </div>
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
