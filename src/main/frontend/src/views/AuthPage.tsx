import { LogIn, UserPlus } from 'lucide-react';
import { useNavigate, useParams } from 'react-router-dom';

import { LoginForm } from '@/components/auth/LoginForm';
import { RegisterForm } from '@/components/auth/RegisterForm';
import { Button } from '@/components/ui/button';
import { Card, CardDescription, CardFooter, CardHeader, CardTitle } from '@/components/ui/card';

type TAuthMode = 'login' | 'register';

export default function AuthPage() {
  const navigate = useNavigate();

  // Auth mode is controlled by the route: /auth/:mode
  const { mode } = useParams<{ mode: TAuthMode }>();

  // Fallback to "login" if the route param is missing or invalid
  const currentMode: TAuthMode = mode === 'register' ? 'register' : 'login';

  // Switch mode by updating the URL (keeps state URL-driven)
  const switchMode = (next: TAuthMode) => {
    navigate(`/auth/${next}`);
  };

  return (
    <div className="flex min-h-screen items-center justify-center px-4">
      <div className="w-full max-w-md space-y-6">
        <div className="space-y-2 text-center">
          <div className="flex items-center justify-center gap-2">
            {currentMode === 'login' ? (
              <LogIn className="h-6 w-6 text-primary" />
            ) : (
              <UserPlus className="h-6 w-6 text-primary" />
            )}
            <h1 className="text-2xl font-bold">{currentMode === 'login' ? 'Login' : 'Create account'}</h1>
          </div>
          <p className="text-muted-foreground">
            {currentMode === 'login' ? 'Enter your credentials to continue' : 'Fill in your details to get started'}
          </p>
        </div>

        <Card>
          <CardHeader className="text-center">
            <CardTitle>{currentMode === 'login' ? 'Welcome back' : 'Sign up'}</CardTitle>
            <CardDescription>
              {currentMode === 'login'
                ? 'Please enter your username and password.'
                : 'Create your account in a few steps.'}
            </CardDescription>
          </CardHeader>

          {currentMode === 'login' ? (
            <LoginForm onSuccess={() => navigate('/', { replace: true })} />
          ) : (
            <RegisterForm onSuccess={() => navigate('/auth/login')} />
          )}

          <CardFooter className="flex justify-center">
            <Button
              variant="link"
              onClick={() => switchMode(currentMode === 'login' ? 'register' : 'login')}
            >
              {currentMode === 'login' ? "Don't have an account? Sign up" : 'Already have an account? Login'}
            </Button>
          </CardFooter>
        </Card>
      </div>
    </div>
  );
}
