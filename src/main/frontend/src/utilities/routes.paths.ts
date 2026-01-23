export const ROUTES = {
  HOME: '/',
  PRODUCTS: '/products',
  PRODUCT_DETAIL: '/products/:id',
  SUBSCRIPTIONS: '/subscriptions',
  NOTIFICATIONS: '/notifications',
  PROFILE: '/profile',
  ADDRESSES: '/addresses',
  ORDERS: '/orders',
  CHECKOUT: '/checkout',
  PAYMENT: '/payment/:orderNumber',
  PAYMENT_SUCCESS: '/payment/success/:orderNum',
  ADMIN_USERS: '/admin/users',
  ADMIN_PRODUCTS: '/admin/products',
  AUTH: '/auth',
  LOGIN: '/auth/login',
  REGISTER: '/auth/register',
  LOGOUT: '/logout',
} as const;

export type TRoutesType = typeof ROUTES;
