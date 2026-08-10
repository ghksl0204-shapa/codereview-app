import { useState } from 'react';
import RatingStars from '../common/RatingStars';
import Textarea from '../common/Textarea';
import Button from '../common/Button';

const CRITERIA = [
  { key: 'kindnessScore', label: '친절도' },
  { key: 'accuracyScore', label: '정확도' },
  { key: 'detailScore', label: '상세도' },
];

export default function RatingForm({ initialValues, submitLabel = '평점 등록', onSubmit, onCancel, loading }) {
  const [scores, setScores] = useState({
    kindnessScore: initialValues?.kindnessScore ?? 0,
    accuracyScore: initialValues?.accuracyScore ?? 0,
    detailScore: initialValues?.detailScore ?? 0,
  });
  const [commentText, setCommentText] = useState(initialValues?.commentText ?? '');
  const [error, setError] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (Object.values(scores).some((v) => v === 0)) {
      setError('세 항목 모두 점수를 선택해주세요.');
      return;
    }
    setError('');
    onSubmit({ ...scores, commentText: commentText.trim() || undefined });
  };

  return (
    <form
      onSubmit={handleSubmit}
      className="mt-3 flex flex-col gap-3 rounded-lg border border-border bg-bg p-4"
    >
      {CRITERIA.map(({ key, label }) => (
        <div key={key} className="flex items-center justify-between">
          <span className="text-sm text-text-muted">{label}</span>
          <RatingStars
            value={scores[key]}
            onChange={(n) => setScores((prev) => ({ ...prev, [key]: n }))}
          />
        </div>
      ))}

      <Textarea
        rows={2}
        placeholder="평가 코멘트 (선택)"
        value={commentText}
        onChange={(e) => setCommentText(e.target.value)}
      />

      {error && <p className="text-xs text-red-300">{error}</p>}

      <div className="flex justify-end gap-2">
        <Button type="button" variant="ghost" size="sm" onClick={onCancel}>
          취소
        </Button>
        <Button type="submit" size="sm" loading={loading}>
          {submitLabel}
        </Button>
      </div>
    </form>
  );
}
