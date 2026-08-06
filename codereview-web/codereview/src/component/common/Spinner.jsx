export default function Spinner({ size = 20, className = '' }) {
  return (
    <div
      className={`animate-spin rounded-full border-2 border-border border-t-primary ${className}`}
      style={{ width: size, height: size }}
      role="status"
      aria-label="로딩 중"
    />
  );
}

export function PageSpinner({ label = '불러오는 중...' }) {
  return (
    <div className="flex flex-1 flex-col items-center justify-center gap-3 py-24 text-text-muted">
      <Spinner size={28} />
      <p className="text-sm font-mono">{label}</p>
    </div>
  );
}
