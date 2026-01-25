import { getErrorMessage } from '@/config/config';
import { TPageResponseDTO, TPaginationParams } from '@/DTO/pagination.types';
import axios from 'axios';
import { TUserDTO, TUserMeDTO, TUserMeUpdateDTO, UserxRole, UserxTypes } from '../DTO/userx.types';
import { createUserxFromInterfaces } from './userxUtilities';

/**
 * Fetch all users from the backend
 */
const fetchAllUsers = async (
  params: TPaginationParams & { role?: UserxRole[]; deleted?: boolean }
): Promise<TPageResponseDTO<TUserDTO>> => {
  try {
    const response = await axios.get<TPageResponseDTO<TUserDTO>>('/admin/users', { params });
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error fetching users: ${getErrorMessage(err)}`);
  }
};

/**
 * Create a new user
 */
const createUser = async (selectedUser: TUserDTO): Promise<UserxTypes> => {
  try {
    const userxInstance = createUserxFromInterfaces(selectedUser);
    const response = await axios.post<TUserDTO>('/admin/users', userxInstance.toCreateJSON());
    return UserxTypes.fromJSON(response.data);
  } catch (err: unknown) {
    throw new Error(`Error saving user: ${getErrorMessage(err)}`);
  }
};

/**
 * Update an existing user
 */
const updateUser = async (selectedUser: TUserDTO): Promise<UserxTypes> => {
  try {
    const userxInstance = createUserxFromInterfaces(selectedUser);
    const response = await axios.patch<TUserDTO>(`/admin/users/${selectedUser.id}`, userxInstance.toUpdateJSON());
    return UserxTypes.fromJSON(response.data);
  } catch (err: unknown) {
    throw new Error(`Error updating user: ${getErrorMessage(err)}`);
  }
};

/**
 * Delete an existing user
 */
const deleteUser = async (selectedUser: TUserDTO): Promise<void> => {
  try {
    await axios.delete(`/admin/users/${selectedUser.id}`);
  } catch (err: unknown) {
    throw new Error(`Error deleting user: ${getErrorMessage(err)}`);
  }
};

/**
 * Return currently logged-in user
 */
const getCurrentUser = async (): Promise<UserxTypes> => {
  try {
    const response = await axios.get<TUserDTO>('/users/me');
    return UserxTypes.fromJSON(response.data);
  } catch (err: unknown) {
    throw new Error(`Error determining current user: ${getErrorMessage(err)}`);
  }
};

/**
 * Return true if user is authenticated
 */
const isAuthenticated = async (): Promise<boolean> => {
  try {
    const response = await axios.get('/users/authenticated');
    return response.status >= 200 && response.status < 300;
  } catch {
    return false;
  }
};

const getMe = async (): Promise<TUserMeDTO> => {
  try {
    const response = await axios.get<TUserMeDTO>('/users/me');
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error fetching current user: ${getErrorMessage(err)}`);
  }
};

const updateMe = async (dto: TUserMeUpdateDTO): Promise<TUserMeDTO> => {
  try {
    const response = await axios.patch<TUserMeDTO>('/users/me', dto);
    return response.data;
  } catch (err: unknown) {
    throw new Error(`Error updating current user: ${getErrorMessage(err)}`);
  }
};

export const UserxApi = {
  fetchAllUsers,
  createUser,
  updateUser,
  deleteUser,
  getCurrentUser,
  isAuthenticated,
  getMe,
  updateMe,
};
