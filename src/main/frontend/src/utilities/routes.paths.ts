export const ROUTES = {
  HOME: '/',
  PRODUCTS: '/products',
  PRODUCT_DETAIL: '/products/:id',
  SUBSCRIPTIONS: '/subscriptions',
  NOTIFICATIONS: '/notifications',
  ADDRESSES: '/addresses',
  ORDERS: '/orders',
  ADMIN_USERS: '/admin/users',
  ADMIN_PRODUCTS: '/admin/products',
  LOGIN: '/login',
  LOGOUT: '/logout',

  // future routes
  // CHECKOUT: '/checkout',
  // ORDER_CONFIRMATION: '/order-confirmation',
  // PROFILE: '/profile',
  // REGISTER: '/register',
} as const;

export type TRoutesType = typeof ROUTES;
