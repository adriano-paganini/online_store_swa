import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

type TNetflixPasswordMethodProps = {
  value: string;
  error?: string;
  onChange: (value: string) => void;
};

export function NetflixPasswordMethod({ value, error, onChange }: TNetflixPasswordMethodProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Netflix password</CardTitle>
      </CardHeader>

      <CardContent className="space-y-1">
        <Label>Password</Label>
        <Input
          type="password"
          placeholder="hunter2"
          value={value}
          onChange={(e) => onChange(e.target.value)}
        />
        {error && <p className="text-xs text-destructive">{error}</p>}
      </CardContent>
    </Card>
  );
}
