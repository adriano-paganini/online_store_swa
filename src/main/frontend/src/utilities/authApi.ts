/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */

import axios from 'axios';
import { TLoginDTO, TLoginResponse, TRegistrationDTO } from '../DTO/auth.types';

import { getErrorMessage } from '@/config/config';
import globalAxios from 'axios';

/**
 * Try to log in a user
 * @param login the login data of the user (username and password)
 *
 * @returns Promise with the status and data of the response
 * @throws Error if the request fails
 */
const login = async (login: TLoginDTO): Promise<TLoginResponse> => {
  // Send the request, await the response
  const response = await globalAxios.post<TLoginResponse>(`/authentication/login`, login);

  // Return the response
  return response.data;
};

const register = async (register: TRegistrationDTO): Promise<void> => {
  try {
    const r = await axios.post('/registration', register);
    console.log('r:', r);
  } catch (err: unknown) {
    throw new Error(`Error fetching cart: ${getErrorMessage(err)}`);
  }
};

export const AuthApi = {
  login,
  register,
};
