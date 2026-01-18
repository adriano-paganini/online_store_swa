import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';

import { useCart } from '@/Contexts/cartContext';
import { AddressApi } from '@/utilities/addressApi';
import { OrderApi } from '@/utilities/orderApi';

import type { TAddressDTO } from '@/DTO/address.types';
import { ShippingMethod } from '@/DTO/order.types';

import { AddressSelector } from '@/components/checkout/AddressSelector';
import { CheckoutCartItems } from '@/components/checkout/CheckoutCartItems';
import { ShippingMethodSelector } from '@/components/checkout/ShippingMethodSelector';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

export function CheckoutPage() {
  const navigate = useNavigate();
  const { cart, itemLoadingIds, clearCart } = useCart();

  const [addresses, setAddresses] = useState<TAddressDTO[]>([]);
  const [shippingAddressId, setShippingAddressId] = useState<number | null>(null);
  const [billingAddressId, setBillingAddressId] = useState<number | null>(null);
  const [useSameAddress, setUseSameAddress] = useState(true);
  const [shippingMethod, setShippingMethod] = useState<ShippingMethod | null>(ShippingMethod.FAIRY_DUST_DISPATCH);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    AddressApi.fetchAddresses()
      .then(setAddresses)
      .catch(() => toast.error('Failed to load addresses'));
  }, []);

  useEffect(() => {
    if (!shippingAddressId && addresses.length > 0) {
      setShippingAddressId(addresses[0].id);
    }
  }, [shippingAddressId, addresses]);

  if (!cart || cart.items.length === 0) {
    return <p className="text-muted-foreground">Your cart is empty.</p>;
  }

  const handleSubmit = async () => {
    if (!shippingAddressId) {
      toast.error('Please select a shipping address');
      return;
    }

    if (!useSameAddress && !billingAddressId) {
      toast.error('Please select a billing address');
      return;
    }

    if (!shippingMethod) {
      toast.error('Please select a shipping method');
      return;
    }

    try {
      setSubmitting(true);

      const order = await OrderApi.createOrder({
        shippingAddressId,
        billingAddressId: useSameAddress ? shippingAddressId : billingAddressId!,
        shippingMethod,
      });

      await clearCart();

      navigate(`/payment/${order.orderNumber}`);
    } catch {
      toast.error('Failed to create order');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="mx-auto space-y-8">
      <CheckoutCartItems />

      <AddressSelector
        addresses={addresses}
        shippingAddressId={shippingAddressId}
        billingAddressId={billingAddressId}
        useSameAddress={useSameAddress}
        onShippingChange={setShippingAddressId}
        onBillingChange={setBillingAddressId}
        onToggleSame={setUseSameAddress}
      />

      <Card>
        <CardHeader>
          <CardTitle>Shipping Method</CardTitle>
        </CardHeader>
        <CardContent>
          <ShippingMethodSelector
            value={shippingMethod}
            onChange={setShippingMethod}
          />
        </CardContent>
      </Card>

      <Button
        className="w-full"
        size="lg"
        disabled={itemLoadingIds.size > 0 || submitting}
        onClick={() => void handleSubmit()}
      >
        {submitting ? 'Creating order…' : 'Confirm & go to payment'}
      </Button>
    </div>
  );
}
