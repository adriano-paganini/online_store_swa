/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
import React from "react";
import { NavbarComponent } from "../components/NavbarComponent";
import { FooterComponent } from "../components/FooterComponent";
import UserTable from "@/components/user/UserTable";

/**
 * Component / View for managing users.
 */

class ManageUsers extends React.Component {
  render() {
    return (
      <div>
        <NavbarComponent />
        <UserTable />
        <FooterComponent />
      </div>
    );
  }
}

export default ManageUsers;
