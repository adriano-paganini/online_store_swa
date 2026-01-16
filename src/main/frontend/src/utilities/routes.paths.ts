export const ROUTES = {
  HOME: '/',
  PRODUCTS: '/products',
  PRODUCT_DETAIL: '/products/:id',
  SUBSCRIPTIONS: '/subscriptions',
  NOTIFICATIONS: '/notifications',
  ADDRESSES: '/addresses',
  ORDERS: '/orders',
  CHECKOUT: '/checkout',
  PAYMENT: '/payment/:orderNumber',
  ADMIN_USERS: '/admin/users',
  ADMIN_PRODUCTS: '/admin/products',
  AUTH: '/auth',
  LOGIN: '/auth/login',
  REGISTER: '/auth/register',
  LOGOUT: '/logout',

  // future routes
  // PROFILE: '/profile',
} as const;

export type TRoutesType = typeof ROUTES;
