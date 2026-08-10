import { useEffect, useState } from 'react';
import { commentApi } from '../../api/commentApi';
import { extractErrorMessage, useToast } from '../../context/ToastContext';
import CommentForm from './CommentForm';
import CommentItem from './CommentItem';
import EmptyState from '../common/EmptyState';
import { PageSpinner } from '../common/Spinner';

const LIMIT = 50;

export default function CommentList({ postId }) {
  const { showToast } = useToast();
  const [comments, setComments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [posting, setPosting] = useState(false);

  const fetchComments = async () => {
    try {
      const { data } = await commentApi.getList({ postId, limit: LIMIT });
      setComments(data.data.items);
    } catch (error) {
      showToast(extractErrorMessage(error, '댓글을 불러오지 못했습니다.'), 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect -- 게시글 변경 시 댓글을 새로 불러오는 표준 패턴
    fetchComments();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [postId]);

  const handleCreate = async (content) => {
    setPosting(true);
    try {
      await commentApi.create({ postId: Number(postId), content });
      await fetchComments();
    } catch (error) {
      showToast(extractErrorMessage(error, '댓글 등록에 실패했습니다.'), 'error');
    } finally {
      setPosting(false);
    }
  };

  const handleReply = async (parentCommentId, content) => {
    try {
      await commentApi.create({ postId: Number(postId), content, parentCommentId });
      await fetchComments();
    } catch (error) {
      showToast(extractErrorMessage(error, '답글 등록에 실패했습니다.'), 'error');
    }
  };

  const handleUpdate = async (commentId, content) => {
    try {
      await commentApi.update(commentId, content);
      await fetchComments();
      showToast('댓글이 수정되었습니다.', 'success');
    } catch (error) {
      showToast(extractErrorMessage(error, '댓글 수정에 실패했습니다.'), 'error');
    }
  };

  const handleDelete = async (commentId) => {
    try {
      await commentApi.remove(commentId);
      await fetchComments();
      showToast('댓글이 삭제되었습니다.', 'success');
    } catch (error) {
      showToast(extractErrorMessage(error, '댓글 삭제에 실패했습니다.'), 'error');
    }
  };

  return (
    <section className="flex flex-col gap-4">
      <h2 className="font-display text-base font-medium text-text">
        댓글 {comments.length > 0 && `(${comments.length})`}
      </h2>

      <CommentForm submitLabel="댓글 등록" loading={posting} onSubmit={handleCreate} />

      {loading ? (
        <PageSpinner label="댓글 불러오는 중..." />
      ) : comments.length === 0 ? (
        <EmptyState title="아직 댓글이 없습니다" description="가장 먼저 의견을 남겨보세요." />
      ) : (
        <div className="flex flex-col gap-3">
          {comments.map((comment) => (
            <CommentItem
              key={comment.id}
              comment={comment}
              onReply={handleReply}
              onUpdate={handleUpdate}
              onDelete={handleDelete}
              onRatingChanged={fetchComments}
            />
          ))}
        </div>
      )}
    </section>
  );
}
