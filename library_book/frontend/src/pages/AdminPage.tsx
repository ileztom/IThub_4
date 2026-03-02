import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { catalogApi } from '../api/catalogApi';
import { loanApi } from '../api/loanApi';
import { userApi } from '../api/userApi';
import { Book, Loan, User, BookRequest } from '../types';

const AdminPage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState<'books' | 'loans' | 'users'>('books');
  const [books, setBooks] = useState<Book[]>([]);
  const [loans, setLoans] = useState<Loan[]>([]);
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [showAddBook, setShowAddBook] = useState(false);
  const [bookForm, setBookForm] = useState<BookRequest>({
    isbn: '', title: '', author: '', publisher: '', year: 2024,
    genre: '', description: '', coverUrl: '', totalCopies: 1,
  });
  const [formError, setFormError] = useState('');

  useEffect(() => {
    if (!user || (user.role !== 'ADMIN' && user.role !== 'LIBRARIAN')) {
      navigate('/books');
    }
  }, [user, navigate]);

  useEffect(() => {
    setLoading(true);
    if (activeTab === 'books') {
      catalogApi.getAll().then(setBooks).finally(() => setLoading(false));
    } else if (activeTab === 'loans') {
      loanApi.getActive().then(setLoans).finally(() => setLoading(false));
    } else {
      userApi.getAll().then(setUsers).finally(() => setLoading(false));
    }
  }, [activeTab]);

  const handleAddBook = async (e: React.FormEvent) => {
    e.preventDefault();
    setFormError('');
    try {
      const created = await catalogApi.create(bookForm);
      setBooks((b) => [created, ...b]);
      setShowAddBook(false);
      setBookForm({ isbn: '', title: '', author: '', publisher: '', year: 2024, genre: '', description: '', coverUrl: '', totalCopies: 1 });
    } catch (err: any) {
      setFormError(err.response?.data?.message || 'Ошибка при добавлении книги');
    }
  };

  const handleDeleteBook = async (id: string) => {
    if (!window.confirm('Удалить книгу из каталога?')) return;
    await catalogApi.delete(id);
    setBooks((b) => b.filter((x) => x.id !== id));
  };

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Управление библиотекой</h1>
      </div>
      <div className="tabs">
        {([['books', 'Каталог'], ['loans', 'Активные выдачи'], ['users', 'Читатели']] as const).map(([val, label]) => (
          <button key={val} className={`tab${activeTab === val ? ' active' : ''}`} onClick={() => setActiveTab(val)}>
            {label}
          </button>
        ))}
      </div>

      {/* ── Books tab ─────────────────────────────── */}
      {activeTab === 'books' && (
        <div>
          <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: 16 }}>
            <button className="btn btn-primary" onClick={() => setShowAddBook(!showAddBook)}>
              {showAddBook ? '✕ Отмена' : '+ Добавить книгу'}
            </button>
          </div>
          {showAddBook && (
            <div className="card" style={{ marginBottom: 24 }}>
              <h3 style={{ marginBottom: 16 }}>Новая книга</h3>
              {formError && <div className="alert alert-error">{formError}</div>}
              <form onSubmit={handleAddBook}>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                  {[
                    { key: 'isbn', label: 'ISBN *', type: 'text' },
                    { key: 'title', label: 'Название *', type: 'text' },
                    { key: 'author', label: 'Автор *', type: 'text' },
                    { key: 'publisher', label: 'Издатель', type: 'text' },
                    { key: 'year', label: 'Год *', type: 'number' },
                    { key: 'genre', label: 'Жанр', type: 'text' },
                    { key: 'totalCopies', label: 'Кол-во копий *', type: 'number' },
                    { key: 'coverUrl', label: 'URL обложки', type: 'url' },
                  ].map(({ key, label, type }) => (
                    <div className="form-group" key={key}>
                      <label>{label}</label>
                      <input
                        className="form-control"
                        type={type}
                        value={(bookForm as any)[key]}
                        onChange={(e) => setBookForm((f) => ({ ...f, [key]: type === 'number' ? +e.target.value : e.target.value }))}
                        required={['isbn', 'title', 'author', 'year', 'totalCopies'].includes(key)}
                      />
                    </div>
                  ))}
                </div>
                <div className="form-group">
                  <label>Описание</label>
                  <textarea
                    className="form-control"
                    rows={3}
                    value={bookForm.description}
                    onChange={(e) => setBookForm((f) => ({ ...f, description: e.target.value }))}
                  />
                </div>
                <button className="btn btn-primary" type="submit">Добавить</button>
              </form>
            </div>
          )}
          {loading ? <div className="loading">Загрузка...</div> : (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr><th>Название</th><th>Автор</th><th>ISBN</th><th>Доступно</th><th>Действия</th></tr>
                </thead>
                <tbody>
                  {books.map((b) => (
                    <tr key={b.id}>
                      <td>{b.title}</td>
                      <td>{b.author}</td>
                      <td>{b.isbn}</td>
                      <td><span className={b.availableCopies > 0 ? 'copies-available' : 'copies-none'}>{b.availableCopies}/{b.totalCopies}</span></td>
                      <td><button className="btn btn-danger btn-sm" onClick={() => handleDeleteBook(b.id)}>Удалить</button></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* ── Loans tab ─────────────────────────────── */}
      {activeTab === 'loans' && (
        <div>
          {loading ? <div className="loading">Загрузка...</div> : (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr><th>Книга</th><th>Читатель</th><th>Дата выдачи</th><th>Срок возврата</th><th>Статус</th></tr>
                </thead>
                <tbody>
                  {loans.map((l) => {
                    const isOverdue = new Date(l.dueDate) < new Date() && l.status === 'ACTIVE';
                    return (
                      <tr key={l.id}>
                        <td>{l.bookTitle}</td>
                        <td>{l.userFullName}</td>
                        <td>{new Date(l.loanDate).toLocaleDateString('ru-RU')}</td>
                        <td style={{ color: isOverdue ? 'var(--danger)' : 'inherit' }}>
                          {new Date(l.dueDate).toLocaleDateString('ru-RU')}{isOverdue ? ' ⚠️' : ''}
                        </td>
                        <td><span className={`badge badge-${l.status.toLowerCase()}`}>{l.status}</span></td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* ── Users tab ─────────────────────────────── */}
      {activeTab === 'users' && (
        <div>
          {loading ? <div className="loading">Загрузка...</div> : (
            <div className="table-wrapper">
              <table>
                <thead>
                  <tr><th>ФИО</th><th>Логин</th><th>Email</th><th>Роль</th><th>Статус</th></tr>
                </thead>
                <tbody>
                  {users.map((u) => (
                    <tr key={u.id}>
                      <td>{u.fullName}</td>
                      <td>{u.username}</td>
                      <td>{u.email}</td>
                      <td>{u.role}</td>
                      <td><span className={`badge ${u.active ? 'badge-active' : 'badge-returned'}`}>{u.active ? 'Активен' : 'Заблокирован'}</span></td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default AdminPage;
