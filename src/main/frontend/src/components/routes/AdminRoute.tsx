import { Navigate, Outlet } from 'react-router-dom';
import { useUser } from '../../Contexts/authenticatedUserContext';
import { UserxRole } from '../../DTO/userx.types';
import { ROUTES } from '../../utilities/routes.paths';

const AdminRoute = () => {
  const { currentUser } = useUser();

  if (!currentUser?.roles.includes(UserxRole.ADMIN)) {
    return (
      <Navigate
        to={ROUTES.HOME}
        replace
      />
    );
  }

  return <Outlet />;
};

export default AdminRoute;
