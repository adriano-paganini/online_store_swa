import type { TProductDTO } from '@/DTO/product.types';
import { MoreHorizontal, PenLine, Trash2 } from 'lucide-react';

import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';

type TProductRowProps = {
  product: TProductDTO;
  onEdit: (p: TProductDTO) => void;
  onDelete: (p: TProductDTO) => void;
};

export const ProductRow = ({ product, onEdit, onDelete }: TProductRowProps) => {
  return (
    <div className="flex items-center gap-3 border-b px-4 py-3 text-sm transition-opacity hover:bg-muted">
      <img
        src={product.images[0] || `/placeholder.svg?height=64&width=64`}
        alt={product.name}
        className="h-16 w-16 rounded-md object-cover"
      />

      <div className="flex-1">
        <div className="font-medium">{product.name}</div>
        <div className="line-clamp-2 text-sm text-muted-foreground">{product.description}</div>
      </div>

      <div className="w-28 text-sm">${product.price.toFixed(2)}</div>
      <div className="w-24 text-sm">{Math.round(product.discount * 100)}%</div>
      <div className="w-24 text-sm">{product.stock}</div>

      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button
            variant="ghost"
            size="icon"
          >
            <MoreHorizontal />
          </Button>
        </DropdownMenuTrigger>

        <DropdownMenuContent align="end">
          <DropdownMenuItem onClick={() => onEdit(product)}>
            <PenLine className="mr-2 h-4 w-4" />
            Edit
          </DropdownMenuItem>
          <DropdownMenuItem
            className="text-destructive"
            onClick={() => onDelete(product)}
          >
            <Trash2 className="mr-2 h-4 w-4" />
            Delete
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );
};
