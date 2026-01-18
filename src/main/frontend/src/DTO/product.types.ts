export type TProductDTO = {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  discount: number;
  avgScore: number;
  images: string[];
  deleted: boolean;
  createdByName?: string;
  createdAt?: string;
  updatedByName?: string;
  updatedAt?: string;
};

export type TProductCreateDTO = {
  name: string;
  images: string[];
  description: string;
  price: number;
  stock: number;
  discount: number;
};

export type TProductUpdateDTO = {
  name?: string;
  images?: string[];
  description?: string;
  price?: number;
  stock?: number;
  discount?: number;
};
