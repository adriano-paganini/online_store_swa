import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { ShippingMethod } from '@/DTO/order.types';
import { SHIPPING_METHODS } from '@/utilities/shippingUtils';

type TShippingMethodSelectorProps = {
  value: ShippingMethod | null;
  onChange: (value: ShippingMethod) => void;
};

export function ShippingMethodSelector({ value, onChange }: TShippingMethodSelectorProps) {
  return (
    <RadioGroup
      value={value ?? undefined}
      onValueChange={(v) => onChange(v as ShippingMethod)}
    >
      {SHIPPING_METHODS.map((m) => (
        <Label
          key={m.key}
          className="flex cursor-pointer items-start gap-3 rounded-md border p-3 hover:bg-muted"
        >
          <RadioGroupItem value={m.key} />
          <div>
            <div className="font-medium">{m.label}</div>
            <div className="text-sm text-muted-foreground">{m.description}</div>
          </div>
        </Label>
      ))}
    </RadioGroup>
  );
}
