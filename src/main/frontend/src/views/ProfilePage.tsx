import { useEffect, useState } from 'react';

import { Alert, AlertDescription } from '@/components/ui/alert';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { getErrorMessage } from '@/config/config';
import { TUserMeDTO, TUserMeUpdateDTO } from '@/DTO/userx.types';
import { UserxApi } from '@/utilities/userxApi';

export default function ProfilePage() {
  const [user, setUser] = useState<TUserMeDTO | null>(null);
  const [form, setForm] = useState<TUserMeUpdateDTO>({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    const loadMe = async () => {
      try {
        const me = await UserxApi.getMe();
        setUser(me);
        setForm({
          username: me.username,
          firstName: me.firstName,
          lastName: me.lastName,
          email: me.email,
          phone: me.phone,
        });
      } catch (err: unknown) {
        setError(getErrorMessage(err));
      } finally {
        setLoading(false);
      }
    };

    void loadMe();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setForm((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    setSuccess(false);

    try {
      // destructure password out so it does not get sent by default
      const { password, ...rest } = form;

      // only include password in payload if user actually provided one
      const payload: TUserMeUpdateDTO = password ? { ...rest, password } : rest;

      const updated = await UserxApi.updateMe(payload);
      setUser(updated);
      setSuccess(true);

      // remove password from form state entirely after save to not include on next send
      setForm(rest);
    } catch (err: unknown) {
      setError(getErrorMessage(err));
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center py-10">
        <p className="text-muted-foreground">Loading profile…</p>
      </div>
    );
  }

  if (!user) {
    return (
      <Alert variant="destructive">
        <AlertDescription>Profile not available</AlertDescription>
      </Alert>
    );
  }

  return (
    <div className="flex justify-center px-4 py-10">
      <Card className="w-full max-w-2xl">
        <CardHeader>
          <CardTitle>My Profile</CardTitle>
        </CardHeader>

        <CardContent>
          {error && (
            <Alert
              variant="destructive"
              className="mb-4"
            >
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          {success && (
            <Alert className="mb-4">
              <AlertDescription>Profile updated successfully</AlertDescription>
            </Alert>
          )}

          <form
            onSubmit={(e) => void handleSubmit(e)}
            className="space-y-4"
          >
            <div className="grid gap-2">
              <Label htmlFor="username">Username</Label>
              <Input
                id="username"
                name="username"
                value={form.username ?? ''}
                onChange={handleChange}
                minLength={3}
                maxLength={50}
                required
              />
            </div>

            <div className="grid gap-2 sm:grid-cols-2">
              <div>
                <Label htmlFor="firstName">First name</Label>
                <Input
                  id="firstName"
                  name="firstName"
                  value={form.firstName ?? ''}
                  onChange={handleChange}
                  maxLength={50}
                />
              </div>

              <div>
                <Label htmlFor="lastName">Last name</Label>
                <Input
                  id="lastName"
                  name="lastName"
                  value={form.lastName ?? ''}
                  onChange={handleChange}
                  maxLength={50}
                />
              </div>
            </div>

            <div className="grid gap-2">
              <Label htmlFor="email">Email</Label>
              <Input
                id="email"
                type="email"
                name="email"
                value={form.email ?? ''}
                onChange={handleChange}
                maxLength={100}
              />
            </div>

            <div className="grid gap-2">
              <Label htmlFor="phone">Phone</Label>
              <Input
                id="phone"
                name="phone"
                value={form.phone ?? ''}
                onChange={handleChange}
                maxLength={20}
              />
            </div>

            <div className="grid gap-2">
              <Label htmlFor="password">New password</Label>
              <Input
                id="password"
                type="password"
                name="password"
                value={form.password ?? ''}
                onChange={handleChange}
                minLength={8}
                maxLength={72}
                placeholder="Leave empty to keep current password"
              />
            </div>

            <Button
              type="submit"
              className="w-full"
              disabled={saving}
            >
              {saving ? 'Saving…' : 'Save changes'}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
