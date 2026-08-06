const STATUS_MAP = {
  PENDING: { label: 'AI 리뷰 준비중', dot: 'bg-amber animate-pulse', text: 'text-amber-300' },
  COMPLETED: { label: 'AI 리뷰 완료', dot: 'bg-mint', text: 'text-emerald-300' },
  FAILED: { label: 'AI 리뷰 실패', dot: 'bg-danger', text: 'text-red-300' },
};

export default function AIReviewStatusPill({ status }) {
  const config = STATUS_MAP[status] ?? {
    label: 'AI 리뷰 없음',
    dot: 'bg-text-faint',
    text: 'text-text-faint',
  };

  return (
    <span
      className={`inline-flex items-center gap-1.5 rounded-full border border-border bg-surface-2 px-2.5 py-1 font-mono text-[11px] ${config.text}`}
    >
      <span className={`h-1.5 w-1.5 rounded-full ${config.dot}`} />
      {config.label}
    </span>
  );
}
