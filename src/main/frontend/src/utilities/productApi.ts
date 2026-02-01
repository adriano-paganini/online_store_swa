import { getErrorMessage } from '@/config/config';
import axios from 'axios';
import type { TPageResponseDTO, TPaginationParams } from '../DTO/pagination.types';
import type { TProductCreateDTO, TProductDTO, TProductUpdateDTO } from '../DTO/product.types';

const fetchProducts = async (
  params?: TPaginationParams & {
    minPrice?: number;
    maxPrice?: number | null;
    inStock?: boolean;
    minRating?: number;
    search?: string;
  }
): Promise<TPageResponseDTO<TProductDTO>> => {
  try {
    const response = await axios.get<TPageResponseDTO<TProductDTO>>('/products', { params });
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error fetching products: ${getErrorMessage(err)}`);
  }
};

const fetchProductById = async (id: number): Promise<TProductDTO> => {
  try {
    const response = await axios.get<TProductDTO>(`/products/${id}`);
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error fetching product: ${getErrorMessage(err)}`);
  }
};

const createProduct = async (product: TProductCreateDTO): Promise<TProductDTO> => {
  try {
    const response = await axios.post<TProductDTO>('/products', product);
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error creating product: ${getErrorMessage(err)}`);
  }
};

const updateProduct = async (id: number, product: TProductUpdateDTO): Promise<TProductDTO> => {
  try {
    const response = await axios.put<TProductDTO>(`/products/${id}`, product);
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error updating product: ${getErrorMessage(err)}`);
  }
};

const deleteProduct = async (id: number): Promise<void> => {
  try {
    await axios.delete(`/products/${id}`);
  } catch (err: unknown) {
    throw new Error(`Error deleting product: ${getErrorMessage(err)}`);
  }
};

export const ProductApi = {
  fetchProducts,
  fetchProductById,
  createProduct,
  updateProduct,
  deleteProduct,
};
