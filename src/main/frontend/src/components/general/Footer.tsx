import { Link } from 'react-router-dom';
import { ROUTES } from '../../utilities/routes.paths';

export function Footer() {
  return (
    <footer className="mt-2 border-t bg-muted/50">
      <div className="container mx-auto p-4">
        <div className="grid gap-8 md:grid-cols-4">
          <div className="text-left md:text-center">
            <h3 className="mb-4 text-lg font-semibold">Shop</h3>
            <p className="text-sm text-muted-foreground">
              Your one-stop destination for quality products at great prices.
            </p>
          </div>

          <div className="text-left md:text-center">
            <h4 className="mb-4 font-semibold">Navigation</h4>
            <ul className="flex flex-col items-start space-y-2 text-sm md:items-center">
              <li>
                <Link
                  to={ROUTES.HOME}
                  className="text-muted-foreground hover:text-foreground"
                >
                  Start Page
                </Link>
              </li>
              <li>
                <Link
                  to={ROUTES.PRODUCTS}
                  className="text-muted-foreground hover:text-foreground"
                >
                  Products
                </Link>
              </li>
              <li>
                <Link
                  to={ROUTES.CHECKOUT}
                  className="text-muted-foreground hover:text-foreground"
                >
                  Checkout
                </Link>
              </li>
            </ul>
          </div>

          <div className="text-left md:text-center">
            <h4 className="mb-4 font-semibold">Account</h4>
            <ul className="flex flex-col items-start space-y-2 text-sm md:items-center">
              <li>
                <Link
                  to={ROUTES.PROFILE}
                  className="text-muted-foreground hover:text-foreground"
                >
                  My Profile
                </Link>
              </li>
              <li>
                <Link
                  to={ROUTES.ADDRESSES}
                  className="text-muted-foreground hover:text-foreground"
                >
                  Addresses
                </Link>
              </li>
              <li>
                <Link
                  to={ROUTES.NOTIFICATIONS}
                  className="text-muted-foreground hover:text-foreground"
                >
                  Notifications
                </Link>
              </li>
            </ul>
          </div>

          <div className="text-left md:text-center">
            <h4 className="mb-4 font-semibold">Personal</h4>
            <ul className="flex flex-col items-start space-y-2 text-sm md:items-center">
              <li>
                <Link
                  to={ROUTES.ORDERS}
                  className="text-muted-foreground hover:text-foreground"
                >
                  Orders
                </Link>
              </li>
              <li>
                <Link
                  to={ROUTES.SUBSCRIPTIONS}
                  className="text-muted-foreground hover:text-foreground"
                >
                  Subscriptions
                </Link>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </footer>
  );
}
