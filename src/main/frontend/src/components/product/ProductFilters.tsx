'use client';

import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import { DualRangeSlider } from '@/components/ui/dual-range-slider';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

type TProductFiltersProps = {
  priceRange: [number, number];
  setPriceRange: (value: [number, number]) => void;
  inStockOnly: boolean;
  setInStockOnly: (value: boolean) => void;
  minRating: number;
  setMinRating: (value: number) => void;
  onApplyFilters: () => void;
};

export function ProductFilters({
  priceRange,
  setPriceRange,
  inStockOnly,
  setInStockOnly,
  minRating,
  setMinRating,
  onApplyFilters,
}: TProductFiltersProps) {
  return (
    <aside className="w-full space-y-6 lg:w-64">
      <div className="rounded-lg border p-4">
        <h3 className="mb-4 font-semibold">Filters</h3>

        <div className="space-y-6">
          <div>
            <Label className="mb-3 block">Price Range</Label>

            <DualRangeSlider
              value={priceRange}
              onValueChange={(v) => setPriceRange(v as [number, number])}
              min={0}
              max={1000}
              step={10}
            />

            <div className="mt-2 flex justify-between text-sm text-muted-foreground">
              <span>${priceRange[0]}</span>
              <span>${priceRange[1]}</span>
            </div>
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
            Apply Filters
          </Button>
        </div>
      </div>
    </aside>
  );
}
