export type TPageResponseDTO<T> = {
  data: T[];
  page: number;
  limit: number;
  totalElements: number;
  totalPages: number;
};

export type TPaginationParams = {
  page?: number;
  limit?: number;
  sort?: string;
  sortDir?: 'ASC' | 'DESC';
};
