'use client';

import { cn } from '@/lib/utils';
import { Star } from 'lucide-react';
import { useState } from 'react';

type TStarRatingProps = {
  value: number;
  onChange: (value: number) => void;
  size?: number;
};

export function StarRating({ value, onChange, size = 20 }: TStarRatingProps) {
  const [hoverValue, setHoverValue] = useState<number | null>(null);

  const displayValue = hoverValue ?? value;

  return (
    <div
      className="flex items-center gap-1"
      onMouseLeave={() => setHoverValue(null)}
    >
      {Array.from({ length: 5 }).map((_, i) => {
        const full = i + 1;
        const half = i + 0.5;

        const isFull = displayValue >= full;
        const isHalf = displayValue >= half && displayValue < full;

        return (
          <div
            key={i}
            className="relative h-5 w-5 cursor-pointer"
          >
            <Star
              size={size}
              className={cn(
                'absolute left-0 top-0 text-yellow-400 transition-colors',
                isHalf ? 'fill-yellow-400' : 'fill-transparent'
              )}
              style={{ clipPath: 'inset(0 50% 0 0)' }}
              onMouseEnter={() => setHoverValue(half)}
              onClick={() => onChange(half)}
            />

            <Star
              size={size}
              className={cn('text-yellow-400 transition-colors', isFull ? 'fill-yellow-400' : 'fill-transparent')}
              onMouseEnter={() => setHoverValue(full)}
              onClick={() => onChange(full)}
            />
          </div>
        );
      })}
    </div>
  );
}
