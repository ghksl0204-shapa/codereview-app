import AIReviewStatusPill from './AIReviewStatusPill';
import Button from '../common/Button';

export default function AIReviewPanel({
  status,
  content,
  previousContent,
  isOwner,
  regenerating,
  onRegenerate,
}) {
  const isPending = status === 'PENDING';
  const displayContent = status === 'COMPLETED' ? content : previousContent;

  return (
    <section className="rounded-xl border border-border bg-surface p-5">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <h2 className="font-display text-base font-medium text-text">AI 리뷰</h2>
          <AIReviewStatusPill status={status} />
        </div>
        {isOwner && (
          <Button
            variant="subtle"
            size="sm"
            loading={regenerating}
            disabled={isPending}
            onClick={onRegenerate}
          >
            재검토
          </Button>
        )}
      </div>

      <div className="mt-4">
        {status === 'COMPLETED' && content ? (
          <p className="whitespace-pre-wrap break-words text-sm leading-relaxed text-text-muted">
            {content}
          </p>
        ) : isPending && displayContent ? (
          <>
            <p className="text-sm text-amber-300">
              AI 리뷰를 다시 생성하는 중입니다. 완료되면 아래 내용이 최신 리뷰로 교체됩니다.
            </p>
            <p className="mt-3 whitespace-pre-wrap break-words text-sm leading-relaxed text-text-muted opacity-60">
              {displayContent}
            </p>
          </>
        ) : status === 'FAILED' && displayContent ? (
          <>
            <p className="text-sm text-red-300">
              AI 리뷰 재생성에 실패했습니다. 이전 리뷰는 유지됩니다. 다시 시도해주세요.
            </p>
            <p className="mt-3 whitespace-pre-wrap break-words text-sm leading-relaxed text-text-muted">
              {displayContent}
            </p>
          </>
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
