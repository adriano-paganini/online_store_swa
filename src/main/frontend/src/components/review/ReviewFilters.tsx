'use client';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';

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
        <div className="space-y-3">
          <div>
            <Label className="mb-2 block">Sort By</Label>

            <Select
              value={sort}
              onValueChange={setSort}
            >
              <SelectTrigger className="w-full">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="timestamp,desc">Newest first</SelectItem>
                <SelectItem value="timestamp,asc">Oldest first</SelectItem>
                <SelectItem value="score,desc">Highest rating</SelectItem>
                <SelectItem value="score,asc">Lowest rating</SelectItem>
              </SelectContent>
            </Select>
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
