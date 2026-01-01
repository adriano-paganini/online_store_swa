import { Link } from 'react-router-dom';
import { ROUTES } from '../utilities/routes.paths';

const NotFoundPage = () => {
  return (
    <div className="flex min-h-[60vh] flex-col items-center justify-center text-center">
      <h1 className="mb-4 text-4xl font-bold">404 – Page not found</h1>

      <p className="mb-6 text-muted-foreground">The page you are looking for does not exist.</p>

      <Link
        to={ROUTES.HOME}
        className="text-primary underline"
      >
        Go back to homepage
      </Link>
    </div>
  );
};

export default NotFoundPage;
