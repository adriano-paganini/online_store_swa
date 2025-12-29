import type { TPageResponseDTO, TPaginationParams } from '@/DTO/pagination.types';
import type { TProductCreateDTO, TProductDTO, TProductUpdateDTO } from '@/DTO/product.types';
import { mockProducts } from './mockProducts';

type TFetchParams = TPaginationParams & {
  minPrice?: number;
  maxPrice?: number;
  inStock?: boolean;
  minRating?: number;
};

const products: TProductDTO[] = [...mockProducts];

const delay = (ms = 400) => new Promise((res) => setTimeout(res, ms));

export const MockProductApi = {
  async fetchProducts(params?: TFetchParams): Promise<TPageResponseDTO<TProductDTO>> {
    const { page = 0, limit = 12, sort = 'id,asc', minPrice, maxPrice, inStock, minRating } = params ?? {};

    let filtered = products.filter((p) => !p.deleted);

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

    const [sortField, sortDirection] = sort.split(',');
    const isAsc = sortDirection === 'asc';

    filtered.sort((a, b) => {
      const aVal = a[sortField as keyof TProductDTO];
      const bVal = b[sortField as keyof TProductDTO];

      if (typeof aVal === 'number' && typeof bVal === 'number') {
        return isAsc ? aVal - bVal : bVal - aVal;
      }

      if (typeof aVal === 'string' && typeof bVal === 'string') {
        return isAsc ? aVal.localeCompare(bVal) : bVal.localeCompare(aVal);
      }

      return 0;
    });

    const totalElements = filtered.length;
    const totalPages = Math.ceil(totalElements / limit);
    const start = page * limit;

    await delay();

    return {
      data: filtered.slice(start, start + limit),
      page,
      limit,
      totalElements,
      totalPages,
    };
  },

  async fetchProductById(id: number): Promise<TProductDTO> {
    await delay();

    const product = products.find((p) => p.id === id && !p.deleted);

    if (!product) {
      throw new Error('Product not found');
    }

    return product;
  },

  async createProduct(dto: TProductCreateDTO): Promise<TProductDTO> {
    await delay();

    const newProduct: TProductDTO = {
      id: Math.max(...products.map((p) => p.id)) + 1,
      avgScore: 0,
      images: [],
      deleted: false,
      ...dto,
    };

    products.push(newProduct);
    return newProduct;
  },

  async updateProduct(id: number, dto: TProductUpdateDTO): Promise<TProductDTO> {
    await delay();

    const index = products.findIndex((p) => p.id === id && !p.deleted);
    if (index === -1) {
      throw new Error('Product not found');
    }

    products[index] = {
      ...products[index],
      ...dto,
    };

    return products[index];
  },

  async deleteProduct(id: number): Promise<void> {
    await delay();

    const product = products.find((p) => p.id === id);
    if (!product) {
      throw new Error('Product not found');
    }

    // soft delete (matches your DTO)
    product.deleted = true;
  },
};
