export default function Pagination({ offset, limit, totalCount, onChangeOffset }) {
  const currentPage = Math.floor(offset / limit) + 1;
  const totalPages = Math.max(1, Math.ceil(totalCount / limit));

  if (totalPages <= 1) return null;

  const pages = Array.from({ length: totalPages }, (_, i) => i + 1);

  return (
    <nav className="flex items-center justify-center gap-1.5 py-6" aria-label="페이지네이션">
      <button
        type="button"
        onClick={() => onChangeOffset(Math.max(0, offset - limit))}
        disabled={currentPage === 1}
        className="rounded-md px-2.5 py-1.5 text-sm text-text-muted hover:text-text disabled:opacity-30"
      >
        이전
      </button>

      {pages.map((page) => (
        <button
          key={page}
          type="button"
          onClick={() => onChangeOffset((page - 1) * limit)}
          className={`h-8 w-8 rounded-md font-mono text-sm transition-colors ${
            page === currentPage
              ? 'bg-primary text-[#0b0e14]'
              : 'text-text-muted hover:bg-surface-2 hover:text-text'
          }`}
        >
          {page}
        </button>
      ))}

      <button
        type="button"
        onClick={() => onChangeOffset(offset + limit)}
        disabled={currentPage === totalPages}
        className="rounded-md px-2.5 py-1.5 text-sm text-text-muted hover:text-text disabled:opacity-30"
      >
        다음
      </button>
    </nav>
  );
}
