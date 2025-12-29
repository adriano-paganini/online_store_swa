export const ROUTES = {
  HOME: '/',
  PRODUCTS: '/products',
  ADMIN_USERS: '/admin/users',
  LOGIN: '/login',
  LOGOUT: '/logout',

  // future routes
  // PRODUCT_DETAIL: '/products/:id',
  // CART: '/cart',
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
