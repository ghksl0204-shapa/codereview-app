const STAR_FULL = '★';
const STAR_EMPTY = '☆';

export default function RatingStars({ value, onChange, readOnly = false, size = 'text-base' }) {
  const stars = [1, 2, 3, 4, 5];

  return (
    <span className={`inline-flex gap-0.5 ${size}`}>
      {stars.map((n) => (
        <button
          key={n}
          type="button"
          disabled={readOnly}
          onClick={() => onChange?.(n)}
          className={`leading-none transition-colors ${
            readOnly ? 'cursor-default' : 'cursor-pointer hover:scale-110'
          } ${n <= value ? 'text-amber' : 'text-text-faint'}`}
          aria-label={`${n}점`}
        >
          {n <= value ? STAR_FULL : STAR_EMPTY}
        </button>
      ))}
    </span>
  );
}
