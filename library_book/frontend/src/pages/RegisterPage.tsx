import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const RegisterPage: React.FC = () => {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({
    username: '', email: '', password: '', fullName: '', phone: '', address: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const set = (field: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm((f) => ({ ...f, [field]: e.target.value }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      await register(form);
      navigate('/books');
    } catch (err: any) {
      const msg = err.response?.data?.message || err.response?.data?.username ||
        err.response?.data?.email || 'Ошибка регистрации';
      setError(typeof msg === 'object' ? JSON.stringify(msg) : msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="card">
        <h1 className="auth-title">Регистрация</h1>
        <p className="auth-subtitle">Создайте аккаунт читателя</p>
        {error && <div className="alert alert-error">{error}</div>}
        <form onSubmit={handleSubmit}>
          {[
            { field: 'username' as const, label: 'Логин *', type: 'text', placeholder: 'Придумайте логин' },
            { field: 'email' as const, label: 'Email *', type: 'email', placeholder: 'your@email.com' },
            { field: 'password' as const, label: 'Пароль *', type: 'password', placeholder: 'Минимум 6 символов' },
            { field: 'fullName' as const, label: 'ФИО *', type: 'text', placeholder: 'Иванов Иван Иванович' },
            { field: 'phone' as const, label: 'Телефон', type: 'tel', placeholder: '+7 (900) 000-00-00' },
            { field: 'address' as const, label: 'Адрес', type: 'text', placeholder: 'г. Москва, ул. Ленина, 1' },
          ].map(({ field, label, type, placeholder }) => (
            <div className="form-group" key={field}>
              <label>{label}</label>
              <input
                className="form-control"
                type={type}
                value={form[field]}
                onChange={set(field)}
                placeholder={placeholder}
                required={!['phone', 'address'].includes(field)}
              />
            </div>
          ))}
          <button className="btn btn-primary" style={{ width: '100%' }} type="submit" disabled={loading}>
            {loading ? 'Регистрация...' : 'Зарегистрироваться'}
          </button>
        </form>
        <div className="auth-link">
          Уже есть аккаунт? <Link to="/login">Войти</Link>
        </div>
      </div>
    </div>
  );
};

export default RegisterPage;
