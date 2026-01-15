import { AxiosError } from 'axios';
import { AlertCircle, UserPlus } from 'lucide-react';
import { useState } from 'react';

import type { TRegistrationDTO } from '@/DTO/auth.types'; // adjust import if needed

import { Alert, AlertDescription } from '@/components/ui/alert';
import { Button } from '@/components/ui/button';
import { CardContent } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

import { AuthApi } from '@/utilities/authApi';

type TRegisterFormProps = {
  onSuccess: () => void;
};

export function RegisterForm({ onSuccess }: TRegisterFormProps) {
  const [form, setForm] = useState<TRegistrationDTO>({
    username: '',
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    phone: '',
  });

  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const update = (key: keyof TRegistrationDTO, value: string) => {
    setForm((prev) => ({ ...prev, [key]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (loading) return;

    setError(null);
    setLoading(true);

    try {
      const { register } = AuthApi;
      await register(form);
      onSuccess();
    } catch (err) {
      let message = 'Registration failed';

      if (err && typeof err === 'object' && 'response' in err) {
        const axiosErr = err as AxiosError;
        if (axiosErr.response?.status === 409) {
          message = 'Username or email already exists';
        } else if (axiosErr.response?.status === 400) {
          message = 'Invalid registration data';
        }
      }

      setError(message);
      console.error('Registration failed:', err);
    } finally {
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

        <div className="grid grid-cols-2 gap-4">
          <div className="space-y-1">
            <Label>First name</Label>
            <Input
              value={form.firstName}
              onChange={(e) => update('firstName', e.target.value)}
              placeholder="John"
              required
            />
          </div>

          <div className="space-y-1">
            <Label>Last name</Label>
            <Input
              value={form.lastName}
              onChange={(e) => update('lastName', e.target.value)}
              placeholder="Doe"
              required
            />
          </div>
        </div>

        <div className="space-y-1">
          <Label>Username</Label>
          <Input
            value={form.username}
            onChange={(e) => update('username', e.target.value)}
            placeholder="cappuccino_dispenser"
            required
          />
        </div>

        <div className="space-y-1">
          <Label>Email</Label>
          <Input
            type="email"
            value={form.email}
            onChange={(e) => update('email', e.target.value)}
            placeholder="test@email.com"
            required
          />
        </div>

        <div className="space-y-1">
          <Label>Password</Label>
          <Input
            type="password"
            value={form.password}
            onChange={(e) => update('password', e.target.value)}
            required
          />
        </div>

        <div className="space-y-1">
          <Label>Phone (optional)</Label>
          <Input
            value={form.phone ?? ''}
            onChange={(e) => update('phone', e.target.value)}
            placeholder="+42145678"
          />
        </div>

        <Button
          type="submit"
          className="mt-4 w-full"
          disabled={loading}
        >
          <UserPlus className="h-4 w-4" />
          {loading ? 'Creating account…' : 'Create account'}
        </Button>
      </form>
    </CardContent>
  );
}
