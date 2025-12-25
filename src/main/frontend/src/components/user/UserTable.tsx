/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
import React, { useEffect, useState } from 'react';

import { CheckedState } from '@radix-ui/react-checkbox';
import { Plus } from 'lucide-react';
import { toast } from 'sonner';
import { TUserDTO, UserxTypes } from '../../DTO/userx.types';
import { UserxApi } from '../../utilities/userxApi';
import {
  createUserxFromInterfaces,
  createUserxRoleArrayFromStrings,
  TUserxValidationResult,
} from '../../utilities/userxUtilities';
import { Button } from '../ui/button';
import { UserDeleteDialog } from './UserDeleteDialog';
import { UserDialog } from './UserDialog';
import { UserList } from './UserList';

/**
 * Component for managing users.
 */
const UserTable = () => {
  const [users, setUsers] = useState<UserxTypes[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [selectedUser, setSelectedUser] = useState<TUserDTO | null>(null);
  const [isNewUser, setIsNewUser] = useState<boolean>(false);
  const [dialogVisible, setDialogVisible] = useState<boolean>(false);
  const [deleteDialogVisible, setDeleteDialogVisible] = useState<boolean>(false);
  const [validation, setValidation] = useState<TUserxValidationResult>({
    valid: true,
  });

  /**
   * Fetch all users from the backend on mount once.
   */
  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const userxData = await UserxApi.fetchAllUsers();
        const userxInstances = userxData.map((user: TUserDTO) => createUserxFromInterfaces(user));
        setUsers(userxInstances);
      } catch (err: unknown) {
        console.error('Error fetching users:', err);
        toast.error('Error fetching users');
      } finally {
        setLoading(false); // Set loading to false regardless of success or failure
      }
    };
    void fetchUsers(); // ignore the returned promise; void explicit so ESLint doesn’t complain
  }, []); // empty dependency array means this effect will only run once on mount

  /**
   * Validate the user object.
   * @param user
   * @param opts
   */
  const validateUser = (
    user: TUserDTO | null,
    opts: { requirePassword?: boolean } = { requirePassword: true }
  ): TUserxValidationResult => {
    if (!user) return { valid: false, message: 'No user selected' };

    const required: (keyof TUserDTO)[] = ['firstName', 'lastName', 'username'];
    const { requirePassword = true } = opts; // password input on edit user not needed
    const fieldErrors: Partial<Record<keyof TUserDTO, string>> = {};

    required.forEach((k) => {
      const v = (user[k] as unknown as string) ?? '';
      if (!v.trim()) fieldErrors[k] = 'Required';
    });

    // check for password required
    const pwd = (user.password as unknown as string) ?? '';
    if (requirePassword && !pwd.trim()) fieldErrors.password = 'Required';

    // at least one role required (see also UserxCreateDTO in backend
    if (!Array.isArray(user.roles) || user.roles.length === 0) {
      fieldErrors.roles = 'Required';
    }

    const valid = Object.keys(fieldErrors).length === 0;
    return valid ? { valid } : { valid, message: 'Please fill in all required fields', fieldErrors };
  };

  /**
   * Handle the submit event for the user dialog.
   */
  const handleSubmit = async () => {
    const validationResult = validateUser(selectedUser, {
      requirePassword: isNewUser,
    });
    if (!validationResult.valid) {
      // Display an error eventMessage or handle the validation error
      setValidation(validationResult);
      console.error('Please fill in all required fields.');
      toast.error('Please fill in all required fields.');
      return;
    }

    setValidation({ valid: true });

    if (isNewUser) {
      await createUser();
    } else {
      await updateUser();
    }
    hideDialog();
  };

  /**
   * Create a new user and update the state.
   */
  const createUser = async () => {
    if (!selectedUser) return;

    try {
      const newUser: UserxTypes = await UserxApi.createUser(selectedUser);
      setUsers([...users, newUser]);

      toast.success('User created successfully');
    } catch (err: unknown) {
      console.error('Error saving user:', err);
      toast.error('Error saving user');
    }
  };

  /**
   * Update an existing user and update the state.
   */
  const updateUser = async () => {
    if (!selectedUser) return;

    try {
      const updatedUser: UserxTypes = await UserxApi.updateUser(selectedUser);
      setUsers(users.map((user: UserxTypes) => (user.id === updatedUser.id ? updatedUser : user)));

      toast.success('User updated successfully');
    } catch (err: unknown) {
      console.error('Error updating user:', err);
      toast.error('Error updating user');
    }
  };

  const deleteUser = async () => {
    if (!selectedUser?.id) return;

    const userId = selectedUser.id;

    try {
      await UserxApi.deleteUser(selectedUser);

      setUsers((prevUsers) => prevUsers.filter((user: UserxTypes) => user.id !== userId));

      hideDialog();
      toast.success('User deleted successfully');
    } catch (err: unknown) {
      console.error('Error deleting user:', err);
      toast.error('Error deleting user');
    } finally {
      setDeleteDialogVisible(false);
    }
  };

  /**
   * Open the edit dialog for a user.
   * @param user
   */
  const openEditDialog = (user: UserxTypes) => {
    setSelectedUser(user);
    setValidation({ valid: true });
    setIsNewUser(false);
    showDialog();
  };

  const openDeleteDialog = (user: UserxTypes) => {
    setSelectedUser(user);
    setDeleteDialogVisible(true);
  };

  /**
   * Open the dialog for creating a new user.
   */
  const openNewUserDialog = () => {
    setSelectedUser(UserxTypes.empty());
    setValidation({ valid: true });
    showDialog();
    setIsNewUser(true);
  };

  /**
   * Show the dialog.
   */
  const showDialog = () => {
    setValidation({ valid: true });
    setDialogVisible(true);
  };

  /**
   * Hide the dialog.
   */
  const hideDialog = () => {
    setValidation({ valid: true });
    setDialogVisible(false);
  };

  /**
   * Handle input changes for the user dialog.
   * @param event
   */
  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    if (!selectedUser) return;

    const { name, value } = event.target;

    setSelectedUser({
      ...selectedUser,
      [name]: value,
    });
  };

  /**
   * Handle user enabled change for the user dialog.
   * @param event
   */
  const handleUserEnabledChange = (checked: CheckedState, fieldName: string) => {
    if (!selectedUser || checked === 'indeterminate') return;

    const value = checked === true;

    setSelectedUser({
      ...selectedUser,
      [fieldName]: value,
    });
  };
  /**
   * Handle roles change for the user dialog.
   * @param event
   */
  const handleRolesChange = (event: { value: string[] }) => {
    if (!selectedUser) return;

    const roles = createUserxRoleArrayFromStrings(event.value);

    setSelectedUser({ ...selectedUser, roles: roles });
  };

  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-row items-center justify-between">
        <Button
          className="w-fit"
          onClick={openNewUserDialog}
        >
          <Plus />
          Add New User
        </Button>
      </div>

      <UserList
        users={users}
        loading={loading}
        onEditUser={openEditDialog}
        onDeleteUser={openDeleteDialog}
      />

      {/* Dialog for creating or editing a user */}
      <UserDialog
        visible={dialogVisible}
        user={selectedUser}
        isNewUser={isNewUser}
        validation={validation}
        onHide={hideDialog}
        onSubmit={() => {
          void handleSubmit();
        }}
        onInputChange={handleInputChange}
        onRolesChange={handleRolesChange}
        onUserEnabledChange={handleUserEnabledChange}
      />

      <UserDeleteDialog
        visible={deleteDialogVisible}
        onHide={() => setDeleteDialogVisible(false)}
        onDelete={() => {
          void deleteUser();
        }}
        user={selectedUser}
      />
    </div>
  );
};

export default UserTable;
