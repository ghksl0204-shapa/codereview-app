const VARIANTS = {
  primary: 'bg-primary text-[#0b0e14] hover:bg-primary-hover disabled:opacity-50',
  ghost:
    'bg-transparent text-text-muted border border-border hover:border-text-faint hover:text-text disabled:opacity-50',
  danger:
    'bg-transparent text-red-300 border border-danger/40 hover:bg-danger-soft disabled:opacity-50',
  subtle: 'bg-surface-2 text-text hover:bg-border disabled:opacity-50',
};

const SIZES = {
  sm: 'px-3 py-1.5 text-sm',
  md: 'px-4 py-2 text-sm',
  lg: 'px-5 py-2.5 text-base',
};

export default function Button({
  as: As = 'button',
  variant = 'primary',
  size = 'md',
  className = '',
  loading = false,
  children,
  disabled,
  ...rest
}) {
  return (
    <As
      className={`inline-flex items-center justify-center gap-2 rounded-lg font-medium transition-colors cursor-pointer disabled:cursor-not-allowed ${VARIANTS[variant]} ${SIZES[size]} ${className}`}
      disabled={disabled || loading}
      {...rest}
    >
      {loading && (
        <span className="h-3.5 w-3.5 animate-spin rounded-full border-2 border-current border-t-transparent" />
      )}
      {children}
    </As>
  );
}
