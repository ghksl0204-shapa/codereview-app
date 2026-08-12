import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { postApi } from '../../api/postApi';
import { aiReviewApi } from '../../api/aiReviewApi';
import { extractErrorMessage, useToast } from '../../context/ToastContext';
import { useAuth } from '../../context/AuthContext';
import { formatDate } from '../../utils/formatDate';
import { PageSpinner } from '../../component/common/Spinner';
import Button from '../../component/common/Button';
import ConfirmDialog from '../../component/common/ConfirmDialog';
import CodeViewer from '../../component/post/CodeViewer';
import AIReviewPanel from '../../component/post/AIReviewPanel';
import CommentList from '../../component/comment/CommentList';

export default function PostDetailPage() {
  const { postId } = useParams();
  const { user } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();

  const [post, setPost] = useState(null);
  const [loading, setLoading] = useState(true);
  const [confirmDelete, setConfirmDelete] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [regenerateSubmitting, setRegenerateSubmitting] = useState(false);
  const [lastKnownReview, setLastKnownReview] = useState(null);

  useEffect(() => {
    let ignore = false;
    async function fetchPost() {
      setLoading(true);
      try {
        const { data } = await postApi.getDetail(postId);
        if (!ignore) setPost(data.data);
      } catch (error) {
        if (!ignore) {
          showToast(extractErrorMessage(error, '게시글을 불러오지 못했습니다.'), 'error');
          navigate('/posts', { replace: true });
        }
      } finally {
        if (!ignore) setLoading(false);
      }
    }
    fetchPost();
    return () => {
      ignore = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [postId]);

  // AI 리뷰가 PENDING인 동안 3초 간격으로 다시 조회해서 COMPLETED/FAILED로 바뀐 것을 반영한다.
  // 상태가 더 이상 PENDING이 아니게 되면(post?.aiReviewStatus 변경) 이 effect가 재실행되며
  // 조건에 걸려 인터벌을 새로 걸지 않고, cleanup에서 이전 인터벌은 정리된다.
  useEffect(() => {
    if (!post || post.aiReviewStatus !== 'PENDING') return;

    let ignore = false;
    const intervalId = setInterval(async () => {
      try {
        const { data } = await postApi.getDetail(postId);
        if (!ignore) setPost(data.data);
      } catch (error) {
        // 폴링 중 일시적인 에러는 무시하고 다음 주기에 다시 시도한다.
      }
    }, 3000);

    return () => {
      ignore = true;
      clearInterval(intervalId);
    };
  }, [postId, post?.aiReviewStatus]);

  const handleDelete = async () => {
    setDeleting(true);
    try {
      await postApi.remove(postId);
      showToast('게시글이 삭제되었습니다.', 'success');
      navigate('/posts', { replace: true });
    } catch (error) {
      showToast(extractErrorMessage(error, '게시글 삭제에 실패했습니다.'), 'error');
      setDeleting(false);
    }
  };

  const handleRegenerate = async () => {
    setRegenerateSubmitting(true);
    try {
      const { data: regenerateData } = await aiReviewApi.regenerate(postId);
      // 코드가 바뀌지 않아 서버가 새로 호출하지 않고 기존 리뷰를 그대로 돌려준 경우(status
      // COMPLETED)는 재검토가 "아무 일도 안 일어난" 것처럼 보이지 않도록 토스트로 알린다.
      // 신규 생성인 경우(PENDING)엔 메시지 없이 기존 폴링 흐름으로 넘어간다.
      if (regenerateData.data.status === 'COMPLETED') {
        showToast(regenerateData.message, 'success');
      }
      const { data } = await postApi.getDetail(postId);
      setPost(data.data);
    } catch (error) {
      showToast(extractErrorMessage(error, 'AI 리뷰 재검토 요청에 실패했습니다.'), 'error');
    } finally {
      setRegenerateSubmitting(false);
    }
  };

  if (loading) return <PageSpinner label="게시글 불러오는 중..." />;
  if (!post) return null;

  const isOwner = user?.id === post.memberId;

  // 재생성이 시작되면 서버는 새 PENDING row를 "최신"으로 응답하므로 aiReviewContent가
  // 즉시 null로 바뀐다. 직전에 완료됐던 내용을 렌더 중에 파생시켜 들고 있다가, 재생성
  // 중/실패 시에도 화면에서 보여주기 위한 캐시다 (React가 권장하는 "이전 렌더값 저장" 패턴).
  if (post.aiReviewContent && post.aiReviewContent !== lastKnownReview) {
    setLastKnownReview(post.aiReviewContent);
  }

  return (
    <div className="flex min-w-0 flex-col gap-8">
      <div>
        <div className="flex items-center gap-2">
          <span className="rounded-md bg-primary-soft px-2 py-0.5 font-mono text-[11px] text-primary">
            {post.language}
          </span>
          <span className="rounded-md bg-surface-2 px-2 py-0.5 text-[11px] text-text-muted">
            {post.category}
          </span>
        </div>

        <div className="mt-3 flex items-start justify-between gap-4">
          <h1 className="min-w-0 break-words font-display text-2xl font-semibold text-text">
            {post.title}
          </h1>
          {isOwner && (
            <div className="flex shrink-0 gap-2">
              <Button as={Link} to={`/posts/${post.id}/edit`} variant="ghost" size="sm">
                수정
              </Button>
              <Button variant="danger" size="sm" onClick={() => setConfirmDelete(true)}>
                삭제
              </Button>
            </div>
          )}
        </div>

        <div className="mt-2 flex items-center gap-2 text-xs text-text-faint">
          <span className="font-medium text-text-muted">{post.memberNickname}</span>
          <span>·</span>
          <span>{formatDate(post.createdAt)}</span>
        </div>

        {post.summary && (
          <p className="mt-4 break-words text-sm leading-relaxed text-text-muted">{post.summary}</p>
        )}
        {post.reviewFocus && (
          <p className="mt-2 break-words rounded-lg border border-border-soft bg-surface-2 px-3 py-2 text-xs text-text-muted">
            리뷰 포커스: {post.reviewFocus}
          </p>
        )}
      </div>

      <CodeViewer code={post.codeContent} language={post.language} />

      <AIReviewPanel
        status={post.aiReviewStatus}
        content={post.aiReviewContent}
        previousContent={lastKnownReview}
        isOwner={isOwner}
        regenerating={regenerateSubmitting}
        onRegenerate={handleRegenerate}
      />

      <CommentList postId={post.id} />

      <ConfirmDialog
        open={confirmDelete}
        title="게시글을 삭제할까요?"
        description="삭제한 게시글은 되돌릴 수 없습니다."
        danger
        loading={deleting}
        onConfirm={handleDelete}
        onCancel={() => setConfirmDelete(false)}
      />
    </div>
  );
}