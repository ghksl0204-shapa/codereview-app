export default function Textarea({ label, error, hint, className = '', id, ...rest }) {
  return (
    <div className="flex flex-col gap-1.5">
      {label && (
        <label htmlFor={id} className="text-sm font-medium text-text-muted">
          {label}
        </label>
      )}
      <textarea
        id={id}
        className={`w-full resize-y rounded-lg border bg-surface px-3.5 py-2.5 text-sm text-text placeholder:text-text-faint outline-none transition-colors focus:border-primary ${
          error ? 'border-danger/60' : 'border-border'
        } ${className}`}
        {...rest}
      />
      {error ? (
        <p className="text-xs text-red-300">{error}</p>
      ) : hint ? (
        <p className="text-xs text-text-faint">{hint}</p>
      ) : null}
    </div>
  );
}
