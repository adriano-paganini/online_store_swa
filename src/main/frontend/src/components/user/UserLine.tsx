import { Badge } from '@/components/ui/badge';
import { UserxTypes } from '@/DTO/userx.types';
import { MoreHorizontal, PenLine, Trash2 } from 'lucide-react';
import { useState } from 'react';
import { Button } from '../ui/button';
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from '../ui/dropdown-menu';

type TUserLineProps = {
  isMe: boolean;
  user: UserxTypes;
  onEditUser?: (user: UserxTypes) => void;
  onDeleteUser: (user: UserxTypes) => void;
};

export function UserLine({ isMe, user, onEditUser, onDeleteUser }: TUserLineProps) {
  const fullName = `${user.firstName} ${user.lastName}`;
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <div
      className={`flex items-center gap-3 border-b px-4 py-3 text-sm transition-opacity hover:bg-muted ${
        user.enabled ? '' : 'opacity-50'
      }`}
    >
      <div className="flex-1">
        <div className="font-medium">@{user.username}</div>
        <div className="text-muted-foreground">{fullName}</div>
        <div className="text-sm text-muted-foreground">{user.email}</div>
      </div>

      <div className="flex flex-wrap gap-1">
        {[...(user?.roles ?? [])].map((role) => (
          <Badge
            key={role}
            variant="secondary"
          >
            {role}
          </Badge>
        ))}
      </div>

      <DropdownMenu
        open={menuOpen}
        onOpenChange={setMenuOpen}
      >
        <DropdownMenuTrigger asChild>
          <Button
            variant="ghost"
            size="icon"
          >
            <MoreHorizontal className="h-5 w-5" />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="end">
          {onEditUser && (
            <DropdownMenuItem
              onClick={() => {
                setMenuOpen(false);
                onEditUser(user);
              }}
            >
              <PenLine className="mr-2 h-4 w-4" />
              Edit
            </DropdownMenuItem>
          )}
          {!isMe && (
            <DropdownMenuItem
              className="text-destructive"
              onClick={() => {
                setMenuOpen(false);
                onDeleteUser(user);
              }}
            >
              <Trash2 className="mr-2 h-4 w-4 text-destructive" />
              Delete
            </DropdownMenuItem>
          )}
        </DropdownMenuContent>
      </DropdownMenu>
    </div>
  );
}
