import client from './client';
import { Book, BookRequest } from '../types';

export const catalogApi = {
  getAll: () => client.get<Book[]>('/books').then((r) => r.data),
  getById: (id: string) => client.get<Book>(`/books/${id}`).then((r) => r.data),
  search: (q: string) => client.get<Book[]>(`/books/search?q=${encodeURIComponent(q)}`).then((r) => r.data),
  getAvailable: () => client.get<Book[]>('/books/available').then((r) => r.data),
  getByGenre: (genre: string) => client.get<Book[]>(`/books/genre/${genre}`).then((r) => r.data),
  create: (data: BookRequest) => client.post<Book>('/books', data).then((r) => r.data),
  update: (id: string, data: BookRequest) => client.put<Book>(`/books/${id}`, data).then((r) => r.data),
  delete: (id: string) => client.delete(`/books/${id}`),
};
