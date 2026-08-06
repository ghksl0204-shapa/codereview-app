import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import Button from '../common/Button';

export default function Header() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <header className="sticky top-0 z-40 border-b border-border-soft bg-bg/90 backdrop-blur">
      <div className="mx-auto flex h-16 max-w-6xl items-center justify-between px-5">
        <div className="flex items-center gap-8">
          <NavLink to="/posts" className="flex items-center gap-2">
            <span className="flex h-7 w-7 items-center justify-center rounded-md bg-primary font-mono text-sm font-bold text-[#0b0e14]">
              {'</>'}
            </span>
            <span className="font-display text-lg font-semibold tracking-tight text-text">
              CodeReview
            </span>
          </NavLink>

          <nav className="hidden items-center gap-1 sm:flex">
            <NavLink
              to="/posts"
              className={({ isActive }) =>
                `rounded-md px-3 py-1.5 text-sm font-medium transition-colors ${
                  isActive ? 'bg-surface-2 text-text' : 'text-text-muted hover:text-text'
                }`
              }
            >
              게시글
            </NavLink>
            <NavLink
              to="/posts/new"
              className={({ isActive }) =>
                `rounded-md px-3 py-1.5 text-sm font-medium transition-colors ${
                  isActive ? 'bg-surface-2 text-text' : 'text-text-muted hover:text-text'
                }`
              }
            >
              코드 업로드
            </NavLink>
          </nav>
        </div>

        <div className="flex items-center gap-3">
          <NavLink
            to="/mypage"
            className="hidden items-center gap-2 text-sm text-text-muted hover:text-text sm:flex"
          >
            <span className="h-6 w-6 rounded-full bg-primary-soft text-center font-mono text-[11px] leading-6 text-primary">
              {user?.nickname?.[0]?.toUpperCase() ?? '?'}
            </span>
            {user?.nickname}
          </NavLink>
          <Button variant="ghost" size="sm" onClick={handleLogout}>
            로그아웃
          </Button>
        </div>
      </div>
    </header>
  );
}
