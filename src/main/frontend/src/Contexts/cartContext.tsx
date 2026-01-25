/* eslint-disable react-refresh/only-export-components */
'use client';

import type React from 'react';
import { createContext, useContext, useEffect, useMemo, useState } from 'react';

import type { TCartItemCreateDTO, TPopulatedCartDTO } from '../DTO/cart.types';

import { toastApiError } from '@/lib/utils';
import { toast } from 'sonner';
import { CartApi } from '../utilities/cartApi';
import { useUser } from './authenticatedUserContext';

type TCartContextType = {
  cart: TPopulatedCartDTO | null;
  loading: boolean;
  itemLoadingIds: Set<number>;
  clearingCart: boolean;

  cartItemCount: number;

  refreshCart: () => Promise<void>;
  addItem: (item: TCartItemCreateDTO) => Promise<void>;
  incrementItem: (id: number) => Promise<void>;
  decrementItem: (id: number) => Promise<void>;
  removeItem: (id: number) => Promise<void>;
  clearCart: () => Promise<void>;
};

/**
 * Cart context providing cart state and mutation helpers.
 *
 * Handles:
 * - Fetching and refreshing the cart
 * - Item-level loading states
 * - Syncing cart data with authenticated user state
 */
const CartContext = createContext<TCartContextType | null>(null);

export function CartProvider({ children }: { children: React.ReactNode }) {
  const { currentUser } = useUser();
  const isAuthenticated = !!currentUser;

  const [cart, setCart] = useState<TPopulatedCartDTO | null>(null);

  const [loading, setLoading] = useState(false);

  // traks which cart item IDs are currently being mutated
  const [itemLoadingIds, setItemLoadingIds] = useState<Set<number>>(new Set());
  const [clearingCart, setClearingCart] = useState(false);

  // Mark an individual cart item as loading (optimistic UI support)
  const markItemLoading = (id: number) => {
    setItemLoadingIds((prev) => new Set(prev).add(id));
  };

  // Remove loading state from a specific cart item
  const unmarkItemLoading = (id: number) => {
    setItemLoadingIds((prev) => {
      const next = new Set(prev);
      next.delete(id);
      return next;
    });
  };

  /**
   * Fetches the latest cart from the API.
   * Automatically resets cart state when the user logs out.
   */
  const refreshCart = async () => {
    if (!isAuthenticated) {
      setCart(null);
      return;
    }

    try {
      setLoading(true);
      const cartData = await CartApi.getCart();
      setCart(cartData);
    } catch (err) {
      toastApiError(err);
    } finally {
      setLoading(false);
    }
  };

  // Adds a new item to the cart
  const addItem = async (item: TCartItemCreateDTO) => {
    if (!isAuthenticated) {
      toast.error('You must be logged in to add items to cart');
      return;
    }

    try {
      const updatedCart = await CartApi.addItemToCart(item);
      setCart(updatedCart);
    } catch (err) {
      toastApiError(err);
    }
  };

  // Increases the quantity of a cart item
  const incrementItem = async (id: number) => {
    if (!isAuthenticated || !cart) return;

    const item = cart.items.find((i) => i.id === id);
    if (!item) return;

    try {
      markItemLoading(id);
      const updatedCart = await CartApi.updateCartItem(id, {
        quantity: item.quantity + 1,
      });
      setCart(updatedCart);
    } catch (err) {
      toastApiError(err);
    } finally {
      unmarkItemLoading(id);
    }
  };

  // Decreases item quantity or removes the item if quantity reaches 0
  const decrementItem = async (id: number) => {
    if (!isAuthenticated || !cart) return;

    const item = cart.items.find((i) => i.id === id);
    if (!item) return;

    try {
      markItemLoading(id);

      if (item.quantity === 1) {
        await CartApi.removeCartItem(id);
        setCart((prev) => (prev ? { ...prev, items: prev.items.filter((i) => i.id !== id) } : prev));
      } else {
        const updatedCart = await CartApi.updateCartItem(id, {
          quantity: item.quantity - 1,
        });
        setCart(updatedCart);
      }
    } catch (err) {
      toastApiError(err);
    } finally {
      unmarkItemLoading(id);
    }
  };

  const removeItem = async (id: number) => {
    if (!isAuthenticated) return;

    try {
      markItemLoading(id);
      await CartApi.removeCartItem(id);
      setCart((prev) => (prev ? { ...prev, items: prev.items.filter((i) => i.id !== id) } : prev));
    } catch (err) {
      toastApiError(err);
    } finally {
      unmarkItemLoading(id);
    }
  };

  const clearCart = async () => {
    if (!isAuthenticated) return;

    try {
      setClearingCart(true);
      await CartApi.clearCart();
      setCart({ items: [] });
      toast.success('Cart cleared');
    } catch (err) {
      toastApiError(err);
    } finally {
      setClearingCart(false);
    }
  };

  // Refresh cart whenever authentication state changes
  useEffect(() => {
    void refreshCart();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated]);

  // Total number of items in the cart (sum of quantities)
  const cartItemCount = useMemo(() => cart?.items.reduce((sum, item) => sum + item.quantity, 0) ?? 0, [cart]);

  const value: TCartContextType = {
    cart,
    loading,
    itemLoadingIds,
    clearingCart,
    cartItemCount,
    refreshCart,
    addItem,
    incrementItem,
    decrementItem,
    removeItem,
    clearCart,
  };

  return <CartContext.Provider value={value}>{children}</CartContext.Provider>;
}

/**
 * Hook for accessing cart state and actions.
 * Must be used within a CartProvider.
 */
export function useCart() {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
}
