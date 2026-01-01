import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../Contexts/authenticatedUserContext';
import { ROUTES } from '../utilities/routes.paths';

const Logout = () => {
  const navigate = useNavigate();
  const { logout } = useUser();

  useEffect(() => {
    logout();
    navigate(ROUTES.HOME);
  }, [logout, navigate]);

  return null;
};

export default Logout;
