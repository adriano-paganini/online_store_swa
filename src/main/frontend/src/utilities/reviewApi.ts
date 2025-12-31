import { getErrorMessage } from '@/config/config';
import axios from 'axios';
import type { TPageResponseDTO, TPaginationParams } from '../DTO/pagination.types';
import type { TReviewCreateDTO, TReviewDTO } from '../DTO/review.types';

const getProductReviews = async (
  productId: number,
  params: TPaginationParams & { minRating?: number; maxRating?: number }
): Promise<TPageResponseDTO<TReviewDTO>> => {
  try {
    const response = await axios.get<TPageResponseDTO<TReviewDTO>>(`/products/${productId}/reviews`, {
      params: {
        page: params.page,
        limit: params.limit,
        minRating: params.minRating,
        maxRating: params.maxRating,
        sort: params.sort,
      },
    });
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error fetching reviews: ${getErrorMessage(err)}`);
  }
};

const createReview = async (productId: number, review: TReviewCreateDTO): Promise<TReviewDTO> => {
  try {
    const response = await axios.post<TReviewDTO>(`/products/${productId}/reviews`, review);
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error creating review: ${getErrorMessage(err)}`);
  }
};

export const ReviewApi = {
  getProductReviews,
  createReview,
};
