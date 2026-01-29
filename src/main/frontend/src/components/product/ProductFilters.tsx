'use client';

import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import { DualRangeSlider } from '@/components/ui/dual-range-slider';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';

type TProductFiltersProps = {
  priceRange: [number, number | null];
  setPriceRange: (value: [number, number | null]) => void;
  inStockOnly: boolean;
  setInStockOnly: (value: boolean) => void;
  minRating: number;
  setMinRating: (value: number) => void;
  sort: string;
  setSort: (value: string) => void;
  search: string;
  setSearch: (value: string) => void;
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
  search,
  setSearch,
  onApplyFilters,
}: TProductFiltersProps) {
  return (
    <aside className="w-full space-y-6 lg:w-64">
      <div className="rounded-lg border p-4">
        <div className="space-y-3">
          <div>
            <Label className="mb-2 block">Search</Label>
            <Input
              placeholder="Search products…"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>

          <div>
            <Label className="mb-3 block">Price Range</Label>

            <DualRangeSlider
              value={[priceRange[0], priceRange[1] ?? MAX_PRICE]}
              onValueChange={(v) => {
                const [min, max] = v as [number, number];

                // If the selected max value equals the slider's max, treat it as "no limit" (null)
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
            <Label className="mb-2 block">Sort By</Label>

            <Select
              value={sort}
              onValueChange={setSort}
            >
              <SelectTrigger className="w-full">
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="id,asc">Default</SelectItem>
                <SelectItem value="price,asc">Price Ascending</SelectItem>
                <SelectItem value="price,desc">Price Descending</SelectItem>
                <SelectItem value="avgScore,asc">Rating Ascending</SelectItem>
                <SelectItem value="avgScore,desc">Rating Descending</SelectItem>
                <SelectItem value="discount,asc">Discount Ascending</SelectItem>
                <SelectItem value="discount,desc">Discount Descending</SelectItem>
                <SelectItem value="name,asc">Name A-Z</SelectItem>
                <SelectItem value="name,desc">Name Z-A</SelectItem>
              </SelectContent>
            </Select>
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
            <Label className="mb-2 block">Minimum Rating</Label>
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
