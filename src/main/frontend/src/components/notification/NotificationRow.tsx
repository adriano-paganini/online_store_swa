'use client';

import { NotificationStatus, type TNotificationResponseDTO } from '@/DTO/notification.types';
import { NotificationStatusLabels, NotificationTypeLabels } from '@/utilities/notificationUtils';
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '../ui/tooltip';

type TNotificationRowProps = {
  notification: TNotificationResponseDTO;
};

export function NotificationRow({ notification }: TNotificationRowProps) {
  const statusColor =
    notification.status === NotificationStatus.FAILED
      ? 'bg-red-500'
      : notification.status === NotificationStatus.SENT
        ? 'bg-green-500'
        : 'bg-yellow-400';

  return (
    <div className="flex items-center gap-4 border-b px-4 py-3 text-sm last:border-0">
      <div className="flex items-center gap-2">
        <TooltipProvider delayDuration={200}>
          <Tooltip>
            <TooltipTrigger asChild>
              <div
                className={`h-3 w-3 rounded-full ${statusColor}`}
                aria-label={`Status: ${notification.status}`}
              />
            </TooltipTrigger>

            <TooltipContent side="top">
              <span className="text-sm">{NotificationStatusLabels[notification.status]}</span>
            </TooltipContent>
          </Tooltip>
        </TooltipProvider>

        <div>{NotificationTypeLabels[notification.channel]}</div>
      </div>

      <span className="break-words">{notification.message}</span>
    </div>
  );
}
