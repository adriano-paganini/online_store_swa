import { getErrorMessage } from '@/config/config';
import axios from 'axios';
import type { TAddressCreateDTO, TAddressDTO, TAddressUpdateDTO } from '../DTO/address.types';

const fetchAddresses = async (): Promise<TAddressDTO[]> => {
  try {
    const response = await axios.get<TAddressDTO[]>('/addresses');
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error fetching addresses: ${getErrorMessage(err)}`);
  }
};

const createAddress = async (address: TAddressCreateDTO): Promise<TAddressDTO> => {
  try {
    const response = await axios.post<TAddressDTO>('/addresses', address);
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error creating address: ${getErrorMessage(err)}`);
  }
};

const updateAddress = async (id: number, address: TAddressUpdateDTO): Promise<TAddressDTO> => {
  try {
    const response = await axios.patch<TAddressDTO>(`/addresses/${id}`, address);
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error updating address: ${getErrorMessage(err)}`);
  }
};

const deleteAddress = async (id: number): Promise<void> => {
  try {
    await axios.delete(`/addresses/${id}`);
  } catch (err: unknown) {
    throw new Error(`Error deleting address: ${getErrorMessage(err)}`);
  }
};

export const AddressApi = {
  fetchAddresses,
  createAddress,
  updateAddress,
  deleteAddress,
};
