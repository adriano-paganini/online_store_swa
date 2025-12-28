/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
import { Footer } from '@/components/general/Footer';
import { Navbar } from '@/components/general/Navbar';
import UserTable from '@/components/user/UserTable';
import React from 'react';

/**
 * Component / View for managing users.
 */

class ManageUsers extends React.Component {
  render() {
    return (
      <div>
        <Navbar isAdminPage={true} />
        <UserTable />
        <Footer />
      </div>
    );
  }
}

export default ManageUsers;
