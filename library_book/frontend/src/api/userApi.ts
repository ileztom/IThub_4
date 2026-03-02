import client from './client';
import { AuthResponse, User } from '../types';

export const authApi = {
  register: (data: {
    username: string;
    email: string;
    password: string;
    fullName: string;
    phone?: string;
    address?: string;
  }) => client.post<AuthResponse>('/auth/register', data).then((r) => r.data),

  login: (usernameOrEmail: string, password: string) =>
    client.post<AuthResponse>('/auth/login', { usernameOrEmail, password }).then((r) => r.data),
};

export const userApi = {
  getById: (id: string) => client.get<User>(`/users/${id}`).then((r) => r.data),
  getAll: () => client.get<User[]>('/users').then((r) => r.data),
  update: (id: string, data: Partial<User>) =>
    client.put<User>(`/users/${id}`, data).then((r) => r.data),
  deactivate: (id: string) => client.delete(`/users/${id}`),
};
