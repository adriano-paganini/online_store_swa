export type TPaymentMethodKey = 'credit_card' | 'netflix_password' | 'dad_joke';

type TPaymentMethodConfig = {
  key: TPaymentMethodKey;
  label: string;
  description: string;
  maxAmount?: number;
};

export const PAYMENT_METHODS: TPaymentMethodConfig[] = [
  {
    key: 'credit_card',
    label: 'Credit card',
    description: 'Visa, Mastercard, Amex',
  },
  {
    key: 'netflix_password',
    label: 'Netflix password',
    description: 'We promise to only watch one episode',
    maxAmount: 50,
  },
  {
    key: 'dad_joke',
    label: 'Dad joke',
    description: 'The cornier, the better',
    maxAmount: 20,
  },
];
