import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { TPaymentErrors, TPaymentFormValues } from '@/DTO/payment.types';

type TCreditCardMethodProps = {
  values: TPaymentFormValues;
  errors: TPaymentErrors;
  onChange: (field: keyof TPaymentFormValues, value: string) => void;
};

export function CreditCardMethod({ values, errors, onChange }: TCreditCardMethodProps) {
  const now = new Date();
  const currentMonth = now.getMonth() + 1; // 1–12
  const currentYear = now.getFullYear();

  const selectedYearFull = values.expiryYear ? Number(`20${values.expiryYear}`) : null;

  const availableMonths =
    selectedYearFull === currentYear
      ? Array.from({ length: 12 - currentMonth + 1 }, (_, i) => currentMonth + i)
      : Array.from({ length: 12 }, (_, i) => i + 1);

  return (
    <Card>
      <CardHeader>
        <CardTitle>Card details</CardTitle>
      </CardHeader>

      <CardContent className="space-y-4">
        <div className="space-y-1">
          <Label>Card number</Label>
          <Input
            placeholder="1234 5678 9012 3456"
            maxLength={19}
            value={values.cardNumber}
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
          {errors.cardNumber && <p className="text-xs text-destructive">{errors.cardNumber}</p>}
        </div>

        <div className="space-y-1">
          <Label>Card holder</Label>
          <Input
            placeholder="John Doe"
            value={values.cardHolderName}
            onChange={(e) => onChange('cardHolderName', e.target.value)}
          />
          {errors.cardHolderName && <p className="text-xs text-destructive">{errors.cardHolderName}</p>}
        </div>

        <div className="grid grid-cols-2 gap-4">
          <div className="space-y-1">
            <Label>Expiration</Label>
            <div className="flex gap-2">
              <Select
                value={values.expiryMonth}
                onValueChange={(v) => onChange('expiryMonth', v)}
              >
                <SelectTrigger>
                  <SelectValue placeholder="MM" />
                </SelectTrigger>

                <SelectContent>
                  {availableMonths.map((m) => {
                    const value = m.toString().padStart(2, '0');
                    return (
                      <SelectItem
                        key={value}
                        value={value}
                      >
                        {value}
                      </SelectItem>
                    );
                  })}
                </SelectContent>
              </Select>

              <Select
                value={values.expiryYear}
                onValueChange={(v) => {
                  onChange('expiryYear', v);

                  const fullYear = Number(`20${v}`);
                  if (fullYear === currentYear && Number(values.expiryMonth) < currentMonth) {
                    onChange('expiryMonth', '');
                  }
                }}
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

            {(errors.expiryMonth ?? errors.expiryYear) && (
              <p className="text-xs text-destructive">{errors.expiryMonth ?? errors.expiryYear}</p>
            )}
          </div>

          <div className="space-y-1">
            <Label>CVC</Label>
            <Input
              maxLength={3}
              placeholder="000"
              value={values.cvv}
              onChange={(e) => onChange('cvv', e.target.value.replace(/\D/g, ''))}
            />
            {errors.cvv && <p className="text-xs text-destructive">{errors.cvv}</p>}
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
