import { createContext, useCallback, useContext, useRef, useState } from 'react';

const ToastContext = createContext(null);

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);
  const idRef = useRef(0);

  const showToast = useCallback((message, type = 'info') => {
    const id = ++idRef.current;
    setToasts((prev) => [...prev, { id, message, type }]);
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 3200);
  }, []);

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      <div className="fixed bottom-5 right-5 z-50 flex flex-col gap-2">
        {toasts.map((t) => (
          <div
            key={t.id}
            className={
              'rounded-lg border px-4 py-3 text-sm shadow-lg backdrop-blur-sm animate-in ' +
              (t.type === 'error'
                ? 'border-danger/40 bg-danger-soft text-red-200'
                : t.type === 'success'
                  ? 'border-mint/40 bg-mint-soft text-emerald-200'
                  : 'border-border bg-surface-2 text-text')
            }
          >
            {t.message}
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

// eslint-disable-next-line react-refresh/only-export-components -- Context Provider와 전용 hook/유틸을 함께 배치하는 표준 패턴
export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error('useToast는 ToastProvider 내부에서 사용해야 합니다.');
  return ctx;
}

/** axios 에러에서 서버 메시지를 안전하게 추출 */
// eslint-disable-next-line react-refresh/only-export-components
export function extractErrorMessage(error, fallback = '요청 처리 중 오류가 발생했습니다.') {
  return error?.response?.data?.message || fallback;
}
