import React from 'react';

import { useUser } from '@/Contexts/authenticatedUserContext';
import { UserxTypes } from '@/DTO/userx.types';
import { LoaderCircle } from 'lucide-react';
import { UserLine } from './UserLine';

type TUserListProps = {
  users: UserxTypes[];
  loading: boolean;
  onEditUser?: (user: UserxTypes) => void;
  onDeleteUser: (user: UserxTypes) => void;
};

/**
 * Component for displaying a list of users in a DataTable.
 * @param users the users to display
 * @param loading whether the users are loading
 * @param onEditUser callback when a user is edited
 * @param onDeleteUser callback when a user is deleted
 */
export const UserList: React.FC<TUserListProps> = ({ users, loading, onEditUser, onDeleteUser }) => {
  const { currentUser } = useUser();

  if (loading) {
    return (
      <div>
        <LoaderCircle />
      </div>
    );
  }

  if (!users || users.length == 0) {
    return <div className="mt-5 w-full text-center text-muted-foreground">No users yet.</div>;
  }

  return (
    <div className="overflow-hidden rounded-lg">
      {users.map((user) => (
        <UserLine
          isMe={currentUser?.username === user.username}
          key={user.id}
          user={user}
          onEditUser={onEditUser}
          onDeleteUser={onDeleteUser}
        />
      ))}
    </div>
  );
};
