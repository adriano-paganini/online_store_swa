import type { TReviewDTO } from '@/DTO/review.types';

export const mockReviews: TReviewDTO[] = [
  {
    productId: 1,
    authorName: 'Alice',
    score: 5,
    content: 'Amazing product! Highly recommend.',
    timestamp: '2024-01-05T10:12:00',
  },
  {
    productId: 1,
    authorName: 'Bob',
    score: 4,
    content: 'Very good quality, but shipping was slow.',
    timestamp: '2024-01-10T14:45:00',
  },
  {
    productId: 1,
    authorName: 'Charlie',
    score: 3,
    content: 'It’s okay. Does the job.',
    timestamp: '2024-02-02T09:30:00',
  },
  {
    productId: 2,
    authorName: 'Diana',
    score: 5,
    content: 'Best stuff I’ve ever used.',
    timestamp: '2024-02-15T18:20:00',
  },
  {
    productId: 2,
    authorName: 'Eric',
    score: 2,
    content: 'Too loud for my taste.',
    timestamp: '2024-03-01T08:10:00',
  },
];
