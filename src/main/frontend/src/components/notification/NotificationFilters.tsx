'use client';

import { Button } from '@/components/ui/button';
import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';

import { NotificationStatus, NotificationType } from '@/DTO/notification.types';
import { NotificationStatusLabels, NotificationTypeLabels } from '@/utilities/notificationUtils';

type TNotificationFiltersProps = {
  status: NotificationStatus | null;
  setStatus: (v: NotificationStatus | null) => void;
  channel: NotificationType | null;
  setChannel: (v: NotificationType | null) => void;
  sort: string;
  setSort: (v: string) => void;
  onApplyFilters: () => void;
};

export function NotificationFilters({
  status,
  setStatus,
  channel,
  setChannel,
  sort,
  setSort,
  onApplyFilters,
}: TNotificationFiltersProps) {
  return (
    <aside className="w-full space-y-6 lg:w-64">
      <div className="space-y-4 rounded-lg border p-4">
        <div>
          <Label className="mb-2 block">Status</Label>

          <RadioGroup
            value={status ?? 'all'}
            onValueChange={(value) => setStatus(value === 'all' ? null : (value as NotificationStatus))}
          >
            <div className="flex items-center gap-2">
              <RadioGroupItem value="all" />
              <span className="text-sm font-medium">All</span>
            </div>

            {Object.values(NotificationStatus).map((s) => (
              <div
                key={s}
                className="flex items-center gap-2"
              >
                <RadioGroupItem value={s} />
                <span className="text-sm">{NotificationStatusLabels[s]}</span>
              </div>
            ))}
          </RadioGroup>
        </div>

        <div>
          <Label className="mb-2 block">Channel</Label>

          <RadioGroup
            value={channel ?? 'all'}
            onValueChange={(value) => setChannel(value === 'all' ? null : (value as NotificationType))}
          >
            <div className="flex items-center gap-2">
              <RadioGroupItem value="all" />
              <span className="text-sm font-medium">All</span>
            </div>

            {Object.values(NotificationType).map((c) => (
              <div
                key={c}
                className="flex items-center gap-2"
              >
                <RadioGroupItem value={c} />
                <span className="text-sm">{NotificationTypeLabels[c]}</span>
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
              <SelectItem value="timestamp,desc">Newest first</SelectItem>
              <SelectItem value="timestamp,asc">Oldest first</SelectItem>
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
