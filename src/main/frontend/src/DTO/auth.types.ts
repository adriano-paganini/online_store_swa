/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */

export type TLoginDTO = { username: string; password: string };

export type TLoginResponse = { bearerToken: string };

export type TRegistrationDTO = {
  username: string;
  firstName: string;
  lastName: string;
  password: string;
  email: string;
  phone?: string;
};
