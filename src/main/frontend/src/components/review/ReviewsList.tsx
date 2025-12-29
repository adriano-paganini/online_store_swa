'use client';

import { Pagination } from '@/components/general/Pagination';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import type { TReviewCreateDTO, TReviewDTO } from '@/DTO/review.types';
import { ReviewApi } from '@/utilities/reviewApi';
import { useEffect, useState } from 'react';
import { toast } from 'sonner';
import { ReviewFilters } from './ReviewFilters';

type TProductReviewsProps = {
  productId: number;
};

export function ReviewsList({ productId }: TProductReviewsProps) {
  const [page, setPage] = useState(0);
  const [limit, setLimit] = useState(6);
  const [totalPages, setTotalPages] = useState(0);

  const [minRating, setMinRating] = useState<number | undefined>(0);
  const [maxRating, setMaxRating] = useState<number | undefined>(5);
  const [sort, setSort] = useState<string>('timestamp,desc');

  const [appliedFilters, setAppliedFilters] = useState<{
    minRating?: number;
    maxRating?: number;
    sort: string;
  }>({
    minRating: undefined,
    maxRating: undefined,
    sort: 'timestamp,desc',
  });

  const [reviews, setReviews] = useState<TReviewDTO[]>([]);
  const [loading, setLoading] = useState(false);

  const [newReview, setNewReview] = useState<TReviewCreateDTO>({
    score: 5,
    content: '',
  });
  const [creating, setCreating] = useState(false);

  useEffect(() => {
    const fetchReviews = async () => {
      setLoading(true);

      try {
        const response = await ReviewApi.getProductReviews(productId, {
          page,
          limit,
          minRating: appliedFilters.minRating,
          maxRating: appliedFilters.maxRating,
          sort: appliedFilters.sort,
        });

        setReviews(response.data);
        setTotalPages(response.totalPages);
      } catch (err) {
        console.error(err instanceof Error ? err.message : 'Failed to load reviews');
        toast.error('Error loading reviews. Please try again later.');
      } finally {
        setLoading(false);
      }
    };

    void fetchReviews();
  }, [productId, page, limit, appliedFilters]);

  const handleApplyFilters = () => {
    setPage(0);
    setAppliedFilters({
      minRating,
      maxRating,
      sort,
    });
  };

  const handleCreateReview = async () => {
    if (!newReview.content.trim()) return;

    setCreating(true);

    try {
      const created = await ReviewApi.createReview(productId, newReview);

      setReviews((prev) => [created, ...prev]);
      setNewReview({ score: 5, content: '' });
    } catch (err) {
      console.error(err instanceof Error ? err.message : 'Failed to create review');
      toast.error('Error submitting review. Please try again later.');
    } finally {
      setCreating(false);
    }
  };

  return (
    <section className="flex flex-col gap-8 lg:flex-row">
      <ReviewFilters
        minRating={minRating}
        setMinRating={setMinRating}
        maxRating={maxRating}
        setMaxRating={setMaxRating}
        sort={sort}
        setSort={setSort}
        onApplyFilters={handleApplyFilters}
      />

      <div className="flex-1 space-y-8">
        <div className="space-y-4 rounded-lg border p-4">
          <h3 className="font-medium">Write a review</h3>

          <Input
            type="number"
            min={1}
            max={5}
            value={newReview.score}
            onChange={(e) => setNewReview({ ...newReview, score: Number(e.target.value) })}
          />

          <Textarea
            placeholder="Share your experience…"
            value={newReview.content}
            onChange={(e) => setNewReview({ ...newReview, content: e.target.value })}
          />

          <Button
            onClick={() => void handleCreateReview()}
            disabled={creating}
          >
            {creating ? 'Submitting…' : 'Submit review'}
          </Button>
        </div>

        {loading && <p className="text-sm text-muted-foreground">Loading reviews…</p>}

        {!loading && reviews.length === 0 && (
          <p className="text-sm text-muted-foreground">No reviews yet. You can be the first!</p>
        )}

        <div className="space-y-4">
          {reviews.map((review) => (
            <div
              key={`${review.authorName}-${review.timestamp}`}
              className="rounded-lg border p-4"
            >
              <div className="flex items-center justify-between">
                <span className="font-medium">{review.authorName}</span>
                <span className="text-sm text-muted-foreground">{new Date(review.timestamp).toLocaleDateString()}</span>
              </div>

              <div className="mt-1 text-sm">Rating: {review.score} / 5</div>

              <p className="mt-2 text-sm">{review.content}</p>
            </div>
          ))}
        </div>

        <Pagination
          page={page}
          totalPages={totalPages}
          limit={limit}
          onLimitChange={(newLimit) => {
            setLimit(newLimit);
            setPage(0);
          }}
          onPageChange={setPage}
        />
      </div>
    </section>
  );
}
