import type { TAddressDTO } from '@/DTO/address.types';

import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Checkbox } from '@/components/ui/checkbox';
import { Label } from '@/components/ui/label';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import { ROUTES } from '@/utilities/routes.paths';
import { Link } from 'react-router-dom';
import { Button } from '../ui/button';

type TAddressSelectorProps = {
  addresses: TAddressDTO[];
  shippingAddressId: number | null;
  billingAddressId: number | null;
  useSameAddress: boolean;
  onShippingChange: (id: number) => void;
  onBillingChange: (id: number) => void;
  onToggleSame: (value: boolean) => void;
};

export function AddressSelector({
  addresses,
  shippingAddressId,
  billingAddressId,
  useSameAddress,
  onShippingChange,
  onBillingChange,
  onToggleSame,
}: TAddressSelectorProps) {
  return (
    <div className="space-y-3">
      <Card>
        <CardHeader>
          <CardTitle>Shipping address</CardTitle>
        </CardHeader>
        <CardContent>
          {addresses && addresses.length >= 1 ? (
            <RadioGroup
              value={shippingAddressId?.toString()}
              onValueChange={(v) => onShippingChange(Number(v))}
              className="gap-1"
            >
              {addresses.map((a) => {
                const id = `shipping-address-${a.id}`;

                return (
                  <div
                    key={a.id}
                    className="flex items-start gap-3 rounded-md border p-3 transition-colors hover:bg-muted"
                  >
                    <RadioGroupItem
                      id={id}
                      value={a.id.toString()}
                      className="mt-1"
                    />
                    <Label
                      htmlFor={id}
                      className="w-full cursor-pointer text-sm leading-tight"
                    >
                      {a.street} {a.number}
                      {a.extra ? `, ${a.extra}` : null}
                      <br />
                      <span className="text-muted-foreground">
                        {a.postalCode} {a.city}, {a.country}
                      </span>
                    </Label>
                  </div>
                );
              })}
            </RadioGroup>
          ) : (
            <Link to={ROUTES.ADDRESSES}>
              <Button>Create an Address</Button>
            </Link>
          )}
        </CardContent>
      </Card>

      {addresses && addresses.length >= 1 && (
        <div className="flex items-center gap-2">
          <Checkbox
            checked={useSameAddress}
            onCheckedChange={(v) => onToggleSame(Boolean(v))}
          />
          <Label>Use same address for billing</Label>
        </div>
      )}

      {!useSameAddress && (
        <Card>
          <CardHeader>
            <CardTitle>Billing address</CardTitle>
          </CardHeader>
          <CardContent>
            <RadioGroup
              value={billingAddressId?.toString()}
              onValueChange={(v) => onBillingChange(Number(v))}
              className="gap-1"
            >
              {addresses.map((a) => {
                const id = `billing-address-${a.id}`;

                return (
                  <div
                    key={a.id}
                    className="flex items-start gap-3 rounded-md border p-3 transition-colors hover:bg-muted"
                  >
                    <RadioGroupItem
                      id={id}
                      value={a.id.toString()}
                      className="mt-1"
                    />
                    <Label
                      htmlFor={id}
                      className="w-full cursor-pointer text-sm leading-tight"
                    >
                      {a.street} {a.number}
                      {a.extra ? `, ${a.extra}` : null}
                      <br />
                      <span className="text-muted-foreground">
                        {a.postalCode} {a.city}, {a.country}
                      </span>
                    </Label>
                  </div>
                );
              })}
            </RadioGroup>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
