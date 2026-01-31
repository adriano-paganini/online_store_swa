import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

type TDadJokeMethodProps = {
  value: string;
  error?: string;
  onChange: (value: string) => void;
};

export function DadJokeMethod({ value, error, onChange }: TDadJokeMethodProps) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Dad joke</CardTitle>
      </CardHeader>

      <CardContent className="space-y-1">
        <Label>Your joke</Label>
        <Input
          placeholder="I’m afraid for the calendar… its days are numbered."
          value={value}
          onChange={(e) => onChange(e.target.value)}
        />
        {error && <p className="text-xs text-destructive">{error}</p>}
      </CardContent>
    </Card>
  );
}
