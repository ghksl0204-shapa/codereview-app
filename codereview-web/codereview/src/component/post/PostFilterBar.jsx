import { useState } from 'react';
import { CATEGORIES, LANGUAGES } from '../../constants/postOptions';
import Button from '../common/Button';

export default function PostFilterBar({ filters, onApply }) {
  const [keyword, setKeyword] = useState(filters.keyword ?? '');

  const handleSelectChange = (key) => (e) => {
    onApply({ ...filters, [key]: e.target.value || undefined });
  };

  const handleKeywordSubmit = (e) => {
    e.preventDefault();
    onApply({ ...filters, keyword: keyword.trim() || undefined });
  };

  const hasActiveFilter = filters.category || filters.language || filters.keyword;

  const handleReset = () => {
    setKeyword('');
    onApply({});
  };

  return (
    <div className="flex flex-col gap-3 rounded-xl border border-border bg-surface p-4 sm:flex-row sm:items-center">
      <form onSubmit={handleKeywordSubmit} className="flex flex-1 items-center gap-2">
        <input
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          placeholder="제목 또는 요약 검색"
          className="w-full rounded-lg border border-border bg-bg px-3.5 py-2 text-sm text-text placeholder:text-text-faint outline-none focus:border-primary"
        />
        <Button type="submit" variant="subtle" size="md">
          검색
        </Button>
      </form>

      <div className="flex flex-wrap items-center gap-2">
        <select
          value={filters.category ?? ''}
          onChange={handleSelectChange('category')}
          className="rounded-lg border border-border bg-bg px-3 py-2 text-sm text-text outline-none focus:border-primary"
        >
          <option value="">전체 카테고리</option>
          {CATEGORIES.map((c) => (
            <option key={c} value={c}>
              {c}
            </option>
          ))}
        </select>

        <select
          value={filters.language ?? ''}
          onChange={handleSelectChange('language')}
          className="rounded-lg border border-border bg-bg px-3 py-2 text-sm text-text outline-none focus:border-primary"
        >
          <option value="">전체 언어</option>
          {LANGUAGES.map((l) => (
            <option key={l} value={l}>
              {l}
            </option>
          ))}
        </select>

        {hasActiveFilter && (
          <Button variant="ghost" size="md" onClick={handleReset}>
            초기화
          </Button>
        )}
      </div>
    </div>
  );
}
