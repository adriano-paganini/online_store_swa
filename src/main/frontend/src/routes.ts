import { ROUTES } from './utilities/routes.paths';
import AddressesPage from './views/AddressesPage';
import AdminProductsPage from './views/AdminProductsPage';
import AuthPage from './views/AuthPage';
import { CheckoutPage } from './views/CheckoutPage';
import HomePage from './views/HomePage';
import Logout from './views/Logout';
import ManageUsers from './views/ManageUsers';
import NotFoundPage from './views/NotFoundPage';
import NotificationsPage from './views/NotificationsPage';
import OrdersPage from './views/OrdersPage';
import PaymentPage from './views/PaymentPage';
import PaymentSuccessPage from './views/PaymentSuccessPage';
import ProductDetailsPage from './views/ProductDetailsPage';
import ProductsPage from './views/ProductsPage';
import ProfilePage from './views/ProfilePage';
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

export const ProfilePageRoute = {
  url: ROUTES.PROFILE,
  component: ProfilePage,
};

export const AddressesPageRoute = {
  url: ROUTES.ADDRESSES,
  component: AddressesPage,
};

export const OrdersPageRoute = {
  url: ROUTES.ORDERS,
  component: OrdersPage,
};

export const CheckoutPageRoute = {
  url: ROUTES.CHECKOUT,
  component: CheckoutPage,
};

export const PaymentPageRoute = {
  url: ROUTES.PAYMENT,
  component: PaymentPage,
};

export const PaymentSuccessPageRoute = {
  url: ROUTES.PAYMENT_SUCCESS,
  component: PaymentSuccessPage,
};

export const AdminProductsPageRoute = {
  url: ROUTES.ADMIN_PRODUCTS,
  component: AdminProductsPage,
};

export const ManageUsersRoute = {
  url: ROUTES.ADMIN_USERS,
  component: ManageUsers,
};

export const AuthPageRoute = {
  url: `${ROUTES.AUTH}/:mode`,
  component: AuthPage,
};

export const LogoutsRoute = {
  url: ROUTES.LOGOUT,
  component: Logout,
};

export const NotFoundRoute = {
  url: '*',
  component: NotFoundPage,
};
