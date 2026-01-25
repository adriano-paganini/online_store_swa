/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
import { useUser } from '@/Contexts/authenticatedUserContext';
import { TUserDTO, UserxRole } from '@/DTO/userx.types';
import React from 'react';
import { Checkbox } from '../ui/checkbox';
import { Input } from '../ui/input';
import { Label } from '../ui/label';

type TUserFormProps = {
  user: TUserDTO;
  isNewUser: boolean;
  fieldErrors?: Partial<Record<keyof TUserDTO, string>>;
  onInputChange: (event: React.ChangeEvent<HTMLInputElement>) => void;
  onRolesChange: (event: { value: string[] }) => void;
};

/**
 * Form for creating or editing a user.
 */
export const UserForm: React.FC<TUserFormProps> = ({ user, isNewUser, fieldErrors, onInputChange, onRolesChange }) => {
  const { currentUser } = useUser();
  const roleSet = new Set(user.roles ?? []);

  return (
    <div className="flex flex-col gap-4">
      {isNewUser && (
        <div className="flex-auto space-y-1">
          <Label htmlFor="username">Username</Label>
          <Input
            id="username"
            name="username"
            value={user.username}
            onChange={onInputChange}
            placeholder="Username"
            autoComplete="off"
            required
          />
          {fieldErrors?.username && <p className="text-sm text-red-600">{fieldErrors.username}</p>}
        </div>
      )}

      <div className="flex-auto space-y-1">
        <Label htmlFor="firstName">First Name</Label>
        <Input
          id="firstName"
          name="firstName"
          value={user.firstName}
          onChange={onInputChange}
          placeholder="First Name"
          autoComplete="off"
        />
        {fieldErrors?.firstName && <p className="text-sm text-red-600">{fieldErrors.firstName}</p>}
      </div>

      <div className="flex-auto space-y-1">
        <Label htmlFor="lastName">Last Name</Label>
        <Input
          id="lastName"
          name="lastName"
          value={user.lastName}
          onChange={onInputChange}
          placeholder="Last Name"
          autoComplete="off"
        />
        {fieldErrors?.lastName && <p className="text-sm text-red-600">{fieldErrors.lastName}</p>}
      </div>

      <div className="flex-auto space-y-1">
        <Label htmlFor="email">E-Mail</Label>
        <Input
          id="email"
          name="email"
          value={user.email ?? ''}
          onChange={onInputChange}
          placeholder="E-Mail"
          autoComplete="off"
        />
        {fieldErrors?.email && <p className="text-sm text-red-600">{fieldErrors.email}</p>}
      </div>

      {isNewUser && (
        <div className="flex-auto space-y-1">
          <Label htmlFor="password">Password</Label>
          <Input
            id="password"
            name="password"
            value={user.password}
            onChange={onInputChange}
            placeholder="Password"
            autoComplete="off"
            type="password"
          />
          {fieldErrors?.password && <p className="text-sm text-red-600">{fieldErrors.password}</p>}
        </div>
      )}

      <div className="flex-auto space-y-1">
        <Label htmlFor="phone">Phone</Label>
        <Input
          id="phone"
          name="phone"
          value={user.phone ?? ''}
          onChange={onInputChange}
          placeholder="+43 123 1234567"
          autoComplete="off"
        />
        {fieldErrors?.phone && <p className="text-sm text-red-600">{fieldErrors.phone}</p>}
      </div>

      <div className="flex-auto">
        <Label className="mb-2 block font-bold">Roles</Label>
        <div className="space-y-2">
          {Object.values(UserxRole).map((role) => {
            const hasRole = roleSet.has(role);
            const isCurrentUserAdmin = user.username === currentUser?.username && role === UserxRole.ADMIN;

            return (
              <div
                key={role}
                className="flex items-center gap-2"
              >
                <Checkbox
                  id={`role-${role}`}
                  checked={hasRole}
                  disabled={isCurrentUserAdmin}
                  onCheckedChange={() => {
                    const updatedRoles = new Set(roleSet);

                    if (hasRole) {
                      updatedRoles.delete(role);
                    } else {
                      updatedRoles.add(role);
                    }

                    onRolesChange({ value: Array.from(updatedRoles) });
                  }}
                />

                <Label
                  htmlFor={`role-${role}`}
                  className="text-sm"
                >
                  {UserxRole[role] ?? role}
                </Label>
              </div>
            );
          })}
        </div>
        {fieldErrors?.roles && <p className="mt-1 text-sm text-red-600">{fieldErrors.roles}</p>}
      </div>
    </div>
  );
};
