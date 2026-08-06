import { useState } from 'react';
import Textarea from '../common/Textarea';
import Button from '../common/Button';

export default function CommentForm({ onSubmit, onCancel, loading, placeholder, submitLabel = '등록' }) {
  const [content, setContent] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!content.trim()) {
      setError('내용을 입력해주세요.');
      return;
    }
    if (content.length > 1000) {
      setError('댓글은 1000자 이하로 입력해주세요.');
      return;
    }
    setError('');
    await onSubmit(content.trim());
    setContent('');
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-2">
      <Textarea
        rows={2}
        value={content}
        onChange={(e) => setContent(e.target.value)}
        placeholder={placeholder ?? '댓글을 입력해주세요'}
        error={error}
      />
      <div className="flex justify-end gap-2">
        {onCancel && (
          <Button type="button" variant="ghost" size="sm" onClick={onCancel}>
            취소
          </Button>
        )}
        <Button type="submit" size="sm" loading={loading}>
          {submitLabel}
        </Button>
      </div>
    </form>
  );
}
