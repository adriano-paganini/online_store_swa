import { getErrorMessage } from '@/config/config';
import type { TPaymentRequestDTO, TPaymentResponseDTO } from '@/DTO/payment.types';
import axios from 'axios';

const processPayment = async (payment: TPaymentRequestDTO): Promise<TPaymentResponseDTO> => {
  try {
    const response = await axios.post<TPaymentResponseDTO>('/cart/payment', payment);
    return response.data;
  } catch (err: unknown) {
    if (axios.isAxiosError(err) && err.response?.data) {
      // Return the backend's error response structure if available
      return err.response.data as TPaymentResponseDTO;
    }
    throw new Error(`Error processing payment: ${getErrorMessage(err)}`);
  }
};

export const PaymentApi = {
  processPayment,
};
