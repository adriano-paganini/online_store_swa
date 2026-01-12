import type { TProductDTO } from '@/DTO/product.types';
import { MoreHorizontal, PenLine, Trash2 } from 'lucide-react';

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { Button } from '@/components/ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { getInitials } from '@/lib/utils';

type TProductRowProps = {
  product: TProductDTO;
  onEdit: (p: TProductDTO) => void;
  onDelete: (p: TProductDTO) => void;
};

export const ProductRow = ({ product, onEdit, onDelete }: TProductRowProps) => {
  return (
    <div className="flex items-center gap-3 border-b px-4 py-3 text-sm transition-opacity hover:bg-muted">
      <Avatar className="h-16 w-16 rounded-md">
        <AvatarImage
          src={product.images?.[0]}
          alt={product.name}
          className="object-cover"
        />
        <AvatarFallback className="flex h-full w-full items-center justify-center rounded-md bg-muted text-sm font-medium">
          {getInitials(product.name)}
        </AvatarFallback>
      </Avatar>

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
