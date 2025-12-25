/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
import { useState } from 'react';

import { Alert, AlertDescription } from '@/components/ui/alert';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { AlertCircle, LogIn } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useUser } from '../Contexts/authenticatedUserContext';

/**
 * Login component
 */
const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const { login } = useUser();
  const navigate = useNavigate();

  const handleLogin = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (loading) {
      return;
    }

    setError(null);
    setLoading(true);

    try {
      await login({ username, password });
      // Redirect to home page
      navigate('/', { replace: true });
    } catch (err: any) {
      const status = err?.response?.status as number | undefined;

      if (status === 401 || status === 403) {
        setError('Wrong username or password');
      } else if (status === 500) {
        setError('Server error');
      } else if (status === undefined) {
        setError('No connection to server. Try again later');
      } else {
        setError('Login failed. Please try again.');
      }

      console.error('Login failed:', error);
    } finally {
      setPassword('');
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <div className="w-full max-w-md space-y-6">
        <div className="text-center">
          <div className="mb-4 flex items-center justify-center gap-2">
            <LogIn className="h-6 w-6 text-primary" />
            <h1 className="text-2xl font-bold">Login</h1>
          </div>
          <p className="text-muted-foreground">Enter your credentials to access the system</p>
        </div>

        <Card>
          <CardHeader>
            <CardTitle className="text-center text-xl">Welcome Back</CardTitle>
            <CardDescription className="text-center">Please enter your username and password.</CardDescription>
          </CardHeader>
          <CardContent>
            <form
              onSubmit={(e) => void handleLogin(e)}
              className="space-y-4"
            >
              {error && (
                <Alert variant="destructive">
                  <div className="flex items-center gap-2">
                    <AlertCircle className="h-4 w-4" />
                    <AlertDescription>{error}</AlertDescription>
                  </div>
                </Alert>
              )}

              <div className="space-y-1">
                <Label htmlFor="username">Username</Label>
                <Input
                  id="username"
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="Your username"
                  autoComplete="off"
                  required
                />
              </div>

              <div className="space-y-1">
                <Label htmlFor="password">Password</Label>
                <Input
                  id="password"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Your password"
                  autoComplete="off"
                  required
                />
              </div>

              <Button
                type="submit"
                className="w-full"
                disabled={loading}
              >
                {loading ? 'Logging in...' : 'Login'}
              </Button>
            </form>
          </CardContent>
          <CardFooter className="flex justify-center">
            <p className="text-xs text-muted-foreground">Use demo credentials: admin / passwd</p>
          </CardFooter>
        </Card>
      </div>
    </div>
  );
};

export default Login;
