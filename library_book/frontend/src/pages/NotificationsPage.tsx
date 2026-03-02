import React, { useEffect, useState } from 'react';
import { notificationApi } from '../api/notificationApi';
import { useAuth } from '../context/AuthContext';
import { Notification } from '../types';

const typeIcon: Record<Notification['type'], string> = {
  LOAN_CREATED: '📗',
  LOAN_RETURNED: '✅',
  LOAN_OVERDUE: '⚠️',
  REMINDER: '🔔',
};

const NotificationsPage: React.FC = () => {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState<Notification[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!user) return;
    notificationApi.getByUser(user.id)
      .then(setNotifications)
      .finally(() => setLoading(false));
  }, [user]);

  const handleMarkRead = async (n: Notification) => {
    if (n.read) return;
    const updated = await notificationApi.markAsRead(n.id);
    setNotifications((prev) => prev.map((x) => x.id === n.id ? updated : x));
  };

  const handleMarkAllRead = async () => {
    if (!user) return;
    await notificationApi.markAllAsRead(user.id);
    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
  };

  const unreadCount = notifications.filter((n) => !n.read).length;

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Уведомления {unreadCount > 0 && <span className="nav-badge">{unreadCount}</span>}</h1>
        {unreadCount > 0 && (
          <button className="btn btn-secondary btn-sm" onClick={handleMarkAllRead}>
            Прочитать все
          </button>
        )}
      </div>
      {loading ? (
        <div className="loading">Загрузка...</div>
      ) : notifications.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">🔔</div>
          <h3>Нет уведомлений</h3>
          <p>Уведомления появятся при выдаче или возврате книг</p>
        </div>
      ) : (
        <div className="notifications-list">
          {notifications.map((n) => (
            <div
              key={n.id}
              className={`notification-item${!n.read ? ' unread' : ''}${n.type === 'LOAN_OVERDUE' ? ' overdue' : ''}`}
              onClick={() => handleMarkRead(n)}
            >
              <div className="notif-icon">{typeIcon[n.type]}</div>
              <div style={{ flex: 1 }}>
                <div className="notif-title">{n.title}</div>
                <div className="notif-message">{n.message}</div>
                <div className="notif-time">
                  {new Date(n.createdAt).toLocaleString('ru-RU')}
                  {!n.read && <span style={{ marginLeft: 8, color: '#1976d2', fontWeight: 600 }}>● Новое</span>}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default NotificationsPage;
