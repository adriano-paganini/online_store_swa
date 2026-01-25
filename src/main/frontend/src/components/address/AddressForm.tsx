'use client';

import { useState } from 'react';
import { toast } from 'sonner';

import type { TAddressCreateDTO, TAddressDTO, TAddressUpdateDTO } from '@/DTO/address.types';
import { AddressApi } from '@/utilities/addressApi';

import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { toastApiError } from '@/lib/utils';

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

  const [errors, setErrors] = useState<Partial<Record<keyof TAddressCreateDTO, string>>>({});

  const validate = (): boolean => {
    const nextErrors: Partial<Record<keyof TAddressCreateDTO, string>> = {};

    if (!values.street.trim()) nextErrors.street = 'Street is required';
    if (!values.number.trim()) nextErrors.number = 'Number is required';
    if (!values.city.trim()) nextErrors.city = 'City is required';
    if (!values.postalCode.trim()) nextErrors.postalCode = 'Postal code is required';
    if (!values.country.trim()) nextErrors.country = 'Country is required';

    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  const submit = async () => {
    if (!validate()) return;

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
    } catch (err) {
      toastApiError(err);
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
            onChange={(e) => {
              setValues({ ...values, street: e.target.value });
              setErrors((prev) => ({ ...prev, street: undefined }));
            }}
          />
          {errors.street && <p className="text-xs text-destructive">{errors.street}</p>}
        </div>

        <div className="space-y-1">
          <Label>Number</Label>
          <Input
            value={values.number}
            onChange={(e) => {
              setValues({ ...values, number: e.target.value });
              setErrors((prev) => ({ ...prev, number: undefined }));
            }}
            className="text-sm"
          />
          {errors.number && <p className="text-xs text-destructive">{errors.number}</p>}
        </div>
      </div>

      <div className="grid grid-cols-3 gap-4">
        <div className="col-span-2 space-y-1">
          <Label>City</Label>
          <Input
            value={values.city}
            onChange={(e) => {
              setValues({ ...values, city: e.target.value });
              setErrors((prev) => ({ ...prev, city: undefined }));
            }}
          />
          {errors.city && <p className="text-xs text-destructive">{errors.city}</p>}
        </div>

        <div className="space-y-1">
          <Label>Postal code</Label>
          <Input
            value={values.postalCode}
            onChange={(e) => {
              setValues({ ...values, postalCode: e.target.value });
              setErrors((prev) => ({ ...prev, postalCode: undefined }));
            }}
            className="text-sm"
          />
          {errors.postalCode && <p className="text-xs text-destructive">{errors.postalCode}</p>}
        </div>
      </div>

      <div className="space-y-1">
        <Label>Country</Label>
        <Input
          value={values.country}
          onChange={(e) => {
            setValues({ ...values, country: e.target.value });
            setErrors((prev) => ({ ...prev, country: undefined }));
          }}
        />
        {errors.country && <p className="text-xs text-destructive">{errors.country}</p>}
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
