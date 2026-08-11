import { Link } from 'react-router-dom';
import AIReviewStatusPill from './AIReviewStatusPill';
import { formatDate } from '../../utils/formatDate';

export default function PostCard({ post }) {
  return (
    <Link
      to={`/posts/${post.id}`}
      className="group flex min-w-0 flex-col gap-3 rounded-xl border border-border bg-surface p-5 transition-colors hover:border-primary/50"
    >
      <div className="flex items-center gap-2">
        <span className="rounded-md bg-primary-soft px-2 py-0.5 font-mono text-[11px] text-primary">
          {post.language}
        </span>
        <span className="rounded-md bg-surface-2 px-2 py-0.5 text-[11px] text-text-muted">
          {post.category}
        </span>
        <div className="ml-auto">
          <AIReviewStatusPill status={post.aiReviewStatus} />
        </div>
      </div>

      <h3 className="break-words font-display text-lg font-medium text-text group-hover:text-primary">
        {post.title}
      </h3>

      {post.summary && (
        <p className="line-clamp-2 text-sm leading-relaxed text-text-muted">{post.summary}</p>
      )}

      <div className="mt-1 flex items-center gap-2 text-xs text-text-faint">
        <span className="font-medium text-text-muted">{post.memberNickname}</span>
        <span>·</span>
        <span>{formatDate(post.createdAt)}</span>
      </div>
    </Link>
  );
}
