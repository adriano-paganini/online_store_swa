'use client';

import { Skeleton } from '@/components/ui/skeleton';
import type { TNotificationResponseDTO } from '@/DTO/notification.types';
import { NotificationRow } from './NotificationRow';

type TNotificationTableProps = {
  notifications: TNotificationResponseDTO[];
  loading: boolean;
};

export function NotificationTable({ notifications, loading }: TNotificationTableProps) {
  if (loading) {
    return (
      <div className="space-y-2">
        {Array.from({ length: 8 }).map((_, i) => (
          <Skeleton
            key={i}
            className="h-12 w-full rounded-md"
          />
        ))}
      </div>
    );
  }

  if (notifications.length === 0) {
    return <p className="text-muted-foreground">No notifications found.</p>;
  }

  return (
    <div className="rounded-lg border">
      <div className="grid grid-cols-5 gap-4 border-b px-4 py-2 text-sm font-medium">
        <span>Message</span>
        <span>Channel</span>
        <span>Status</span>
        <span>Date</span>
        <span />
      </div>

      {notifications.map((notification, index) => (
        <NotificationRow
          key={`${notification.timestamp}-${index}`}
          notification={notification}
        />
      ))}
    </div>
  );
}
