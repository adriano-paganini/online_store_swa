/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { UserDTO } from '@/DTO/userx.types';
import { UserxValidationResult } from '@/utilities/userxUtilities';
import { CheckedState } from '@radix-ui/react-checkbox';
import { AlertTriangle, Check, X } from 'lucide-react';
import React from 'react';
import { Alert, AlertDescription, AlertTitle } from '../ui/alert';
import { Button } from '../ui/button';
import { UserForm } from './UserForm';

type TUserDialogProps = {
  visible: boolean;
  user: UserDTO | null;
  isNewUser: boolean;
  validation: UserxValidationResult;
  onHide: () => void;
  onSubmit: () => void;
  onInputChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  onRolesChange: (event: { value: string[] }) => void;
  onUserEnabledChange: (checked: CheckedState, fieldName: string) => void;
};

/**
 * Dialog for creating or editing a user.
 */
export const UserDialog: React.FC<TUserDialogProps> = ({
  visible,
  user,
  isNewUser,
  validation,
  onHide,
  onSubmit,
  onInputChange,
  onRolesChange,
  onUserEnabledChange,
}) => {
  return (
    <Dialog
      open={visible}
      onOpenChange={(isOpen) => {
        if (!isOpen) onHide();
      }}
    >
      <DialogContent className="sm:max-w-2xl">
        <DialogHeader>
          <DialogTitle>{isNewUser ? 'Create New User' : 'Edit User'}</DialogTitle>
          <DialogDescription>
            {isNewUser ? 'Fill in the form below to create a new user.' : 'Modify the fields and save your changes.'}
          </DialogDescription>
        </DialogHeader>

        {validation.message && (
          <Alert
            variant="destructive"
            className="mb-4"
          >
            <AlertTriangle className="h-5 w-5" />
            <AlertTitle>Error</AlertTitle>
            <AlertDescription>{validation.message}</AlertDescription>
          </Alert>
        )}

        {user && (
          <UserForm
            user={user}
            isNewUser={isNewUser}
            fieldErrors={validation.fieldErrors}
            onInputChange={onInputChange}
            onRolesChange={onRolesChange}
            onUserEnabledChange={onUserEnabledChange}
          />
        )}

        <DialogFooter className="mt-4">
          <DialogClose asChild>
            <Button
              type="button"
              variant="outline"
            >
              <X className="h-4 w-4" />
              Cancel
            </Button>
          </DialogClose>

          <Button
            type="button"
            variant="default"
            onClick={onSubmit}
            autoFocus
          >
            <Check className="h-4 w-4" />
            {isNewUser ? 'Create' : 'Save'}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};
