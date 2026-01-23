import axios from 'axios';

import { getErrorMessage } from '@/config/config';
import type { TPageResponseDTO } from '@/DTO/pagination.types';
import type { TProductDTO } from '@/DTO/product.types';
import type {
  TPopulatedSubscriptionDTO,
  TSubscriptionCreateDTO,
  TSubscriptionDTO,
  TSubscriptionQueryParams,
  TSubscriptionUpdateDTO,
} from '@/DTO/subscription.types';

import { ProductApi } from './productApi';

/**
 * Subscription DTOs reference products only by ID. This helper:
 * - Collects all unique product IDs across subscriptions
 * - Fetches the corresponding products in parallel
 * - Replaces `productId` with the full product object
 *
 * @param subscriptions - Raw subscription DTOs returned from the API
 * @returns Subscriptions enriched with full product data
 *
 * @throws Error if a referenced product cannot be found
 */
const populateSubscriptions = async (subscriptions: TSubscriptionDTO[]): Promise<TPopulatedSubscriptionDTO[]> => {
  if (!subscriptions || subscriptions.length === 0) return [];

  const uniqueProductIds = [...new Set(subscriptions.map((s) => s.productId))];

  const products = await Promise.all(uniqueProductIds.map((id) => ProductApi.fetchProductById(id)));

  const productsMap = products.reduce((map, product) => map.set(product.id, product), new Map<number, TProductDTO>());

  return subscriptions.map((sub) => {
    const product = productsMap.get(sub.productId);
    if (!product) {
      throw new Error(`Product ${sub.productId} not found`);
    }

    return {
      id: sub.id,
      userId: sub.userId,
      types: sub.types,
      channels: sub.channels,
      product,
    };
  });
};

const getUserSubscriptionsPage = async (
  params?: TSubscriptionQueryParams
): Promise<TPageResponseDTO<TSubscriptionDTO>> => {
  try {
    const response = await axios.get<TPageResponseDTO<TSubscriptionDTO>>('/subscriptions', { params });
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error fetching subscriptions: ${getErrorMessage(err)}`);
  }
};

const getUserSubscriptionsPagePopulated = async (
  params?: TSubscriptionQueryParams
): Promise<TPageResponseDTO<TPopulatedSubscriptionDTO>> => {
  try {
    const page = await getUserSubscriptionsPage(params);

    const populated = await populateSubscriptions(page.data);

    return {
      ...page,
      data: populated,
    };
  } catch (err: unknown) {
    throw new Error(`Error fetching subscriptions: ${getErrorMessage(err)}`);
  }
};

const getPopulatedSubscriptionByProductId = async (productId: number): Promise<TPopulatedSubscriptionDTO | null> => {
  try {
    const page = await axios.get<TSubscriptionDTO>(`/subscriptions/product/${productId}`);

    const populated = await populateSubscriptions([page.data]);

    return populated[0] || null;
  } catch (err: unknown) {
    if (axios.isAxiosError(err) && err.response?.status === 404) {
      return null;
    }
    throw new Error(`Error fetching subscription: ${getErrorMessage(err)}`);
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
  getUserSubscriptionsPage,
  getUserSubscriptionsPagePopulated,
  getPopulatedSubscriptionByProductId,
  createSubscription,
  updateSubscription,
  deleteSubscription,
};
