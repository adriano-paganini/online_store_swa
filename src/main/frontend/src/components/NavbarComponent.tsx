/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
import { useUser } from '@/Contexts/authenticatedUserContext';
import { HomePageRoute, ManageUsersRoute } from '@/routes';
import React from 'react';
import { Link } from 'react-router-dom';
import {
  NavigationMenu,
  NavigationMenuContent,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  NavigationMenuTrigger,
} from './ui/navigation-menu';

export const NavbarComponent: React.FC = () => {
  const { isAdmin } = useUser();

  return (
    <NavigationMenu>
      <NavigationMenuList>
        <NavigationMenuItem>
          <NavigationMenuLink asChild>
            <Link to={HomePageRoute.url}>Home</Link>
          </NavigationMenuLink>
        </NavigationMenuItem>
        {isAdmin && (
          <NavigationMenuItem>
            <NavigationMenuTrigger>Admin Submenu</NavigationMenuTrigger>
            <NavigationMenuContent>
              <ul className="gap-3 p-5 md:w-[400px] lg:w-[500px]">
                <li className="row-span-3">
                  <NavigationMenuLink asChild>
                    <Link to={ManageUsersRoute.url}>Manage Users</Link>
                  </NavigationMenuLink>
                </li>
              </ul>
            </NavigationMenuContent>
          </NavigationMenuItem>
        )}
        <NavigationMenuItem>
          <NavigationMenuLink asChild>
            <Link to="/logout">Logout</Link>
          </NavigationMenuLink>
        </NavigationMenuItem>
      </NavigationMenuList>
    </NavigationMenu>
  );
};
