import { getErrorMessage } from '@/config/config';
import type { TPaymentRequestDTO, TPaymentResponseDTO } from '@/DTO/payment.types';
import axios from 'axios';

const processPayment = async (payment: TPaymentRequestDTO): Promise<TPaymentResponseDTO> => {
  try {
    const response = await axios.post<TPaymentResponseDTO>('/cart/payment', payment);
    return response.data;
  } catch (err: unknown) {
    throw new Error(getErrorMessage(err));
  }
};

export const PaymentApi = {
  processPayment,
};
