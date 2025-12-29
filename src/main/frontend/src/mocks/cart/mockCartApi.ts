import type { TCartDTO, TCartItemCreateDTO, TCartItemUpdateDTO } from '@/DTO/cart.types';
import { mockProducts } from '../product/mockProducts';
import { createCartItem, mockCartStore } from './mockCartStore';

const delay = (ms = 300) => new Promise((res) => setTimeout(res, ms));

export const MockCartApi = {
  async getCart(): Promise<TCartDTO> {
    await delay();
    return structuredClone(mockCartStore);
  },

  async addItemToCart(item: TCartItemCreateDTO): Promise<TCartDTO> {
    await delay();

    const product = mockProducts.find((p) => p.id === item.productId);
    if (!product) {
      throw new Error('Product not found');
    }

    const existing = mockCartStore.items.find((i) => i.productId === item.productId);

    if (existing) {
      existing.quantity += item.quantity;
    } else {
      mockCartStore.items.push(
        createCartItem(
          product.id,
          item.quantity,
          product.price,
          product.discount ? product.price * product.discount : null
        )
      );
    }

    return structuredClone(mockCartStore);
  },

  async updateCartItem(id: number, item: TCartItemUpdateDTO): Promise<TCartDTO> {
    await delay();

    const cartItem = mockCartStore.items.find((i) => i.id === id);
    if (!cartItem) {
      throw new Error('Cart item not found');
    }

    if (item.quantity !== undefined) {
      cartItem.quantity = Math.max(1, item.quantity);
    }

    if (item.appliedDiscount !== undefined) {
      cartItem.appliedDiscount = item.appliedDiscount;
    }

    return structuredClone(mockCartStore);
  },

  async removeCartItem(id: number): Promise<void> {
    await delay();
    mockCartStore.items = mockCartStore.items.filter((i) => i.id !== id);
  },

  async clearCart(): Promise<void> {
    await delay();
    mockCartStore.items = [];
  },
};
