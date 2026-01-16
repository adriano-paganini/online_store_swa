import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import type { TPaymentMethodKey } from '@/utilities/paymentUtils';
import { PAYMENT_METHODS } from '@/utilities/paymentUtils';

type TPaymentMethodSelectorProps = {
  value: TPaymentMethodKey;
  total: number;
  onChange: (value: TPaymentMethodKey) => void;
};

export function PaymentMethodSelector({ value, total, onChange }: TPaymentMethodSelectorProps) {
  return (
    <RadioGroup
      value={value}
      onValueChange={onChange}
    >
      {PAYMENT_METHODS.map((m) => {
        const disabled = m.maxAmount !== undefined && total > m.maxAmount;

        return (
          <Label
            key={m.key}
            className={`flex cursor-pointer items-start gap-3 rounded-md border p-3 ${
              disabled ? 'opacity-50' : 'hover:bg-muted'
            }`}
          >
            <RadioGroupItem
              value={m.key}
              disabled={disabled}
            />
            <div>
              <div className="font-medium">{m.label}</div>
              <div className="text-sm text-muted-foreground">
                {m.description}
                {m.maxAmount && ` (up to €${m.maxAmount})`}
              </div>
            </div>
          </Label>
        );
      })}
    </RadioGroup>
  );
}
