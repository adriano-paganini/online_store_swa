import { UserDTO } from '@/DTO/userx.types';
import { Trash2, X } from 'lucide-react';
import React from 'react';
import { Button } from '../ui/button';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '../ui/dialog';

type TDeleteDialogProps = {
  visible: boolean;
  onHide: () => void;
  onDelete: () => void;
  user: UserDTO | null;
};

/**
 * Dialog for deleting a user.
 *
 * @param visible whether the dialog is visible
 * @param onHide callback when the dialog is hidden
 * @param onDelete callback when the user is deleted
 * @param user the user to be deleted
 *
 * @returns the delete dialog
 */
export const UserDeleteDialog: React.FC<TDeleteDialogProps> = ({ visible, onHide, onDelete, user }) => {
  return (
    <Dialog
      open={visible}
      onOpenChange={(isOpen) => !isOpen && onHide()}
    >
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Delete User</DialogTitle>
          <DialogDescription>This action cannot be undone. This will permanently delete the user.</DialogDescription>
        </DialogHeader>

        <div>
          Are you sure you want to <span className="font-semibold">permanently delete</span> the user @{user?.username}{' '}
          <span className="text-muted-foreground">
            ({user?.firstName} {user?.lastName})
          </span>
        </div>

        <DialogFooter>
          <Button
            onClick={onHide}
            variant="outline"
          >
            <X />
            Cancel
          </Button>
          <Button
            onClick={onDelete}
            autoFocus
            variant="destructive"
          >
            <Trash2 />
            Delete
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};
