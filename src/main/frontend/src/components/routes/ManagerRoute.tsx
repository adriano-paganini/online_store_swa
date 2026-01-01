import { useUser } from '@/Contexts/authenticatedUserContext';
import { UserxRole } from '@/DTO/userx.types';
import { ROUTES } from '@/utilities/routes.paths';
import { Navigate, Outlet } from 'react-router-dom';

const ManagerRoute = () => {
  const { currentUser } = useUser();

  if (!currentUser || !(currentUser.roles.includes(UserxRole.MANAGER) || currentUser.roles.includes(UserxRole.ADMIN))) {
    return (
      <Navigate
        to={ROUTES.HOME}
        replace
      />
    );
  }

  return <Outlet />;
};

export default ManagerRoute;
