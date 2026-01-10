export enum NotificationType {
  EMAIL = 'EMAIL',
  SMS = 'SMS',
}

export enum NotificationStatus {
  QUEUED = 'QUEUED',
  SENT = 'SENT',
  FAILED = 'FAILED',
}

export type TNotificationResponseDTO = {
  message: string;
  channel: NotificationType;
  status: NotificationStatus;
  timestamp: string;
};
