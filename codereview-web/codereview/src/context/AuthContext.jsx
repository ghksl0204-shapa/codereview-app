import { createContext, useContext, useEffect, useState, useCallback } from 'react';
import { authApi } from '../api/authApi';
import { getAuthStorage, setAuthStorage, clearAuthStorage } from '../utils/authStorage';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null); // { id, nickname, role }
  const [isReady, setIsReady] = useState(false);

  useEffect(() => {
    const auth = getAuthStorage();
    if (auth?.accessToken) {
      // eslint-disable-next-line react-hooks/set-state-in-effect -- 마운트 시 localStorage에서 세션 1회 복원
      setUser({ id: auth.id, nickname: auth.nickname, role: auth.role });
    }
    setIsReady(true);
  }, []);

  const login = useCallback(async (id, password) => {
    const { data } = await authApi.login(id, password);
    const { accessToken, refreshToken, id: memberId, nickname, role } = data.data;
    setAuthStorage({ accessToken, refreshToken, id: memberId, nickname, role });
    setUser({ id: memberId, nickname, role });
  }, []);

  const logout = useCallback(async () => {
    const auth = getAuthStorage();
    try {
      if (auth?.refreshToken) {
        await authApi.logout(auth.refreshToken);
      }
    } catch {
      // 로그아웃은 서버 실패 여부와 무관하게 클라이언트 상태를 정리한다.
    } finally {
      clearAuthStorage();
      setUser(null);
    }
  }, []);

  const updateNickname = useCallback((nickname) => {
    const auth = getAuthStorage();
    if (auth) setAuthStorage({ ...auth, nickname });
    setUser((prev) => (prev ? { ...prev, nickname } : prev));
  }, []);

  return (
    <AuthContext.Provider
      value={{ user, isAuthenticated: !!user, isReady, login, logout, updateNickname }}
    >
      {children}
    </AuthContext.Provider>
  );
}

// eslint-disable-next-line react-refresh/only-export-components -- Context Provider와 전용 hook을 함께 배치하는 표준 패턴
export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth는 AuthProvider 내부에서 사용해야 합니다.');
  return ctx;
}
