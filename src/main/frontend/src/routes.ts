import { ROUTES } from './utilities/routes.paths';
import HomePage from './views/HomePage';
import Login from './views/Login';
import Logout from './views/Logout';
import ManageUsers from './views/ManageUsers';
import ProductsPage from './views/ProductsPage';

export const HomePageRoute = {
  url: ROUTES.HOME,
  component: HomePage,
};

export const ProductsPageRoute = {
  url: ROUTES.PRODUCTS,
  component: ProductsPage,
};

export const ManageUsersRoute = {
  url: ROUTES.ADMIN_USERS,
  component: ManageUsers,
};

export const LoginsRoute = {
  url: ROUTES.LOGIN,
  component: Login,
};

export const LogoutsRoute = {
  url: ROUTES.LOGOUT,
  component: Logout,
};

// ofr the future routes
//
// export const ProductDetailPageRoute = {
//   url: ROUTES.PRODUCT_DETAIL,
//   component: ProductDetailPage,
// }
//
// export const CheckoutPageRoute = {
//   url: ROUTES.CHECKOUT,
//   component: CheckoutPage,
// }
//
// export const OrderConfirmationPageRoute = {
//   url: "/order-confirmation/:orderNumber",
//   component: OrderConfirmationPage,
// }
//
// export const ProfilePageRoute = {
//   url: ROUTES.PROFILE,
//   component: ProfilePage,
// }
//
// export const OrdersPageRoute = {
//   url: ROUTES.ORDERS,
//   component: OrdersPage,
// }
//
// export const NotificationsPageRoute = {
//   url: ROUTES.NOTIFICATIONS,
//   component: NotificationsPage,
// }

// Array of all routes for easy iteration
export const allRoutes = [
  HomePageRoute,
  ProductsPageRoute,
  ManageUsersRoute,
  LoginsRoute,
  LogoutsRoute,
  // ProductDetailPageRoute,
  // CheckoutPageRoute,
  // OrderConfirmationPageRoute,
  // ProfilePageRoute,
  // OrdersPageRoute,
  // NotificationsPageRoute,
];
