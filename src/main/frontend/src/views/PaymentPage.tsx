import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { toast } from 'sonner';

import { OrderApi } from '@/utilities/orderApi';
import { calculateOrderTotal } from '@/utilities/orderUtils';
import { PaymentApi } from '@/utilities/paymentApi';

import type { TOrderDTO } from '@/DTO/order.types';
import type { TPaymentRequestDTO } from '@/DTO/payment.types';
import type { TPaymentMethodKey } from '@/utilities/paymentUtils';

import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Separator } from '@/components/ui/separator';

import { CreditCardMethod } from '@/components/payment/CreditCardMethod';
import { DadJokeMethod } from '@/components/payment/DadJokeMethod';
import { NetflixPasswordMethod } from '@/components/payment/NetflixPasswordMethod';
import { PaymentMethodSelector } from '@/components/payment/PaymentMethodSelector';

export default function PaymentPage() {
  const { orderNumber } = useParams<{ orderNumber: string }>();

  const [order, setOrder] = useState<TOrderDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const [method, setMethod] = useState<TPaymentMethodKey>('credit_card');

  const [cardNumber, setCardNumber] = useState('');
  const [cardHolderName, setCardHolderName] = useState('');
  const [expiryMonth, setExpiryMonth] = useState('');
  const [expiryYear, setExpiryYear] = useState('');
  const [cvv, setCvv] = useState('');

  useEffect(() => {
    if (!orderNumber) return;

    OrderApi.fetchOrderByNumber(orderNumber)
      .then(setOrder)
      .catch(() => toast.error('Failed to load order'))
      .finally(() => setLoading(false));
  }, [orderNumber]);

  if (loading) return <p>Loading payment details…</p>;
  if (!order) return <p>Order not found.</p>;

  const total = calculateOrderTotal(order.items);

  const handlePay = async () => {
    if (method === 'credit_card' && cardNumber.replace(/\s/g, '').length !== 16) {
      toast.error('Invalid card details');
      return;
    }

    try {
      setSubmitting(true);

      const payload: TPaymentRequestDTO = {
        amount: total,
        paymentMethod: method,
        cardNumber: method === 'credit_card' || method === 'netflix_password' ? cardNumber : undefined,
        cardHolderName: method === 'credit_card' || method === 'dad_joke' ? cardHolderName : undefined,
        expiryDate: method === 'credit_card' ? `${expiryMonth}/${expiryYear}` : undefined,
        cvv: method === 'credit_card' ? cvv : undefined,
      };

      const res = await PaymentApi.processPayment(payload);

      if (res.success) toast.success(res.message);
      else toast.error(res.message);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="mx-auto max-w-xl space-y-6">
      <Card>
        <CardHeader>
          <CardTitle>Order #{order.orderNumber}</CardTitle>
        </CardHeader>
        <CardContent className="space-y-2">
          {order.items.map((i) => (
            <div
              key={i.productId}
              className="flex justify-between text-sm"
            >
              <span>
                {i.productName} × {i.quantity}
              </span>
              <span>€{(i.priceAtPurchase * (1 - i.appliedDiscount) * i.quantity).toFixed(2)}</span>
            </div>
          ))}
          <Separator />
          <div className="flex justify-between font-medium">
            <span>Total</span>
            <span>€{total.toFixed(2)}</span>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Payment method</CardTitle>
        </CardHeader>
        <CardContent>
          <PaymentMethodSelector
            value={method}
            total={total}
            onChange={setMethod}
          />
        </CardContent>
      </Card>

      {method === 'credit_card' && (
        <CreditCardMethod
          cardNumber={cardNumber}
          cardHolderName={cardHolderName}
          expiryMonth={expiryMonth}
          expiryYear={expiryYear}
          cvv={cvv}
          onChange={(f, v) => {
            if (f === 'cardNumber') setCardNumber(v);
            if (f === 'cardHolderName') setCardHolderName(v);
            if (f === 'expiryMonth') setExpiryMonth(v);
            if (f === 'expiryYear') setExpiryYear(v);
            if (f === 'cvv') setCvv(v);
          }}
        />
      )}

      {method === 'netflix_password' && (
        <NetflixPasswordMethod
          value={cardNumber}
          onChange={setCardNumber}
        />
      )}

      {method === 'dad_joke' && (
        <DadJokeMethod
          value={cardHolderName}
          onChange={setCardHolderName}
        />
      )}

      <Button
        className="w-full"
        disabled={submitting}
        onClick={() => void handlePay()}
      >
        {submitting ? 'Processing payment…' : 'Pay now'}
      </Button>
    </div>
  );
}
