export type TPaymentRequestDTO = {
  amount: number;
  paymentMethod: string;
  orderNumber: string;
  cardNumber?: string;
  cardHolderName?: string;
  expiryDate?: string;
  cvv?: string;
};

export type TPaymentResponseDTO = {
  success: boolean;
  transactionId: string | null;
  message: string;
  timestamp: string;
};

// ui only
export type TPaymentFormValues = {
  cardNumber: string;
  cardHolderName: string;
  expiryMonth: string;
  expiryYear: string;
  cvv: string;
};

export type TPaymentErrors = Partial<Record<keyof TPaymentFormValues, string>>;
