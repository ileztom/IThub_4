import React, { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { catalogApi } from '../api/catalogApi';
import { Book } from '../types';

const BookCard: React.FC<{ book: Book; onClick: () => void }> = ({ book, onClick }) => (
  <div className="book-card" onClick={onClick}>
    <div className="book-cover">
      {book.coverUrl ? (
        <img src={book.coverUrl} alt={book.title} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
      ) : (
        <span>📖</span>
      )}
    </div>
    <div className="book-info">
      <div className="book-title">{book.title}</div>
      <div className="book-author">{book.author}</div>
      {book.genre && <div className="book-genre">{book.genre}</div>}
      <div className="book-meta" style={{ marginTop: 12 }}>
        <span className={`book-copies ${book.availableCopies > 0 ? 'copies-available' : 'copies-none'}`}>
          {book.availableCopies > 0 ? `Доступно: ${book.availableCopies}` : 'Нет в наличии'}
        </span>
        <span style={{ color: '#999', fontSize: '0.8rem' }}>{book.year}</span>
      </div>
    </div>
  </div>
);

const BooksPage: React.FC = () => {
  const [books, setBooks] = useState<Book[]>([]);
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const loadBooks = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const data = query.trim()
        ? await catalogApi.search(query.trim())
        : await catalogApi.getAll();
      setBooks(data);
    } catch {
      setError('Не удалось загрузить каталог');
    } finally {
      setLoading(false);
    }
  }, [query]);

  useEffect(() => {
    const timer = setTimeout(loadBooks, 300);
    return () => clearTimeout(timer);
  }, [loadBooks]);

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Каталог книг</h1>
        <span style={{ color: '#666', fontSize: '0.9rem' }}>Найдено: {books.length}</span>
      </div>
      <div className="search-bar">
        <input
          className="search-input"
          type="text"
          placeholder="🔍 Поиск по названию, автору, ISBN..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
      </div>
      {error && <div className="alert alert-error">{error}</div>}
      {loading ? (
        <div className="loading">Загрузка каталога...</div>
      ) : books.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">📚</div>
          <h3>Книги не найдены</h3>
          <p>{query ? 'Попробуйте изменить запрос' : 'Каталог пуст'}</p>
        </div>
      ) : (
        <div className="card-grid">
          {books.map((book) => (
            <BookCard key={book.id} book={book} onClick={() => navigate(`/books/${book.id}`)} />
          ))}
        </div>
      )}
    </div>
  );
};

export default BooksPage;
