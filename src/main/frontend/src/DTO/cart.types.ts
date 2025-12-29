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

export type TCartItemCreateDTO = {
  productId: number;
  quantity: number;
};

export type TCartItemUpdateDTO = {
  quantity?: number;
  appliedDiscount?: number;
};
