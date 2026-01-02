import { TProductDTO } from '@/DTO/product.types';
import axios from 'axios';
import type { TCartDTO, TCartItemCreateDTO, TCartItemUpdateDTO, TPopulatedCartDTO } from '../DTO/cart.types';
import { getErrorMessage } from '../config/config';
import { ProductApi } from './productApi';

const populateCartItems = async (cart: TCartDTO): Promise<TPopulatedCartDTO> => {
  if (cart.items.length === 0) return { items: [] };

  const uniqueProductIds = [...new Set(cart.items.map((item) => item.productId))];
  const productResponses = await Promise.all(uniqueProductIds.map((id) => ProductApi.fetchProductById(id)));

  const productsMap = productResponses.reduce(
    (map, product) => map.set(product.id, product),
    new Map<number, TProductDTO>()
  );

  const populatedItems = cart.items.map((item) => {
    const product = productsMap.get(item.productId);
    if (!product) {
      throw new Error(`Product ${item.productId} not found`);
    }

    return {
      ...item,
      product,
    };
  });

  return { items: populatedItems };
};

const getCart = async (): Promise<TPopulatedCartDTO> => {
  try {
    const response = await axios.get<TCartDTO>('/cart');
    return populateCartItems(response.data);
  } catch (err: unknown) {
    throw new Error(`Error fetching cart: ${getErrorMessage(err)}`);
  }
};

const addItemToCart = async (item: TCartItemCreateDTO): Promise<TPopulatedCartDTO> => {
  try {
    const response = await axios.post<TCartDTO>('/cart/items', item);
    return populateCartItems(response.data);
  } catch (err: unknown) {
    throw new Error(`Error adding item to cart: ${getErrorMessage(err)}`);
  }
};

const updateCartItem = async (id: number, item: TCartItemUpdateDTO): Promise<TPopulatedCartDTO> => {
  try {
    const response = await axios.patch<TCartDTO>(`/cart/items/${id}`, item);
    return populateCartItems(response.data);
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
