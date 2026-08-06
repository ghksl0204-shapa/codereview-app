import { useState } from 'react';
import { CATEGORIES, LANGUAGES } from '../../constants/postOptions';
import Input from '../common/Input';
import Textarea from '../common/Textarea';
import Button from '../common/Button';
import CodeEditorField from './CodeEditorField';

const EMPTY = {
  title: '',
  summary: '',
  codeContent: '',
  language: LANGUAGES[0],
  category: CATEGORIES[0],
  reviewFocus: '',
};

function validate(form) {
  const errors = {};
  if (!form.title.trim()) errors.title = '제목은 필수입니다.';
  else if (form.title.length > 200) errors.title = '제목은 200자 이하로 입력해주세요.';

  if (form.summary.length > 500) errors.summary = '요약은 500자 이하로 입력해주세요.';

  if (!form.codeContent.trim()) errors.codeContent = '코드는 필수입니다.';
  else if (form.codeContent.length > 10000) {
    errors.codeContent = '코드는 10,000자를 초과할 수 없습니다.';
  }

  if (form.reviewFocus.length > 500) {
    errors.reviewFocus = '리뷰 포커스는 500자 이하로 입력해주세요.';
  }

  return errors;
}

export default function PostForm({ initialValues, submitLabel, loading, onSubmit }) {
  const [form, setForm] = useState({ ...EMPTY, ...initialValues });
  const [errors, setErrors] = useState({});

  const handleField = (key) => (e) => {
    setForm((prev) => ({ ...prev, [key]: e.target.value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const clientErrors = validate(form);
    setErrors(clientErrors);
    if (Object.keys(clientErrors).length > 0) return;
    onSubmit(form);
  };

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-5">
      <Input
        id="title"
        label="제목"
        value={form.title}
        onChange={handleField('title')}
        error={errors.title}
        placeholder="예: 배열 합계 계산 함수 리뷰 부탁드립니다"
        required
      />

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div className="flex flex-col gap-1.5">
          <label className="text-sm font-medium text-text-muted">언어</label>
          <select
            value={form.language}
            onChange={handleField('language')}
            className="rounded-lg border border-border bg-surface px-3.5 py-2.5 text-sm text-text outline-none focus:border-primary"
          >
            {LANGUAGES.map((l) => (
              <option key={l} value={l}>
                {l}
              </option>
            ))}
          </select>
        </div>
        <div className="flex flex-col gap-1.5">
          <label className="text-sm font-medium text-text-muted">카테고리</label>
          <select
            value={form.category}
            onChange={handleField('category')}
            className="rounded-lg border border-border bg-surface px-3.5 py-2.5 text-sm text-text outline-none focus:border-primary"
          >
            {CATEGORIES.map((c) => (
              <option key={c} value={c}>
                {c}
              </option>
            ))}
          </select>
        </div>
      </div>

      <Textarea
        id="summary"
        label="요약 (선택)"
        rows={2}
        value={form.summary}
        onChange={handleField('summary')}
        error={errors.summary}
        placeholder="코드에 대한 짧은 설명"
      />

      <Textarea
        id="reviewFocus"
        label="리뷰 포커스 (선택)"
        rows={2}
        value={form.reviewFocus}
        onChange={handleField('reviewFocus')}
        error={errors.reviewFocus}
        placeholder="특히 봐줬으면 하는 부분이 있다면 적어주세요 (예: 성능, 예외 처리)"
      />

      <CodeEditorField
        value={form.codeContent}
        onChange={(v) => setForm((prev) => ({ ...prev, codeContent: v }))}
        language={form.language}
        error={errors.codeContent}
      />

      <Button type="submit" size="lg" loading={loading} className="self-start px-8">
        {submitLabel}
      </Button>
    </form>
  );
}
