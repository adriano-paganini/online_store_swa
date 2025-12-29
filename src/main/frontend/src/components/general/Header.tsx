'use client';

import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { useUser } from '@/Contexts/authenticatedUserContext';
import { useCart } from '@/Contexts/cartContext';
import { LogOut, Menu, Search, Shield, ShoppingCart } from 'lucide-react';
import { Link } from 'react-router-dom';
import { ROUTES } from '../../utilities/routes.paths';
import { CartSidebar } from '../cart/CartSidebar';
import { Avatar, AvatarFallback, AvatarImage } from '../ui/avatar';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '../ui/dropdown-menu';
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetTrigger } from '../ui/sheet';

export function Header() {
  const { currentUser, isAdmin } = useUser();
  const isAuthenticated = !!currentUser;

  const userName =
    currentUser?.firstName || currentUser?.lastName
      ? `${currentUser?.firstName ?? ''} ${currentUser?.lastName ?? ''}`.trim()
      : currentUser?.username;

  const { cartItemCount } = useCart();

  const CartSheet = (
    <Sheet>
      <SheetTrigger asChild>
        <Button
          variant="ghost"
          size="icon"
          className="relative"
        >
          <ShoppingCart className="h-5 w-5" />
          {cartItemCount > 0 && (
            <div className="absolute -right-1 -top-1 flex h-5 w-5 items-center justify-center rounded-full bg-foreground p-0 text-xs text-background">
              <span>{cartItemCount}</span>
            </div>
          )}
        </Button>
      </SheetTrigger>

      <SheetContent
        side="right"
        className="w-full max-w-md px-0"
      >
        <SheetHeader>
          <SheetTitle className="py-2 text-center">Your Cart</SheetTitle>
        </SheetHeader>

        <CartSidebar />
      </SheetContent>
    </Sheet>
  );

  const MenuSheet = (
    <Sheet>
      <SheetTrigger asChild>
        <Button
          variant="ghost"
          size="icon"
          className="md:hidden"
        >
          <Menu className="h-5 w-5" />
        </Button>
      </SheetTrigger>

      <SheetContent
        side="right"
        className="max-w-[400px] px-0"
      >
        <SheetHeader>
          <div className="mt-4 flex items-center gap-3 px-4">
            <Avatar>
              <AvatarImage src="/placeholder.svg" />
              <AvatarFallback>
                {isAuthenticated ? `${currentUser?.firstName?.[0] ?? ''}${currentUser?.lastName?.[0] ?? ''}` : 'G'}
              </AvatarFallback>
            </Avatar>

            <div className="flex flex-col">
              <span className="text-sm font-medium">{isAuthenticated ? userName : 'Guest'}</span>
              <span className="text-xs text-muted-foreground">{isAuthenticated ? 'Account' : 'Not signed in'}</span>
            </div>
          </div>
        </SheetHeader>

        <div className="relative mx-4 mt-4">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            type="search"
            placeholder="Search products..."
            className="pl-10"
          />
        </div>

        <nav className="mt-6 flex flex-col gap-1 px-4">
          <Link to={ROUTES.HOME}>
            <Button
              variant="ghost"
              className="w-full justify-start"
            >
              Home
            </Button>
          </Link>

          {isAdmin && (
            <Link to={ROUTES.ADMIN_USERS}>
              <Button
                variant="ghost"
                className="w-full justify-start text-destructive"
              >
                <Shield className="mr-2 h-4 w-4" />
                Manage Users
              </Button>
            </Link>
          )}
        </nav>

        <div className="mt-6 border-t px-4 pt-4">
          {isAuthenticated ? (
            <Link to="/logout">
              <Button
                variant="ghost"
                className="w-full justify-start"
              >
                <LogOut className="mr-2 h-4 w-4" />
                Logout
              </Button>
            </Link>
          ) : (
            <>
              <Link to={ROUTES.LOGIN}>
                <Button
                  variant="ghost"
                  className="w-full justify-start"
                >
                  Log in
                </Button>
              </Link>
              <Link to={ROUTES.HOME}>
                <Button className="mt-2 w-full">Sign up</Button>
              </Link>
            </>
          )}
        </div>
      </SheetContent>
    </Sheet>
  );

  return (
    <header className="sticky top-0 z-50 w-full border-b bg-background/95 backdrop-blur">
      <div className="container mx-auto flex h-16 items-center justify-between px-4">
        <Link
          to={ROUTES.HOME}
          className="flex items-center gap-2"
        >
          <div className="flex h-8 w-8 items-center justify-center rounded-md bg-foreground text-background">
            <span className="text-lg font-bold">S</span>
          </div>
          <span className="text-lg font-semibold">Shop</span>
        </Link>

        <div className="hidden max-w-lg flex-1 px-8 md:flex">
          <div className="relative w-full">
            <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
            <Input
              type="search"
              placeholder="Search products..."
              className="pl-10"
            />
          </div>
        </div>

        <div className="flex items-center gap-2">
          <div className="hidden md:block">{CartSheet}</div>
          <div className="md:hidden">{CartSheet}</div>

          {isAuthenticated && (
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button
                  variant="ghost"
                  size="icon"
                  className="hidden rounded-full md:flex"
                >
                  <Avatar>
                    <AvatarImage src="/placeholder.svg" />
                    <AvatarFallback>
                      {currentUser?.firstName?.[0] ?? currentUser?.username?.[0]?.toUpperCase() ?? '?'}
                    </AvatarFallback>
                  </Avatar>
                </Button>
              </DropdownMenuTrigger>

              <DropdownMenuContent align="end">
                <div className="px-2 py-1.5 text-sm font-medium">{userName}</div>
                <DropdownMenuSeparator />
                <DropdownMenuItem asChild>
                  <Link to="/logout">
                    <LogOut className="mr-2 h-4 w-4" />
                    Logout
                  </Link>
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          )}

          {MenuSheet}
        </div>
      </div>
    </header>
  );
}
