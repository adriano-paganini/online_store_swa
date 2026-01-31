import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { toast } from 'sonner';

import { OrderApi } from '@/utilities/orderApi';
import { calculateOrderTotal } from '@/utilities/orderUtils';
import { PaymentApi } from '@/utilities/paymentApi';

import type { TOrderDTO } from '@/DTO/order.types';
import type { TPaymentErrors, TPaymentFormValues, TPaymentRequestDTO } from '@/DTO/payment.types';
import type { TPaymentMethodKey } from '@/utilities/paymentUtils';

import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Separator } from '@/components/ui/separator';

import { CreditCardMethod } from '@/components/payment/CreditCardMethod';
import { DadJokeMethod } from '@/components/payment/DadJokeMethod';
import { NetflixPasswordMethod } from '@/components/payment/NetflixPasswordMethod';
import { PaymentMethodSelector } from '@/components/payment/PaymentMethodSelector';
import { toastApiError } from '@/lib/utils';

export default function PaymentPage() {
  const { orderNumber } = useParams<{ orderNumber: string }>();
  const navigate = useNavigate();

  const [order, setOrder] = useState<TOrderDTO | null>(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const [method, setMethod] = useState<TPaymentMethodKey>('credit_card');

  const [values, setValues] = useState<TPaymentFormValues>({
    cardNumber: '',
    cardHolderName: '',
    expiryMonth: '',
    expiryYear: '',
    cvv: '',
  });

  const [errors, setErrors] = useState<TPaymentErrors>({});

  const setField = (field: keyof TPaymentFormValues, value: string) => {
    setValues((prev) => ({ ...prev, [field]: value }));
    setErrors((prev) => ({ ...prev, [field]: undefined }));
  };

  useEffect(() => {
    if (!orderNumber) return;

    OrderApi.fetchOrderByNumber(orderNumber)
      .then(setOrder)
      .catch((err) => toastApiError(err))
      .finally(() => setLoading(false));
  }, [orderNumber]);

  const validate = (): boolean => {
    const nextErrors: TPaymentErrors = {};

    if (method === 'credit_card') {
      const digits = values.cardNumber.replace(/\s/g, '');
      if (!digits) nextErrors.cardNumber = 'Card number is required';
      else if (digits.length !== 16) nextErrors.cardNumber = 'Card number must be 16 digits';

      if (!values.cardHolderName.trim()) nextErrors.cardHolderName = 'Card holder name is required';

      if (!values.expiryMonth || !values.expiryYear) nextErrors.expiryMonth = 'Expiration date is required';

      if (!values.cvv) nextErrors.cvv = 'CVC is required';
      else if (values.cvv.length !== 3) nextErrors.cvv = 'CVC must be 3 digits';
    }

    if (method === 'netflix_password') {
      if (!values.cardNumber.trim()) nextErrors.cardNumber = 'Netflix password is required';
    }

    if (method === 'dad_joke') {
      if (!values.cardHolderName.trim()) nextErrors.cardHolderName = 'A dad joke is required';
      else if (values.cardHolderName.length < 10) nextErrors.cardHolderName = 'That joke is… too short';
    }

    setErrors(nextErrors);
    return Object.keys(nextErrors).length === 0;
  };

  if (loading) return <p>Loading payment details…</p>;
  if (!order) return <p>Order not found.</p>;

  const total = calculateOrderTotal(order.items);

  const handlePay = async () => {
    if (!orderNumber) {
      toast.error("Can't find the Order Number");
      return;
    }

    if (!validate()) return;

    try {
      setSubmitting(true);

      const payload: TPaymentRequestDTO = {
        amount: total,
        paymentMethod: method,
        orderNumber,
        cardNumber: method === 'credit_card' || method === 'netflix_password' ? values.cardNumber : undefined,
        cardHolderName: method === 'credit_card' || method === 'dad_joke' ? values.cardHolderName : undefined,
        expiryDate: method === 'credit_card' ? `${values.expiryMonth}/${values.expiryYear}` : undefined,
        cvv: method === 'credit_card' ? values.cvv : undefined,
      };

      const res = await PaymentApi.processPayment(payload);

      if (res.success) {
        toast.success(res.message);
        navigate(`/payment/success/${order.orderNumber}`, { replace: true });
      }
    } catch (err) {
      toastApiError(err);
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
          values={values}
          errors={errors}
          onChange={setField}
        />
      )}

      {method === 'netflix_password' && (
        <NetflixPasswordMethod
          value={values.cardNumber}
          error={errors.cardNumber}
          onChange={(v) => setField('cardNumber', v)}
        />
      )}

      {method === 'dad_joke' && (
        <DadJokeMethod
          value={values.cardHolderName}
          error={errors.cardHolderName}
          onChange={(v) => setField('cardHolderName', v)}
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
