export type TReviewDTO = {
  productId: number;
  authorName: string;
  score: number;
  content: string;
  timestamp: string;
};

export type TReviewCreateDTO = {
  score: number;
  content: string;
};
