import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { PageSpinner } from '../common/Spinner';

export default function PublicOnlyRoute() {
  const { isAuthenticated, isReady } = useAuth();

  if (!isReady) return <PageSpinner label="세션 확인 중..." />;
  if (isAuthenticated) return <Navigate to="/posts" replace />;

  return <Outlet />;
}
