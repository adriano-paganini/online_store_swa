export const ROUTES = {
  HOME: '/',
  PRODUCTS: '/products',
  PRODUCT_DETAIL: '/products/:id',
  SUBSCRIPTIONS: '/subscriptions',
  NOTIFICATIONS: '/notifications',
  ADDRESSES: '/addresses',
  ADMIN_USERS: '/admin/users',
  ADMIN_PRODUCTS: '/admin/products',
  LOGIN: '/login',
  LOGOUT: '/logout',

  // future routes
  // CHECKOUT: '/checkout',
  // ORDER_CONFIRMATION: '/order-confirmation',
  // ORDERS: '/orders',
  // ORDER_DETAIL: '/orders/:id',
  // PROFILE: '/profile',
  // REGISTER: '/register',
} as const;

export type TRoutesType = typeof ROUTES;
