'use client';

import { useState } from 'react';

import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';

import type { TNotificationResponseDTO } from '@/DTO/notification.types';
import { NotificationStatus, NotificationType } from '@/DTO/notification.types';

const MESSAGE_PREVIEW_LENGTH = 80;

type TNotificationRowProps = {
  notification: TNotificationResponseDTO;
};

export function NotificationRow({ notification }: TNotificationRowProps) {
  const [expanded, setExpanded] = useState(false);

  const date = new Date(notification.timestamp);
  const isLong = notification.message.length > MESSAGE_PREVIEW_LENGTH;

  const message =
    expanded || !isLong ? notification.message : `${notification.message.slice(0, MESSAGE_PREVIEW_LENGTH)}…`;

  return (
    <>
      <div className="grid grid-cols-5 items-center gap-4 border-b px-4 py-3 text-sm">
        <span className="break-words">{message}</span>

        <Badge variant="secondary">{notification.channel === NotificationType.EMAIL ? 'Email' : 'SMS'}</Badge>

        <Badge
          variant={
            notification.status === NotificationStatus.SENT
              ? 'secondary'
              : notification.status === NotificationStatus.FAILED
                ? 'destructive'
                : 'outline'
          }
        >
          {notification.status}
        </Badge>

        <span>{date.toLocaleString()}</span>

        {isLong && (
          <Button
            size="sm"
            variant="ghost"
            onClick={() => setExpanded((v) => !v)}
          >
            {expanded ? 'Collapse' : 'Expand'}
          </Button>
        )}
      </div>
    </>
  );
}
