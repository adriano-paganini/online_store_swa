/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
import React, { Suspense } from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { Toaster } from 'sonner';
import { MainLayout } from './components/general/MainLayout';
import AdminRoute from './components/routes/AdminRoute';
import ManagerRoute from './components/routes/ManagerRoute';
import PrivateRoute from './components/routes/PrivateRoute';
import { UserProvider } from './Contexts/authenticatedUserContext';
import { CartProvider } from './Contexts/cartContext';
import {
  AddressesPageRoute,
  AdminProductsPageRoute,
  HomePageRoute,
  LoginsRoute,
  LogoutsRoute,
  ManageUsersRoute,
  NotFoundRoute,
  NotificationsPageRoute,
  OrdersPageRoute,
  ProductDetailPageRoute,
  ProductsPageRoute,
  SubscriptionsPageRoute,
} from './routes';

const App: React.FC = () => {
  return (
    // Wrap the application in the UserProvider, which allows to access the authenticated user
    <UserProvider>
      <CartProvider>
        <Toaster
          richColors
          closeButton
        />

        <Suspense fallback={<div>Loading...</div>}>
          <BrowserRouter>
            <Routes>
              {/* public routes */}
              <Route
                path={LoginsRoute.url}
                Component={LoginsRoute.component}
              />

              <Route element={<MainLayout />}>
                <Route
                  path={HomePageRoute.url}
                  Component={HomePageRoute.component}
                />
                <Route
                  path={ProductsPageRoute.url}
                  Component={ProductsPageRoute.component}
                />
                <Route
                  path={ProductDetailPageRoute.url}
                  Component={ProductDetailPageRoute.component}
                />

                {/* authenticated routes */}
                <Route element={<PrivateRoute />}>
                  <Route
                    path={LogoutsRoute.url}
                    Component={LogoutsRoute.component}
                  />

                  <Route
                    path={AddressesPageRoute.url}
                    Component={AddressesPageRoute.component}
                  />

                  <Route
                    path={SubscriptionsPageRoute.url}
                    Component={SubscriptionsPageRoute.component}
                  />

                  <Route
                    path={NotificationsPageRoute.url}
                    Component={NotificationsPageRoute.component}
                  />

                  <Route
                    path={OrdersPageRoute.url}
                    Component={OrdersPageRoute.component}
                  />

                  {/* future */}
                  {/*
                  <Route
                    path={CheckoutPageRoute.url}
                    Component={CheckoutPageRoute.component}
                  />
                  <Route
                    path={ProfilePageRoute.url}
                    Component={ProfilePageRoute.component}
                  />
                  
                  */}
                </Route>

                {/* manager routes */}
                <Route element={<ManagerRoute />}>
                  <Route
                    path={AdminProductsPageRoute.url}
                    Component={AdminProductsPageRoute.component}
                  />
                </Route>

                {/* admin routes */}
                <Route element={<AdminRoute />}>
                  <Route
                    path={ManageUsersRoute.url}
                    Component={ManageUsersRoute.component}
                  />
                </Route>

                {/* catch all 404, must remain last */}
                <Route
                  path={NotFoundRoute.url}
                  Component={NotFoundRoute.component}
                />
              </Route>
            </Routes>
          </BrowserRouter>
        </Suspense>
      </CartProvider>
    </UserProvider>
  );
};

export default App;
