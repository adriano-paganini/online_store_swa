export const ROUTES = {
  HOME: '/',
  LOGIN: '/login',
  LOGOUT: '/logout',
  ADMIN_USERS: '/admin/users',

  // future routes
  // PRODUCTS: '/products',
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
