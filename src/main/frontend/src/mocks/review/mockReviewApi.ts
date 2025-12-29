import type { TPageResponseDTO, TPaginationParams } from '@/DTO/pagination.types';
import type { TReviewCreateDTO, TReviewDTO } from '@/DTO/review.types';
import { mockReviews } from './mockReviews';

type TFetchReviewParams = TPaginationParams & {
  minRating?: number;
  maxRating?: number;
};

export const MockReviewApi = {
  async getProductReviews(productId: number, params?: TFetchReviewParams): Promise<TPageResponseDTO<TReviewDTO>> {
    console.log('MockReviewApi.getProductReviews', productId, params);

    const { page = 0, limit = 6, sort = 'timestamp,desc', minRating, maxRating } = params ?? {};

    let filtered = mockReviews.filter((r) => r.productId === productId);

    if (minRating !== undefined) {
      filtered = filtered.filter((r) => r.score >= minRating);
    }

    if (maxRating !== undefined) {
      filtered = filtered.filter((r) => r.score <= maxRating);
    }

    const [sortField, sortDirection] = sort.split(',');
    const isAsc = sortDirection === 'asc';

    filtered.sort((a, b) => {
      const aValue = a[sortField as keyof TReviewDTO];
      const bValue = b[sortField as keyof TReviewDTO];

      if (!aValue || !bValue) return 0;

      if (typeof aValue === 'number' && typeof bValue === 'number') {
        return isAsc ? aValue - bValue : bValue - aValue;
      }

      if (typeof aValue === 'string' && typeof bValue === 'string') {
        return isAsc ? aValue.localeCompare(bValue) : bValue.localeCompare(aValue);
      }

      return 0;
    });

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

  async createReview(productId: number, review: TReviewCreateDTO): Promise<TReviewDTO> {
    console.log('MockReviewApi.createReview', productId, review);

    const newReview: TReviewDTO = {
      productId,
      authorName: 'You',
      score: review.score,
      content: review.content,
      timestamp: new Date().toISOString(),
    };

    mockReviews.unshift(newReview);

    await new Promise((res) => setTimeout(res, 300));

    return newReview;
  },
};
