'use client';

import { Button } from '@/components/ui/button';

type TPaginationProps = {
  page: number;
  totalPages: number;
  limit: number;
  onLimitChange: (limit: number) => void;
  onPageChange: (page: number) => void;
};

export function Pagination({ page, totalPages, limit, onLimitChange, onPageChange }: TPaginationProps) {
  if (totalPages === 0) {
    return null;
  }

  return (
    <div className="mt-8 flex flex-wrap items-center justify-between gap-4">
      <div className="flex items-center gap-2 text-sm">
        <span className="text-muted-foreground">Items per page</span>
        <select
          value={limit}
          onChange={(e) => onLimitChange?.(Number(e.target.value))}
          className="rounded-md border px-2 py-1 text-sm"
        >
          <option value={6}>6</option>
          <option value={12}>12</option>
          <option value={24}>24</option>
          <option value={48}>48</option>
        </select>
      </div>

      <div className="flex items-center gap-2">
        <Button
          variant="outline"
          onClick={() => onPageChange(Math.max(0, page - 1))}
          disabled={page === 0}
        >
          Previous
        </Button>

        <span className="px-4 text-sm text-muted-foreground">
          Page {page + 1} of {totalPages}
        </span>

        <Button
          variant="outline"
          onClick={() => onPageChange(Math.min(totalPages - 1, page + 1))}
          disabled={page === totalPages - 1}
        >
          Next
        </Button>
      </div>
    </div>
  );
}
