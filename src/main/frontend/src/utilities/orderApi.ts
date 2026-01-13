import { getErrorMessage } from '@/config/config';
import axios from 'axios';
import type { OrderStatus, TOrderCreateDTO, TOrderDTO } from '../DTO/order.types';
import type { TPageResponseDTO, TPaginationParams } from '../DTO/pagination.types';

const fetchOrders = async (
  params?: TPaginationParams & { status?: OrderStatus }
): Promise<TPageResponseDTO<TOrderDTO>> => {
  try {
    const response = await axios.get<TPageResponseDTO<TOrderDTO>>('/orders', { params });
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error fetching orders: ${getErrorMessage(err)}`);
  }
};

const fetchOrderByNumber = async (num: number): Promise<TOrderDTO> => {
  try {
    const response = await axios.get<TOrderDTO>(`/orders/${num}`);
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error fetching order: ${getErrorMessage(err)}`);
  }
};

const createOrder = async (order: TOrderCreateDTO): Promise<TOrderDTO> => {
  try {
    const response = await axios.post<TOrderDTO>('/orders', order);
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error creating order: ${getErrorMessage(err)}`);
  }
};

export const OrderApi = {
  fetchOrders,
  fetchOrderByNumber,
  createOrder,
};
