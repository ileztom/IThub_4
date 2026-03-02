import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { AuthResponse, User } from '../types';
import { authApi, userApi } from '../api/userApi';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (usernameOrEmail: string, password: string) => Promise<void>;
  register: (data: {
    username: string;
    email: string;
    password: string;
    fullName: string;
    phone?: string;
    address?: string;
  }) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | null>(null);

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider');
  return ctx;
};

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const storedUser = localStorage.getItem('user');
    if (storedUser && token) {
      try {
        setUser(JSON.parse(storedUser));
      } catch {
        logout();
      }
    }
    setIsLoading(false);
  }, []);

  const handleAuthResponse = async (resp: AuthResponse) => {
    setToken(resp.token);
    localStorage.setItem('token', resp.token);
    const fullUser = await userApi.getById(resp.userId);
    setUser(fullUser);
    localStorage.setItem('user', JSON.stringify(fullUser));
  };

  const login = async (usernameOrEmail: string, password: string) => {
    const resp = await authApi.login(usernameOrEmail, password);
    await handleAuthResponse(resp);
  };

  const register = async (data: Parameters<typeof authApi.register>[0]) => {
    const resp = await authApi.register(data);
    await handleAuthResponse(resp);
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  return (
    <AuthContext.Provider
      value={{ user, token, isAuthenticated: !!token && !!user, isLoading, login, register, logout }}
    >
      {children}
    </AuthContext.Provider>
  );
};
