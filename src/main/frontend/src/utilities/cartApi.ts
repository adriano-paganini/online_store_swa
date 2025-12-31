import axios from 'axios';
import type { TCartDTO, TCartItemCreateDTO, TCartItemUpdateDTO } from '../DTO/cart.types';
import { getErrorMessage } from '../config/config';

const getCart = async (): Promise<TCartDTO> => {
  try {
    const response = await axios.get<TCartDTO>('/cart');
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error fetching cart: ${getErrorMessage(err)}`);
  }
};

const addItemToCart = async (item: TCartItemCreateDTO): Promise<TCartDTO> => {
  try {
    const response = await axios.post<TCartDTO>('/cart/items', item);
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error adding item to cart: ${getErrorMessage(err)}`);
  }
};

const updateCartItem = async (id: number, item: TCartItemUpdateDTO): Promise<TCartDTO> => {
  try {
    const response = await axios.patch<TCartDTO>(`/cart/items/${id}`, item);
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error updating cart item: ${getErrorMessage(err)}`);
  }
};

const removeCartItem = async (id: number): Promise<void> => {
  try {
    await axios.delete(`/cart/items/${id}`);
  } catch (err: unknown) {
    throw new Error(`Error removing cart item: ${getErrorMessage(err)}`);
  }
};

const clearCart = async (): Promise<void> => {
  try {
    await axios.delete('/cart/items');
  } catch (err: unknown) {
    throw new Error(`Error clearing cart: ${getErrorMessage(err)}`);
  }
};

// Temporarily using real API for testing backend endpoints
// Change back to: import.meta.env.DEV ? MockCartApi : {...}
export const CartApi = {
  getCart,
  addItemToCart,
  updateCartItem,
  removeCartItem,
  clearCart,
};
