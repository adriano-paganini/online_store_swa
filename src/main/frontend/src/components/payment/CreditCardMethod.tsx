import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';

type TCreditCardMethodProps = {
  cardNumber: string;
  cardHolderName: string;
  expiryMonth: string;
  expiryYear: string;
  cvv: string;
  onChange: (field: string, value: string) => void;
};

export function CreditCardMethod({
  cardNumber,
  cardHolderName,
  expiryMonth,
  expiryYear,
  cvv,
  onChange,
}: TCreditCardMethodProps) {
  const currentYear = new Date().getFullYear();

  return (
    <Card>
      <CardHeader>
        <CardTitle>Card details</CardTitle>
      </CardHeader>
      <CardContent className="space-y-3">
        <div>
          <Label>Card number</Label>
          <Input
            placeholder="1234 5678 9012 3456"
            maxLength={19}
            value={cardNumber}
            onChange={(e) =>
              onChange(
                'cardNumber',
                e.target.value
                  .replace(/\D/g, '')
                  .replace(/(.{4})/g, '$1 ')
                  .trim()
              )
            }
          />
        </div>

        <div>
          <Label>Card holder</Label>
          <Input
            value={cardHolderName}
            placeholder="John Doe"
            onChange={(e) => onChange('cardHolderName', e.target.value)}
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div>
            <Label>Expiration</Label>
            <div className="flex gap-2">
              <Select
                value={expiryMonth}
                onValueChange={(v) => onChange('expiryMonth', v)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="MM" />
                </SelectTrigger>
                <SelectContent>
                  {Array.from({ length: 12 }, (_, i) => i + 1).map((m) => (
                    <SelectItem
                      key={m}
                      value={m.toString().padStart(2, '0')}
                    >
                      {m.toString().padStart(2, '0')}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>

              <Select
                value={expiryYear}
                onValueChange={(v) => onChange('expiryYear', v)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="YY" />
                </SelectTrigger>
                <SelectContent>
                  {Array.from({ length: 10 }, (_, i) => currentYear + i).map((y) => (
                    <SelectItem
                      key={y}
                      value={y.toString().slice(2)}
                    >
                      {y}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>

          <div>
            <Label>CVC</Label>
            <Input
              maxLength={3}
              placeholder="000"
              value={cvv}
              onChange={(e) => onChange('cvv', e.target.value.replace(/\D/g, ''))}
            />
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
