import { ROUTES } from './utilities/routes.paths';
import AdminProductsPage from './views/AdminProductsPage';
import HomePage from './views/HomePage';
import Login from './views/Login';
import Logout from './views/Logout';
import ManageUsers from './views/ManageUsers';
import NotFoundPage from './views/NotFoundPage';
import NotificationsPage from './views/NotificationsPage';
import ProductDetailsPage from './views/ProductDetailsPage';
import ProductsPage from './views/ProductsPage';
import SubscriptionsPage from './views/SubscriptionsPage';

export const HomePageRoute = {
  url: ROUTES.HOME,
  component: HomePage,
};

export const ProductsPageRoute = {
  url: ROUTES.PRODUCTS,
  component: ProductsPage,
};

export const ProductDetailPageRoute = {
  url: ROUTES.PRODUCT_DETAIL,
  component: ProductDetailsPage,
};

export const SubscriptionsPageRoute = {
  url: ROUTES.SUBSCRIPTIONS,
  component: SubscriptionsPage,
};

export const NotificationsPageRoute = {
  url: ROUTES.NOTIFICATIONS,
  component: NotificationsPage,
};

export const AdminProductsPageRoute = {
  url: ROUTES.ADMIN_PRODUCTS,
  component: AdminProductsPage,
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

export const NotFoundRoute = {
  url: '*',
  component: NotFoundPage,
};

// ofr the future routes
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
