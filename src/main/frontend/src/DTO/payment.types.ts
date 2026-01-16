export type TPaymentRequestDTO = {
  amount: number;
  paymentMethod: string;
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
