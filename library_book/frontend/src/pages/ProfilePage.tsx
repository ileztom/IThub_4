import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { userApi } from '../api/userApi';

const ProfilePage: React.FC = () => {
  const { user, logout } = useAuth();
  const [editing, setEditing] = useState(false);
  const [form, setForm] = useState({
    fullName: user?.fullName || '',
    phone: user?.phone || '',
    address: user?.address || '',
  });
  const [message, setMessage] = useState('');

  const handleSave = async () => {
    if (!user) return;
    try {
      await userApi.update(user.id, form);
      setMessage('Профиль обновлён');
      setEditing(false);
    } catch {
      setMessage('Ошибка при сохранении');
    }
  };

  if (!user) return null;

  const roleLabels: Record<string, string> = {
    USER: 'Читатель',
    LIBRARIAN: 'Библиотекарь',
    ADMIN: 'Администратор',
  };

  return (
    <div style={{ maxWidth: 600, margin: '0 auto' }}>
      <h1 className="page-title" style={{ marginBottom: 24 }}>Мой профиль</h1>
      <div className="card">
        <div style={{ display: 'flex', alignItems: 'center', gap: 20, marginBottom: 24 }}>
          <div style={{ width: 72, height: 72, borderRadius: '50%', background: 'linear-gradient(135deg, #1976d2, #42a5f5)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff', fontSize: '1.8rem', fontWeight: 700 }}>
            {user.fullName[0]?.toUpperCase() || user.username[0]?.toUpperCase()}
          </div>
          <div>
            <div style={{ fontSize: '1.3rem', fontWeight: 700 }}>{user.fullName}</div>
            <div style={{ color: '#666' }}>@{user.username}</div>
            <span className="badge badge-active">{roleLabels[user.role] || user.role}</span>
          </div>
        </div>

        {message && <div className="alert alert-success">{message}</div>}

        {editing ? (
          <>
            {[
              { key: 'fullName' as const, label: 'ФИО' },
              { key: 'phone' as const, label: 'Телефон' },
              { key: 'address' as const, label: 'Адрес' },
            ].map(({ key, label }) => (
              <div className="form-group" key={key}>
                <label>{label}</label>
                <input
                  className="form-control"
                  value={form[key]}
                  onChange={(e) => setForm((f) => ({ ...f, [key]: e.target.value }))}
                />
              </div>
            ))}
            <div style={{ display: 'flex', gap: 8 }}>
              <button className="btn btn-primary" onClick={handleSave}>Сохранить</button>
              <button className="btn btn-secondary" onClick={() => setEditing(false)}>Отмена</button>
            </div>
          </>
        ) : (
          <>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <tbody>
                {[
                  ['Email', user.email],
                  ['Телефон', user.phone || '—'],
                  ['Адрес', user.address || '—'],
                  ['Дата регистрации', new Date(user.createdAt).toLocaleDateString('ru-RU')],
                ].map(([label, value]) => (
                  <tr key={label}>
                    <td style={{ padding: '8px 0', color: '#666', width: '35%' }}>{label}</td>
                    <td style={{ padding: '8px 0', fontWeight: 500 }}>{value}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div style={{ display: 'flex', gap: 8, marginTop: 20 }}>
              <button className="btn btn-primary" onClick={() => setEditing(true)}>Редактировать</button>
              <button className="btn btn-secondary" onClick={logout}>Выйти</button>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default ProfilePage;
