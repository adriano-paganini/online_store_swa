import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useUser } from '../../Contexts/authenticatedUserContext';
import { ROUTES } from '../../utilities/routes.paths';

const PrivateRoute = () => {
  const { currentUser } = useUser();
  const location = useLocation();

  if (!currentUser) {
    return (
      <Navigate
        to={ROUTES.LOGIN}
        replace
        state={{ from: location }}
      />
    );
  }

  return <Outlet />;
};

export default PrivateRoute;
