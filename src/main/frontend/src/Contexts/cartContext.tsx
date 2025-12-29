/* eslint-disable react-refresh/only-export-components */
'use client';

import type React from 'react';
import { createContext, useContext, useEffect, useMemo, useState } from 'react';

import type { TCartDTO, TCartItemCreateDTO, TCartItemUpdateDTO } from '../DTO/cart.types';

import { toast } from 'sonner';
import { CartApi } from '../utilities/cartApi';
import { useUser } from './authenticatedUserContext';

type TCartContextType = {
  cart: TCartDTO | null;
  loading: boolean;

  cartItemCount: number;

  refreshCart: () => Promise<void>;
  addItem: (item: TCartItemCreateDTO) => Promise<void>;
  updateItem: (id: number, item: TCartItemUpdateDTO) => Promise<void>;
  removeItem: (id: number) => Promise<void>;
  clearCart: () => Promise<void>;
};

const CartContext = createContext<TCartContextType | null>(null);

export function CartProvider({ children }: { children: React.ReactNode }) {
  const { currentUser } = useUser();
  const isAuthenticated = !!currentUser;

  const [cart, setCart] = useState<TCartDTO | null>(null);
  const [loading, setLoading] = useState(false);

  const refreshCart = async () => {
    if (!isAuthenticated) {
      setCart(null);
      return;
    }

    try {
      setLoading(true);
      const cartData = await CartApi.getCart();
      setCart(cartData);
    } catch (error) {
      console.error('Failed to fetch cart:', error);
      toast.error('Failed to load cart');
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
      setLoading(true);
      const updatedCart = await CartApi.addItemToCart(item);
      setCart(updatedCart);
      toast.success('Item added to cart');
    } catch (error) {
      console.error('Failed to add item to cart:', error);
      toast.error('Failed to add item to cart');
    } finally {
      setLoading(false);
    }
  };

  const updateItem = async (id: number, item: TCartItemUpdateDTO) => {
    if (!isAuthenticated) return;

    try {
      setLoading(true);
      const updatedCart = await CartApi.updateCartItem(id, item);
      setCart(updatedCart);
      toast.success('Cart updated');
    } catch (error) {
      console.error('Failed to update cart item:', error);
      toast.error('Failed to update cart item');
    } finally {
      setLoading(false);
    }
  };

  const removeItem = async (id: number) => {
    if (!isAuthenticated) return;

    try {
      setLoading(true);
      await CartApi.removeCartItem(id);

      setCart((prev) => (prev ? { ...prev, items: prev.items.filter((item) => item.id !== id) } : prev));

      toast.success('Item removed from cart');
    } catch (error) {
      console.error('Failed to remove cart item:', error);
      toast.error('Failed to remove cart item');
    } finally {
      setLoading(false);
    }
  };

  const clearCart = async () => {
    if (!isAuthenticated) return;

    try {
      setLoading(true);
      await CartApi.clearCart();
      setCart({ items: [] });
      toast.success('Cart cleared');
    } catch (error) {
      console.error('Failed to clear cart:', error);
      toast.error('Failed to clear cart');
    } finally {
      setLoading(false);
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
    cartItemCount,
    refreshCart,
    addItem,
    updateItem,
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
