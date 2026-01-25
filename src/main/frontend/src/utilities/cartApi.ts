import { TProductDTO } from '@/DTO/product.types';
import axios from 'axios';
import type { TCartDTO, TCartItemCreateDTO, TCartItemUpdateDTO, TPopulatedCartDTO } from '../DTO/cart.types';
import { getErrorMessage } from '../config/config';
import { ProductApi } from './productApi';

/**
 * Enriches raw cart items with full product data.
 *
 * 1. Collects all unique product IDs from the cart
 * 2. Fetches the corresponding product details in parallel
 * 3. Maps each cart item to its full product object
 *
 * Results in a "populated" cart structure, UI ready
 *
 * @param cart - raw cart DTO containing items with product IDs
 * @returns populated cart DTO with full product data attached to each item
 *
 * @throws Error if a referenced product cannot be found
 */
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

export const CartApi = {
  getCart,
  addItemToCart,
  updateCartItem,
  removeCartItem,
  clearCart,
};
