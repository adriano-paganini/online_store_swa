/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
import React, { Suspense } from 'react';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import { Toaster } from 'sonner';
import { MainLayout } from './components/general/MainLayout';
import PrivateRoute from './components/general/PrivateRoute';
import { UserProvider } from './Contexts/authenticatedUserContext';
import { CartProvider } from './Contexts/cartContext';
import { HomePageRoute, LoginsRoute, LogoutsRoute, ManageUsersRoute, ProductsPageRoute } from './routes';

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
              <Route
                path={LoginsRoute.url}
                Component={LoginsRoute.component}
              />
              {/* Protected Routes (authentication required) */}
              <Route element={<MainLayout />}>
                <Route element={<PrivateRoute />}>
                  <Route
                    path={HomePageRoute.url}
                    Component={HomePageRoute.component}
                  />
                  <Route
                    path={ProductsPageRoute.url}
                    element={<ProductsPageRoute.component />}
                  />
                  <Route
                    path={ManageUsersRoute.url}
                    Component={ManageUsersRoute.component}
                  />
                  <Route
                    path={LogoutsRoute.url}
                    Component={LogoutsRoute.component}
                  />
                </Route>
              </Route>
              {/* end of protected routes */}
            </Routes>
          </BrowserRouter>
        </Suspense>
      </CartProvider>
    </UserProvider>
  );
};

export default App;
