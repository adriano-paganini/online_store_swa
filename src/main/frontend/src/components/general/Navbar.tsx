/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
import { useUser } from '@/Contexts/authenticatedUserContext';
import { HomePageRoute, ManageUsersRoute } from '@/routes';
import { Home, LogOut, Shield } from 'lucide-react';
import { Link } from 'react-router-dom';
import { Avatar, AvatarFallback, AvatarImage } from '../ui/avatar';
import { Button } from '../ui/button';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '../ui/dropdown-menu';

type TNavbarProps = {
  isAdminPage?: boolean;
};

export function Navbar({ isAdminPage = false }: TNavbarProps) {
  const { currentUser, isAdmin } = useUser();

  const userName = currentUser?.firstName + ' ' + currentUser?.lastName;

  return (
    <header className="sticky top-0 z-10 border-b bg-background">
      <div className="container mx-auto flex items-center justify-between px-4 py-4 sm:px-6 lg:px-8">
        <h1 className="flex flex-row items-center gap-1 text-xl font-bold">
          {isAdminPage && (
            <Link
              className="mr-2 rounded-md border p-2 hover:bg-muted"
              to={HomePageRoute.url}
            >
              <Home className="h-5 w-5" />
            </Link>
          )}
          ?Shop Name?
          {isAdminPage && <Shield className="h-5 w-5 text-destructive" />}
        </h1>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button
              variant="ghost"
              size="icon"
              className="rounded-full"
            >
              <Avatar>
                <AvatarImage
                  src="/placeholder.svg"
                  alt="User"
                />
                <AvatarFallback>
                  {currentUser?.firstName?.charAt(0) ?? currentUser?.username?.charAt(0).toUpperCase() ?? '?'}
                  {currentUser?.lastName?.charAt(0) ?? currentUser?.username?.charAt(1).toUpperCase() ?? '?'}
                </AvatarFallback>
              </Avatar>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <div className="px-2 py-1.5 text-sm font-medium">{userName ?? currentUser?.username ?? 'Unknown'}</div>
            {isAdmin && (
              <>
                <DropdownMenuSeparator />
                <DropdownMenuItem asChild>
                  <Link to={ManageUsersRoute.url}>
                    <Shield className="mr-2 h-4 w-4 text-destructive" />
                    Manage Users
                  </Link>
                </DropdownMenuItem>
              </>
            )}
            <DropdownMenuSeparator />
            <DropdownMenuItem asChild>
              <Link to="/logout">
                <LogOut className="mr-2 h-4 w-4" />
                Logout
              </Link>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
}
