'use client';

import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';

import { NotificationStatus, NotificationType } from '@/DTO/notification.types';

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
          <div className="space-y-2">
            <div className="flex items-center gap-2">
              <Checkbox
                checked={status === null}
                onCheckedChange={() => setStatus(null)}
              />
              <span className="text-sm font-medium">All</span>
            </div>

            {Object.values(NotificationStatus).map((s) => (
              <div
                key={s}
                className="flex items-center gap-2"
              >
                <Checkbox
                  checked={status === s}
                  onCheckedChange={() => setStatus(s)}
                />
                <span className="text-sm">{s}</span>
              </div>
            ))}
          </div>
        </div>

        <div>
          <Label className="mb-2 block">Channel</Label>
          <div className="space-y-2">
            <div className="flex items-center gap-2">
              <Checkbox
                checked={channel === null}
                onCheckedChange={() => setChannel(null)}
              />
              <span className="text-sm font-medium">All</span>
            </div>

            {Object.values(NotificationType).map((c) => (
              <div
                key={c}
                className="flex items-center gap-2"
              >
                <Checkbox
                  checked={channel === c}
                  onCheckedChange={() => setChannel(c)}
                />
                <span className="text-sm">{c}</span>
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
            <option value="timestamp,desc">Newest first</option>
            <option value="timestamp,asc">Oldest first</option>
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
