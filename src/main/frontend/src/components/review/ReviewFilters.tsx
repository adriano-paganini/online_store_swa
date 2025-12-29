'use client';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

type TReviewFiltersProps = {
  minRating?: number;
  setMinRating: (value?: number) => void;
  maxRating?: number;
  setMaxRating: (value?: number) => void;
  sort: string;
  setSort: (value: string) => void;
  onApplyFilters: () => void;
};

export function ReviewFilters({
  minRating,
  setMinRating,
  maxRating,
  setMaxRating,
  sort,
  setSort,
  onApplyFilters,
}: TReviewFiltersProps) {
  return (
    <aside className="w-full space-y-6 lg:w-64">
      <div className="rounded-lg border p-4">
        <div className="space-y-6">
          <div>
            <Label className="mb-2 block">Sort By</Label>
            <select
              value={sort}
              onChange={(e) => setSort(e.target.value)}
              className="w-full rounded-md border px-3 py-2 text-sm"
            >
              <option value="timestamp,desc">Newest first</option>
              <option value="timestamp,asc">Oldest first</option>
              <option value="score,desc">Highest rating</option>
              <option value="score,asc">Lowest rating</option>
            </select>
          </div>

          <div>
            <Label className="mb-2 block">Minimum Rating</Label>
            <Input
              type="number"
              min={0}
              max={5}
              step={0.5}
              value={minRating ?? ''}
              onChange={(e) => setMinRating(e.target.value ? Number(e.target.value) : undefined)}
            />
          </div>

          <div>
            <Label className="mb-2 block">Maximum Rating</Label>
            <Input
              type="number"
              min={0}
              max={5}
              step={0.5}
              value={maxRating ?? ''}
              onChange={(e) => setMaxRating(e.target.value ? Number(e.target.value) : undefined)}
            />
          </div>

          <Button
            className="w-full"
            onClick={onApplyFilters}
          >
            Apply
          </Button>
        </div>
      </div>
    </aside>
  );
}
