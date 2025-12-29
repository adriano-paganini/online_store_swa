/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
import { useEffect, useState } from 'react';
import { Navigate, Outlet, useLocation } from 'react-router-dom';

import { useUser } from '../../Contexts/authenticatedUserContext';
import { ROUTES } from '../../utilities/routes.paths';

/**
 * Private route component that checks if the user is authenticated.
 * Used to protect routes.
 */
const PrivateRoute = () => {
  enum AuthStatus {
    AUTHENTICATED = 200,
    UNAUTHENTICATED = 401,
    UNKNOWN = 0,
  }

  const { userIsAuthenticated } = useUser();
  const location = useLocation();

  const [authStatus, setAuthStatus] = useState<AuthStatus>(AuthStatus.UNKNOWN);

  useEffect(() => {
    const checkAuthentication = async () => {
      try {
        const isAuthenticated = await userIsAuthenticated();
        setAuthStatus(isAuthenticated ? AuthStatus.AUTHENTICATED : AuthStatus.UNAUTHENTICATED);
      } catch (err: unknown) {
        console.warn('Backend not available:', err);
        setAuthStatus(AuthStatus.UNAUTHENTICATED);
      }
    };

    void checkAuthentication();

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [userIsAuthenticated]);

  if (authStatus === AuthStatus.UNKNOWN) {
    return (
      <div className="flex min-h-screen items-center justify-center">
        <div
          className="h-8 w-8 animate-spin rounded-full border-4 border-muted border-t-primary"
          aria-label="Loading"
        />
      </div>
    );
  }

  return authStatus === AuthStatus.AUTHENTICATED ? (
    <Outlet />
  ) : (
    <Navigate
      to={ROUTES.LOGIN}
      replace
      state={{ from: location }}
    />
  );
};

export default PrivateRoute;
