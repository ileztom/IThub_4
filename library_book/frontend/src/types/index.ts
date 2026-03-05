export interface User {
  id: string;
  username: string;
  email: string;
  fullName: string;
  phone?: string;
  address?: string;
  role: 'USER' | 'LIBRARIAN' | 'ADMIN';
  active: boolean;
  createdAt: string;
}

export interface AuthState {
  token: string | null;
  user: User | null;
  isAuthenticated: boolean;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  userId: string;
  username: string;
  email: string;
  role: string;
  expiresIn: number;
}

export interface Book {
  id: string;
  isbn: string;
  title: string;
  author: string;
  publisher?: string;
  year: number;
  genre?: string;
  description?: string;
  coverUrl?: string;
  totalCopies: number;
  availableCopies: number;
  active: boolean;
  createdAt: string;
}

export interface BookRequest {
  isbn: string;
  title: string;
  author: string;
  publisher?: string;
  year: number;
  genre?: string;
  description?: string;
  coverUrl?: string;
  totalCopies: number;
}

export interface Loan {
  id: string;
  userId: string;
  bookId: string;
  bookIsbn: string;
  bookTitle: string;
  userFullName: string;
  loanDate: string;
  dueDate: string;
  returnDate?: string;
  status: 'ACTIVE' | 'RETURNED' | 'OVERDUE';
  notes?: string;
}

export interface LoanRequest {
  bookId: string;
  userId: string;
  loanDays?: number;
  notes?: string;
}

export interface Notification {
  id: string;
  userId: string;
  userEmail: string;
  title: string;
  message: string;
  type: 'LOAN_CREATED' | 'LOAN_RETURNED' | 'LOAN_OVERDUE' | 'REMINDER';
  read: boolean;
  createdAt: string;
}

export interface ApiError {
  message: string;
  timestamp?: string;
}
