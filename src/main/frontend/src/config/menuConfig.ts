import { UserxRole } from '../DTO/userx.types';
import { ROUTES } from '../utilities/routes.paths';

export type TMenuItemConfig = {
  label: string;
  icon?: string;
  route?: string;
  roles?: UserxRole[];
  items?: TMenuItemConfig[];
};

export const menuConfig: TMenuItemConfig[] = [
  {
    label: 'Home',
    icon: 'pi pi-home',
    route: ROUTES.HOME,
  },
  {
    label: 'Admin Submenu',
    icon: 'pi pi-star',
    roles: [UserxRole.ADMIN],
    items: [
      {
        label: 'Manage Users',
        icon: 'pi pi-star',
        route: ROUTES.ADMIN_USERS,
        roles: [UserxRole.ADMIN],
      },
    ],
  },
  {
    label: 'Logout',
    icon: 'pi pi-sign-out',
    route: ROUTES.LOGOUT,
  },
];
