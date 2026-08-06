import AIReviewStatusPill from './AIReviewStatusPill';

export default function AIReviewPanel({ status, content }) {
  return (
    <section className="rounded-xl border border-border bg-surface p-5">
      <div className="flex items-center justify-between">
        <h2 className="font-display text-base font-medium text-text">AI 리뷰</h2>
        <AIReviewStatusPill status={status} />
      </div>

      <div className="mt-4">
        {status === 'COMPLETED' && content ? (
          <p className="whitespace-pre-wrap text-sm leading-relaxed text-text-muted">{content}</p>
        ) : status === 'FAILED' ? (
          <p className="text-sm text-text-muted">
            AI 리뷰 생성에 실패했습니다. 잠시 후 다시 시도해주세요.
          </p>
        ) : (
          <p className="text-sm text-text-muted">
            AI가 코드를 검토하고 있습니다. 완료되면 이 영역에 리뷰가 표시됩니다.
          </p>
        )}
      </div>
    </section>
  );
}
