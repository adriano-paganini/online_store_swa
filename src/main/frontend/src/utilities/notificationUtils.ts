import { NotificationStatus, NotificationType } from '@/DTO/notification.types';

export const NotificationTypeLabels: Record<NotificationType, string> = {
  EMAIL: 'Email',
  SMS: 'SMS',
};

export const NotificationStatusLabels: Record<NotificationStatus, string> = {
  QUEUED: 'Queued',
  SENT: 'Sent',
  FAILED: 'Failed',
};
