export const ROUTES = {
  HOME: '/',
  PRODUCTS: '/products',
  PRODUCT_DETAIL: '/products/:id',
  ADMIN_USERS: '/admin/users',
  LOGIN: '/login',
  LOGOUT: '/logout',

  // future routes
  // CHECKOUT: '/checkout',
  // ORDER_CONFIRMATION: '/order-confirmation',
  // ORDERS: '/orders',
  // ORDER_DETAIL: '/orders/:id',
  // PROFILE: '/profile',
  // ADDRESSES: '/addresses',
  // SUBSCRIPTIONS: '/subscriptions',
  // NOTIFICATIONS: '/notifications',
  // REGISTER: '/register',
  // ADMIN_PRODUCTS: '/admin/products',
} as const;

export type TRoutesType = typeof ROUTES;
