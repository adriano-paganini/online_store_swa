'use client';

import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import { DualRangeSlider } from '@/components/ui/dual-range-slider';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

type TProductFiltersProps = {
  priceRange: [number, number | null];
  setPriceRange: (value: [number, number | null]) => void;
  inStockOnly: boolean;
  setInStockOnly: (value: boolean) => void;
  minRating: number;
  setMinRating: (value: number) => void;
  sort: string;
  setSort: (value: string) => void;
  onApplyFilters: () => void;
};

const MAX_PRICE = 1000;

export function ProductFilters({
  priceRange,
  setPriceRange,
  inStockOnly,
  setInStockOnly,
  minRating,
  setMinRating,
  sort,
  setSort,
  onApplyFilters,
}: TProductFiltersProps) {
  return (
    <aside className="w-full space-y-6 lg:w-64">
      <div className="rounded-lg border p-4">
        <div className="space-y-3">
          <div>
            <Label className="mb-3 block">Price Range</Label>

            <DualRangeSlider
              value={[priceRange[0], priceRange[1] ?? MAX_PRICE]}
              onValueChange={(v) => {
                const [min, max] = v as [number, number];

                setPriceRange([min, max >= MAX_PRICE ? null : max]);
              }}
              min={0}
              max={MAX_PRICE}
              step={10}
            />
            <div className="mt-2 flex justify-between text-sm text-muted-foreground">
              <span>${priceRange[0]}</span>
              <span>{priceRange[1] === null ? '$1000+' : `$${priceRange[1]}`}</span>
            </div>
          </div>

          <div>
            <Label
              htmlFor="sort"
              className="mb-2 block"
            >
              Sort By
            </Label>
            <select
              id="sort"
              value={sort}
              onChange={(e) => setSort(e.target.value)}
              className="w-full rounded-md border px-3 py-2 text-sm"
            >
              <option value="id,asc">Default</option>
              <option value="price,asc">Price: Ascending</option>
              <option value="price,desc">Price: Descending</option>
              <option value="avgScore,asc">Rating: Ascending</option>
              <option value="avgScore,desc">Rating: Descending</option>
              <option value="discount,asc">Discount: Ascending</option>
              <option value="discount,desc">Discount: Descending</option>
              <option value="name,asc">Name: A to Z</option>
              <option value="name,desc">Name: Z to A</option>
            </select>
          </div>

          <div className="flex items-center space-x-2">
            <Checkbox
              id="inStock"
              checked={inStockOnly}
              onCheckedChange={(v) => setInStockOnly(Boolean(v))}
            />
            <Label htmlFor="inStock">In Stock Only</Label>
          </div>

          <div>
            <Label
              htmlFor="minRating"
              className="mb-2 block"
            >
              Minimum Rating
            </Label>
            <Input
              id="minRating"
              type="number"
              min="0"
              max="5"
              step="0.5"
              value={minRating}
              onChange={(e) => setMinRating(Number(e.target.value))}
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
