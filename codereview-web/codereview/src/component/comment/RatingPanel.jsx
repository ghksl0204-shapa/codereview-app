import { useState } from 'react';
import { ratingApi } from '../../api/ratingApi';
import { extractErrorMessage, useToast } from '../../context/ToastContext';
import RatingForm from './RatingForm';
import Button from '../common/Button';

export default function RatingPanel({ commentId, rating, onChanged }) {
  const { showToast } = useToast();

  const [showForm, setShowForm] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const hasRatings = (rating?.ratingCount ?? 0) > 0;
  const myRating = rating?.myRating ?? null;

  const handleSubmit = async (payload) => {
    setSubmitting(true);
    try {
      if (myRating) {
        await ratingApi.update(commentId, payload);
        showToast('평점이 수정되었습니다.', 'success');
      } else {
        await ratingApi.create(commentId, payload);
        showToast('평점이 등록되었습니다.', 'success');
      }
      setShowForm(false);
      await onChanged?.();
    } catch (error) {
      showToast(extractErrorMessage(error, '평점 처리에 실패했습니다.'), 'error');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="mt-2 rounded-lg border border-border-soft bg-surface-2 p-3">
      {hasRatings ? (
        <div className="flex flex-wrap items-center gap-x-5 gap-y-1 text-xs text-text-muted">
          <span>
            친절도 <b className="font-mono text-text">{rating.averageKindnessScore}</b>
          </span>
          <span>
            정확도 <b className="font-mono text-text">{rating.averageAccuracyScore}</b>
          </span>
          <span>
            상세도 <b className="font-mono text-text">{rating.averageDetailScore}</b>
          </span>
          <span className="text-text-faint">({rating.ratingCount}명 평가)</span>
        </div>
      ) : (
        <p className="text-xs text-text-faint">아직 등록된 평점이 없습니다.</p>
      )}

      {!showForm && (
        <Button variant="ghost" size="sm" className="mt-2" onClick={() => setShowForm(true)}>
          {myRating ? '내 평점 수정' : '평점 남기기'}
        </Button>
      )}

      {showForm && (
        <RatingForm
          initialValues={myRating}
          submitLabel={myRating ? '평점 수정' : '평점 등록'}
          loading={submitting}
          onSubmit={handleSubmit}
          onCancel={() => setShowForm(false)}
        />
      )}
    </div>
  );
}
