import type { TCartDTO, TCartItemDTO } from '@/DTO/cart.types';

let nextCartItemId = 1;

export const mockCartStore: TCartDTO = {
  items: [],
};

export const createCartItem = (
  productId: number,
  quantity: number,
  price: number,
  discount: number | null
): TCartItemDTO => ({
  id: nextCartItemId++,
  productId,
  quantity,
  currentPrice: price,
  appliedDiscount: discount,
});
