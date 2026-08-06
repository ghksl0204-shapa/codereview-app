import { useState } from 'react';
import { ratingApi } from '../../api/ratingApi';
import { extractErrorMessage, useToast } from '../../context/ToastContext';
import { useAuth } from '../../context/AuthContext';
import RatingStars from '../common/RatingStars';
import RatingForm from './RatingForm';
import Button from '../common/Button';
import Spinner from '../common/Spinner';
import { formatDate } from '../../utils/formatDate';

export default function RatingPanel({ commentId }) {
  const { user } = useAuth();
  const { showToast } = useToast();

  const [open, setOpen] = useState(false);
  const [loaded, setLoaded] = useState(false);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [showForm, setShowForm] = useState(false);
  const [summary, setSummary] = useState(null);

  const fetchRatings = async () => {
    setLoading(true);
    try {
      const { data } = await ratingApi.getList(commentId);
      setSummary(data.data);
      setLoaded(true);
    } catch (error) {
      showToast(extractErrorMessage(error, '평점을 불러오지 못했습니다.'), 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleToggle = () => {
    const next = !open;
    setOpen(next);
    if (next && !loaded) fetchRatings();
  };

  const handleSubmit = async (payload) => {
    setSubmitting(true);
    try {
      await ratingApi.create(commentId, payload);
      showToast('평점이 등록되었습니다.', 'success');
      setShowForm(false);
      await fetchRatings();
    } catch (error) {
      showToast(extractErrorMessage(error, '평점 등록에 실패했습니다.'), 'error');
    } finally {
      setSubmitting(false);
    }
  };

  const alreadyRated = summary?.items?.some((r) => r.memberId === user?.id);

  return (
    <div className="mt-2">
      <button
        type="button"
        onClick={handleToggle}
        className="text-xs font-medium text-text-muted hover:text-primary"
      >
        {open ? '평점 숨기기' : '평점 보기'}
        {summary && summary.totalCount > 0 && ` (${summary.totalCount})`}
      </button>

      {open && (
        <div className="mt-2 rounded-lg border border-border-soft bg-surface-2 p-3">
          {loading ? (
            <div className="flex justify-center py-3">
              <Spinner size={16} />
            </div>
          ) : summary && summary.totalCount > 0 ? (
            <>
              <div className="flex flex-wrap gap-x-5 gap-y-1 border-b border-border-soft pb-3 text-xs text-text-muted">
                <span>
                  친절도{' '}
                  <b className="font-mono text-text">{summary.averageKindnessScore}</b>
                </span>
                <span>
                  정확도{' '}
                  <b className="font-mono text-text">{summary.averageAccuracyScore}</b>
                </span>
                <span>
                  상세도{' '}
                  <b className="font-mono text-text">{summary.averageDetailScore}</b>
                </span>
              </div>
              <ul className="mt-3 flex flex-col gap-2.5">
                {summary.items.map((r) => (
                  <li key={r.id} className="text-xs">
                    <div className="flex items-center gap-2">
                      <span className="font-medium text-text">{r.memberNickname}</span>
                      <RatingStars
                        value={Math.round((r.kindnessScore + r.accuracyScore + r.detailScore) / 3)}
                        readOnly
                        size="text-xs"
                      />
                      <span className="text-text-faint">{formatDate(r.createdAt)}</span>
                    </div>
                    {r.commentText && (
                      <p className="mt-0.5 text-text-muted">{r.commentText}</p>
                    )}
                  </li>
                ))}
              </ul>
            </>
          ) : (
            <p className="text-xs text-text-faint">아직 등록된 평점이 없습니다.</p>
          )}

          {!alreadyRated && !showForm && (
            <Button
              variant="ghost"
              size="sm"
              className="mt-3"
              onClick={() => setShowForm(true)}
            >
              평점 남기기
            </Button>
          )}

          {showForm && (
            <RatingForm
              loading={submitting}
              onSubmit={handleSubmit}
              onCancel={() => setShowForm(false)}
            />
          )}
        </div>
      )}
    </div>
  );
}
