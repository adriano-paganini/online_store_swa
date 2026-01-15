import { AxiosError } from 'axios';
import { AlertCircle, LogIn } from 'lucide-react';
import { useState } from 'react';

import { Alert, AlertDescription } from '@/components/ui/alert';
import { Button } from '@/components/ui/button';
import { CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

import { useUser } from '@/Contexts/authenticatedUserContext';

type TLoginFormProps = {
  onSuccess: () => void;
};

export function LoginForm({ onSuccess }: TLoginFormProps) {
  const { login } = useUser();

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (loading) return;

    setError(null);
    setLoading(true);

    try {
      await login({ username, password });
      onSuccess();
    } catch (err) {
      let status: number | undefined;

      if (err && typeof err === 'object' && 'response' in err) {
        const axiosErr = err as AxiosError;
        status = axiosErr.response?.status;
      }

      if (status === 401 || status === 403) {
        setError('Wrong username or password');
      } else {
        setError('Login failed');
      }
    } finally {
      setPassword('');
      setLoading(false);
    }
  };

  return (
    <CardContent>
      <form
        onSubmit={(e) => void handleSubmit(e)}
        className="flex flex-col gap-2"
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
          <Label>Username</Label>
          <Input
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="cappuccino_dispenser"
            required
          />
        </div>

        <div className="space-y-1">
          <Label>Password</Label>
          <Input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>

        <Button
          type="submit"
          className="mt-4 w-full"
          disabled={loading}
        >
          <LogIn className="mr-2 h-4 w-4" />
          {loading ? 'Logging in…' : 'Login'}
        </Button>
      </form>
    </CardContent>
  );
}
