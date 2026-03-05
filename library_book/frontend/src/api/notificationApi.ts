import client from './client';
import { Notification } from '../types';

export const notificationApi = {
  getByUser: (userId: string) =>
    client.get<Notification[]>(`/notifications/user/${userId}`).then((r) => r.data),
  getUnread: (userId: string) =>
    client.get<Notification[]>(`/notifications/user/${userId}/unread`).then((r) => r.data),
  countUnread: (userId: string) =>
    client.get<{ count: number }>(`/notifications/user/${userId}/count`).then((r) => r.data.count),
  markAsRead: (id: string) =>
    client.put<Notification>(`/notifications/${id}/read`).then((r) => r.data),
  markAllAsRead: (userId: string) =>
    client.put(`/notifications/user/${userId}/read-all`),
};
