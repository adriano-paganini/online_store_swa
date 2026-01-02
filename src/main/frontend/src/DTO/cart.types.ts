import { TProductDTO } from './product.types';

export type TCartItemDTO = {
  id: number;
  productId: number;
  quantity: number;
  appliedDiscount: number | null;
  currentPrice: number;
};

export type TCartDTO = {
  items: TCartItemDTO[];
};

export type TPopulatedCartItemDTO = {
  id: number;
  quantity: number;
  appliedDiscount: number | null;
  currentPrice: number;
  product: TProductDTO;
};

export type TPopulatedCartDTO = {
  items: TPopulatedCartItemDTO[];
};

export type TCartItemCreateDTO = {
  productId: number;
  quantity: number;
};

export type TCartItemUpdateDTO = {
  quantity?: number;
  appliedDiscount?: number;
};
