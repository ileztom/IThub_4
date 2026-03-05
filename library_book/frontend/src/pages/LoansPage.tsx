import React, { useEffect, useState } from 'react';
import { loanApi } from '../api/loanApi';
import { useAuth } from '../context/AuthContext';
import { Loan } from '../types';

const statusLabel: Record<Loan['status'], string> = {
  ACTIVE: 'Активна',
  RETURNED: 'Возвращена',
  OVERDUE: 'Просрочена',
};

const LoanCard: React.FC<{ loan: Loan; onReturn: (id: string) => void }> = ({ loan, onReturn }) => {
  const dueDate = new Date(loan.dueDate);
  const isOverdue = loan.status === 'OVERDUE' || (loan.status === 'ACTIVE' && dueDate < new Date());

  return (
    <div className={`loan-card ${loan.status.toLowerCase()}`}>
      <div style={{ flex: 1 }}>
        <div className="loan-title">{loan.bookTitle}</div>
        <div className="loan-meta">ISBN: {loan.bookIsbn}</div>
        <div className="loan-dates" style={{ marginTop: 8 }}>
          <span>Дата выдачи: {new Date(loan.loanDate).toLocaleDateString('ru-RU')}</span>
          <span className={isOverdue ? 'overdue-text' : ''}>
            Срок возврата: {dueDate.toLocaleDateString('ru-RU')}
            {isOverdue && loan.status !== 'RETURNED' ? ' ⚠️ Просрочено!' : ''}
          </span>
          {loan.returnDate && (
            <span>Возвращена: {new Date(loan.returnDate).toLocaleDateString('ru-RU')}</span>
          )}
        </div>
      </div>
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: 8 }}>
        <span className={`badge badge-${loan.status.toLowerCase()}`}>{statusLabel[loan.status]}</span>
        {loan.status === 'ACTIVE' && (
          <button className="btn btn-success btn-sm" onClick={() => onReturn(loan.id)}>
            Вернуть
          </button>
        )}
      </div>
    </div>
  );
};

const LoansPage: React.FC = () => {
  const { user } = useAuth();
  const [loans, setLoans] = useState<Loan[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'all' | 'active' | 'returned'>('all');
  const [error, setError] = useState('');

  useEffect(() => {
    if (!user) return;
    loanApi.getByUser(user.id)
      .then(setLoans)
      .catch(() => setError('Не удалось загрузить выдачи'))
      .finally(() => setLoading(false));
  }, [user]);

  const handleReturn = async (loanId: string) => {
    try {
      const updated = await loanApi.returnBook(loanId);
      setLoans((prev) => prev.map((l) => l.id === loanId ? updated : l));
    } catch (err: any) {
      alert(err.response?.data?.message || 'Ошибка при возврате книги');
    }
  };

  const filtered = loans.filter((l) =>
    activeTab === 'all' ? true :
    activeTab === 'active' ? ['ACTIVE', 'OVERDUE'].includes(l.status) :
    l.status === 'RETURNED'
  );

  return (
    <div>
      <div className="page-header">
        <h1 className="page-title">Мои книги</h1>
        <span style={{ color: '#666' }}>Всего: {loans.length}</span>
      </div>
      {error && <div className="alert alert-error">{error}</div>}
      <div className="tabs">
        {([['all', 'Все'], ['active', 'Активные'], ['returned', 'Возвращённые']] as const).map(([val, label]) => (
          <button key={val} className={`tab${activeTab === val ? ' active' : ''}`} onClick={() => setActiveTab(val)}>
            {label}
          </button>
        ))}
      </div>
      {loading ? (
        <div className="loading">Загрузка...</div>
      ) : filtered.length === 0 ? (
        <div className="empty-state">
          <div className="empty-state-icon">📋</div>
          <h3>Нет выдач</h3>
          <p>Возьмите книгу из каталога</p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {filtered.map((loan) => (
            <LoanCard key={loan.id} loan={loan} onReturn={handleReturn} />
          ))}
        </div>
      )}
    </div>
  );
};

export default LoansPage;
