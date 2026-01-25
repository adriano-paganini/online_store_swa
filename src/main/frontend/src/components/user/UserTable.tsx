/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
import React, { useCallback, useEffect, useState } from 'react';

import { toastApiError } from '@/lib/utils';
import { CheckedState } from '@radix-ui/react-checkbox';
import { ChevronDown, Plus } from 'lucide-react';
import { toast } from 'sonner';
import { TUserDTO, UserxRole, UserxTypes } from '../../DTO/userx.types';
import { UserxApi } from '../../utilities/userxApi';
import {
  createUserxFromInterfaces,
  createUserxRoleArrayFromStrings,
  TUserxValidationResult,
} from '../../utilities/userxUtilities';
import { Pagination } from '../general/Pagination';
import { Button } from '../ui/button';
import { Checkbox } from '../ui/checkbox';
import { DropdownMenu, DropdownMenuCheckboxItem, DropdownMenuContent, DropdownMenuTrigger } from '../ui/dropdown-menu';
import { Label } from '../ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { UserDeleteDialog } from './UserDeleteDialog';
import { UserDialog } from './UserDialog';
import { UserList } from './UserList';

const allRoles = Object.values(UserxRole);

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

  const [page, setPage] = useState(0);
  const [limit, setLimit] = useState(10);
  const [totalPages, setTotalPages] = useState(0);
  const [sort, setSort] = useState('id,desc');
  const [showDeleted, setShowDeleted] = useState(false);

  const [selectedRoles, setSelectedRoles] = useState<UserxRole[]>(allRoles);

  const loadUsers = useCallback(async () => {
    try {
      setLoading(true);

      const res = await UserxApi.fetchAllUsers({
        page,
        limit,
        sort,
        role: selectedRoles.length > 0 ? selectedRoles : undefined,
        deleted: showDeleted ? true : undefined,
      });

      const userxInstances = res.data.map((user: TUserDTO) => createUserxFromInterfaces(user));

      setUsers(userxInstances);
      setTotalPages(res.totalPages);
    } catch (err: unknown) {
      toastApiError(err);
    } finally {
      setLoading(false);
    }
  }, [page, limit, sort, selectedRoles, showDeleted]);

  useEffect(() => {
    void loadUsers();
  }, [loadUsers]);

  useEffect(() => {
    setPage(0);
  }, [limit, sort, selectedRoles, showDeleted]);

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
      toastApiError(err);
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
      toastApiError(err);
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
      toastApiError(err);
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

  const toggleRole = (role: UserxRole, checked: boolean) => {
    setSelectedRoles((prev) => (checked ? [...prev, role] : prev.filter((r) => r !== role)));
  };

  return (
    <div className="flex flex-col gap-4">
      <div className="flex flex-wrap items-center gap-4">
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="outline">
              Roles to show <ChevronDown className="ml-2 h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            {allRoles.map((role) => (
              <DropdownMenuCheckboxItem
                key={role}
                className="capitalize"
                checked={selectedRoles.includes(role)}
                onCheckedChange={(value) => toggleRole(role, value)}
              >
                {role}
              </DropdownMenuCheckboxItem>
            ))}
          </DropdownMenuContent>
        </DropdownMenu>

        <Select
          value={sort}
          onValueChange={setSort}
        >
          <SelectTrigger className="w-[220px]">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="id,desc">Newest</SelectItem>
            <SelectItem value="id,asc">Oldest</SelectItem>
            <SelectItem value="username,asc">Username A–Z</SelectItem>
            <SelectItem value="username,desc">Username Z–A</SelectItem>
            <SelectItem value="firstname,asc">First name A–Z</SelectItem>
            <SelectItem value="firstname,desc">First name Z–A</SelectItem>
            <SelectItem value="lastname,asc">Last name A–Z</SelectItem>
            <SelectItem value="lastname,desc">Last name Z–A</SelectItem>
          </SelectContent>
        </Select>

        <div className="flex items-center gap-2">
          <Checkbox
            checked={showDeleted}
            onCheckedChange={(checked) => setShowDeleted(checked === true)}
          />
          <Label>Deleted</Label>
        </div>

        <Button
          className="ml-auto"
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

      <Pagination
        page={page}
        limit={limit}
        totalPages={totalPages}
        onPageChange={setPage}
        onLimitChange={setLimit}
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
