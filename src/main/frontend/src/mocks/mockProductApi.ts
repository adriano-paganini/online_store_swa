import type { TPageResponseDTO, TPaginationParams } from '@/DTO/pagination.types';
import type { TProductDTO } from '@/DTO/product.types';
import { mockProducts } from './mockProducts';

type TFetchParams = TPaginationParams & {
  minPrice?: number;
  maxPrice?: number;
  inStock?: boolean;
  minRating?: number;
};

export const MockProductApi = {
  async fetchProducts(params?: TFetchParams): Promise<TPageResponseDTO<TProductDTO>> {
    const { page = 0, limit = 12, minPrice, maxPrice, inStock, minRating } = params ?? {};

    let filtered = [...mockProducts];

    if (minPrice !== undefined) {
      filtered = filtered.filter((p) => p.price >= minPrice);
    }

    if (maxPrice !== undefined) {
      filtered = filtered.filter((p) => p.price <= maxPrice);
    }

    if (inStock) {
      filtered = filtered.filter((p) => p.stock > 0);
    }

    if (minRating !== undefined) {
      filtered = filtered.filter((p) => p.avgScore >= minRating);
    }

    const totalElements = filtered.length;
    const totalPages = Math.ceil(totalElements / limit);
    const start = page * limit;
    const data = filtered.slice(start, start + limit);

    await new Promise((res) => setTimeout(res, 400));

    return {
      data,
      page,
      limit,
      totalElements,
      totalPages,
    };
  },
};
