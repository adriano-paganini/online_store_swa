/**
 * This code is part of the skeleton project provided for students of the course "Software
 * Architecture" offered by Innsbruck University.
 */
import globalAxios from 'axios';

export const BEARER_TOKEN_LOCAL_STORAGE_KEY = 'bearerToken';

const API_HOST = window.location.hostname;
const API_PORT = 8080;
export const API_BASE_URL = `http://${API_HOST}:${API_PORT}`;

globalAxios.defaults.baseURL = API_BASE_URL;

// Add a request interceptor to add the bearer token to all requests if available
globalAxios.interceptors.request.use(
  (request) => {
    const accessToken = localStorage.getItem(BEARER_TOKEN_LOCAL_STORAGE_KEY);
    if (accessToken) {
      request.headers['Authorization'] = `Bearer ${accessToken}`;
    }
    return request;
  },
  (error) => {
    return Promise.reject(error);
  }
);
