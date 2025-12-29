/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
import UserTable from '@/components/user/UserTable';
import React from 'react';

/**
 * Component / View for managing users.
 */

class ManageUsers extends React.Component {
  render() {
    return <UserTable />;
  }
}

export default ManageUsers;
