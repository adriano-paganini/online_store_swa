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

const CartContext = createContext<TCartContextType | null>(null);

export function CartProvider({ children }: { children: React.ReactNode }) {
  const { currentUser } = useUser();
  const isAuthenticated = !!currentUser;

  const [cart, setCart] = useState<TPopulatedCartDTO | null>(null);

  const [loading, setLoading] = useState(false);
  const [itemLoadingIds, setItemLoadingIds] = useState<Set<number>>(new Set());
  const [clearingCart, setClearingCart] = useState(false);

  const markItemLoading = (id: number) => {
    setItemLoadingIds((prev) => new Set(prev).add(id));
  };

  const unmarkItemLoading = (id: number) => {
    setItemLoadingIds((prev) => {
      const next = new Set(prev);
      next.delete(id);
      return next;
    });
  };

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

  useEffect(() => {
    void refreshCart();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isAuthenticated]);

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

export function useCart() {
  const context = useContext(CartContext);
  if (!context) {
    throw new Error('useCart must be used within a CartProvider');
  }
  return context;
}
