import { getErrorMessage } from '@/config/config';
import axios from 'axios';
import type { TSubscriptionCreateDTO, TSubscriptionDTO, TSubscriptionUpdateDTO } from '../DTO/subscription.types';

const getUserSubscriptions = async (): Promise<TSubscriptionDTO[]> => {
  try {
    const response = await axios.get<TSubscriptionDTO[]>('/subscriptions');
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error fetching subscriptions: ${getErrorMessage(err)}`);
  }
};

const createSubscription = async (subscription: TSubscriptionCreateDTO): Promise<TSubscriptionDTO> => {
  try {
    const response = await axios.post<TSubscriptionDTO>('/subscriptions', subscription);
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error creating subscription: ${getErrorMessage(err)}`);
  }
};

const updateSubscription = async (id: number, subscription: TSubscriptionUpdateDTO): Promise<TSubscriptionDTO> => {
  try {
    const response = await axios.patch<TSubscriptionDTO>(`/subscriptions/${id}`, subscription);
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error updating subscription: ${getErrorMessage(err)}`);
  }
};

const deleteSubscription = async (id: number): Promise<void> => {
  try {
    await axios.delete(`/subscriptions/${id}`);
  } catch (err: unknown) {
    throw new Error(`Error deleting subscription: ${getErrorMessage(err)}`);
  }
};

export const SubscriptionApi = {
  getUserSubscriptions,
  createSubscription,
  updateSubscription,
  deleteSubscription,
};
