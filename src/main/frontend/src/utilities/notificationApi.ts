import { getErrorMessage } from '@/config/config';
import { NotificationStatus, NotificationType, TNotificationResponseDTO } from '@/DTO/notification.types';
import { TPageResponseDTO, TPaginationParams } from '@/DTO/pagination.types';
import axios from 'axios';

const getUserNotifications = async (
  params: TPaginationParams & { status?: NotificationStatus; channel?: NotificationType }
): Promise<TPageResponseDTO<TNotificationResponseDTO>> => {
  try {
    const response = await axios.get<TPageResponseDTO<TNotificationResponseDTO>>('/notifications', {
      params: {
        page: params.page,
        limit: params.limit,
        status: params.status,
        channel: params.channel,
        sort: params.sort,
      },
    });
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error fetching notifications: ${getErrorMessage(err)}`);
  }
};

export const NotificationApi = {
  getUserNotifications,
};
