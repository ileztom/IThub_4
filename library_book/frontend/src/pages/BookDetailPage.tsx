import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { catalogApi } from '../api/catalogApi';
import { loanApi } from '../api/loanApi';
import { useAuth } from '../context/AuthContext';
import { Book } from '../types';

const BookDetailPage: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const { user, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const [book, setBook] = useState<Book | null>(null);
  const [loading, setLoading] = useState(true);
  const [borrowing, setBorrowing] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  useEffect(() => {
    if (!id) return;
    catalogApi.getById(id)
      .then(setBook)
      .catch(() => navigate('/books'))
      .finally(() => setLoading(false));
  }, [id, navigate]);

  const handleBorrow = async () => {
    if (!user || !book) return;
    setBorrowing(true);
    setMessage(null);
    try {
      await loanApi.create({ bookId: book.id, userId: user.id });
      setMessage({ type: 'success', text: 'Книга успешно взята! Срок возврата — 14 дней.' });
      setBook((b) => b ? { ...b, availableCopies: b.availableCopies - 1 } : b);
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Ошибка при выдаче книги' });
    } finally {
      setBorrowing(false);
    }
  };

  if (loading) return <div className="loading">Загрузка...</div>;
  if (!book) return null;

  return (
    <div>
      <button className="btn btn-secondary btn-sm" onClick={() => navigate(-1)} style={{ marginBottom: 20 }}>
        ← Назад
      </button>
      <div className="card" style={{ display: 'flex', gap: 32, flexWrap: 'wrap' }}>
        <div style={{ width: 200, flexShrink: 0 }}>
          <div className="book-cover" style={{ height: 280, borderRadius: 8 }}>
            {book.coverUrl ? (
              <img src={book.coverUrl} alt={book.title} style={{ width: '100%', height: '100%', objectFit: 'cover', borderRadius: 8 }} />
            ) : <span style={{ fontSize: '4rem' }}>📖</span>}
          </div>
        </div>
        <div style={{ flex: 1, minWidth: 240 }}>
          <h1 style={{ fontSize: '1.8rem', fontWeight: 700, marginBottom: 8 }}>{book.title}</h1>
          <p style={{ color: '#555', fontSize: '1.1rem', marginBottom: 16 }}>{book.author}</p>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '8px 24px', marginBottom: 20 }}>
            {book.isbn && <div><span style={{ color: '#888', fontSize: '0.85rem' }}>ISBN:</span> {book.isbn}</div>}
            {book.year && <div><span style={{ color: '#888', fontSize: '0.85rem' }}>Год:</span> {book.year}</div>}
            {book.publisher && <div><span style={{ color: '#888', fontSize: '0.85rem' }}>Издатель:</span> {book.publisher}</div>}
            {book.genre && <div><span style={{ color: '#888', fontSize: '0.85rem' }}>Жанр:</span> {book.genre}</div>}
            <div>
              <span style={{ color: '#888', fontSize: '0.85rem' }}>Доступно:</span>{' '}
              <span className={book.availableCopies > 0 ? 'copies-available' : 'copies-none'}>
                {book.availableCopies} / {book.totalCopies}
              </span>
            </div>
          </div>
          {book.description && (
            <p style={{ color: '#444', lineHeight: 1.6, marginBottom: 20 }}>{book.description}</p>
          )}
          {message && <div className={`alert alert-${message.type}`}>{message.text}</div>}
          {isAuthenticated ? (
            <button
              className="btn btn-primary"
              onClick={handleBorrow}
              disabled={book.availableCopies === 0 || borrowing}
            >
              {borrowing ? 'Оформление...' : book.availableCopies > 0 ? '📋 Взять книгу' : 'Нет в наличии'}
            </button>
          ) : (
            <button className="btn btn-secondary" onClick={() => navigate('/login')}>
              Войдите, чтобы взять книгу
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default BookDetailPage;
