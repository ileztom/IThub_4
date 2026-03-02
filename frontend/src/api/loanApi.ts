import client from './client';
import { Loan, LoanRequest } from '../types';

export const loanApi = {
  create: (data: LoanRequest) => client.post<Loan>('/loans', data).then((r) => r.data),
  returnBook: (loanId: string) => client.put<Loan>(`/loans/${loanId}/return`).then((r) => r.data),
  getById: (id: string) => client.get<Loan>(`/loans/${id}`).then((r) => r.data),
  getByUser: (userId: string) => client.get<Loan[]>(`/loans/user/${userId}`).then((r) => r.data),
  getActive: () => client.get<Loan[]>('/loans/active').then((r) => r.data),
  getOverdue: () => client.get<Loan[]>('/loans/overdue').then((r) => r.data),
};
