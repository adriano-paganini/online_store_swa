'use client';

import { useState } from 'react';
import { toast } from 'sonner';

import type { TAddressCreateDTO, TAddressDTO, TAddressUpdateDTO } from '@/DTO/address.types';
import { AddressApi } from '@/utilities/addressApi';

import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

type TAddressFormProps = {
  address: TAddressDTO | null;
  onSuccess: () => void;
  setLoading: (loading: boolean) => void;
};

export function AddressForm({ address, onSuccess, setLoading }: TAddressFormProps) {
  const [values, setValues] = useState<TAddressCreateDTO>({
    country: address?.country ?? '',
    city: address?.city ?? '',
    postalCode: address?.postalCode ?? '',
    street: address?.street ?? '',
    number: address?.number ?? '',
    extra: address?.extra ?? '',
  });

  const submit = async () => {
    try {
      setLoading(true);

      if (address) {
        const update: TAddressUpdateDTO = values;
        await AddressApi.updateAddress(address.id, update);
        toast.success('Address updated');
      } else {
        await AddressApi.createAddress(values);
        toast.success('Address created');
      }

      onSuccess();
    } catch {
      toast.error('Failed to save address');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form
      id="address-form"
      className="space-y-4"
      onSubmit={(e) => {
        e.preventDefault();
        void submit();
      }}
    >
      <div className="grid grid-cols-3 gap-4">
        <div className="col-span-2 space-y-1">
          <Label>Street</Label>
          <Input
            value={values.street}
            onChange={(e) => setValues({ ...values, street: e.target.value })}
          />
        </div>

        <div className="space-y-1">
          <Label>Number</Label>
          <Input
            value={values.number}
            onChange={(e) => setValues({ ...values, number: e.target.value })}
            className="text-sm"
          />
        </div>
      </div>

      <div className="grid grid-cols-3 gap-4">
        <div className="col-span-2 space-y-1">
          <Label>City</Label>
          <Input
            value={values.city}
            onChange={(e) => setValues({ ...values, city: e.target.value })}
          />
        </div>

        <div className="space-y-1">
          <Label>Postal code</Label>
          <Input
            value={values.postalCode}
            onChange={(e) => setValues({ ...values, postalCode: e.target.value })}
            className="text-sm"
          />
        </div>
      </div>

      <div className="space-y-1">
        <Label>Country</Label>
        <Input
          value={values.country}
          onChange={(e) => setValues({ ...values, country: e.target.value })}
        />
      </div>

      <div className="space-y-1">
        <Label>Extra</Label>
        <Input
          value={values.extra ?? ''}
          onChange={(e) => setValues({ ...values, extra: e.target.value })}
        />
      </div>
    </form>
  );
}
