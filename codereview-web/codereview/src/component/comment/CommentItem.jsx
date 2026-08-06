import { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { formatDate } from '../../utils/formatDate';
import CommentForm from './CommentForm';
import RatingPanel from './RatingPanel';
import Button from '../common/Button';
import Textarea from '../common/Textarea';
import ConfirmDialog from '../common/ConfirmDialog';

export default function CommentItem({ comment, onReply, onUpdate, onDelete }) {
  const { user } = useAuth();
  const isOwner = user?.id === comment.memberId;
  const isDeleted = comment.status === 'DELETED';
  const isReply = comment.parentCommentId != null;

  const [replying, setReplying] = useState(false);
  const [editing, setEditing] = useState(false);
  const [editContent, setEditContent] = useState(comment.content);
  const [confirmDelete, setConfirmDelete] = useState(false);
  const [busy, setBusy] = useState(false);

  const handleReplySubmit = async (content) => {
    setBusy(true);
    try {
      await onReply(comment.id, content);
      setReplying(false);
    } finally {
      setBusy(false);
    }
  };

  const handleEditSubmit = async () => {
    if (!editContent.trim()) return;
    setBusy(true);
    try {
      await onUpdate(comment.id, editContent.trim());
      setEditing(false);
    } finally {
      setBusy(false);
    }
  };

  const handleDelete = async () => {
    setBusy(true);
    try {
      await onDelete(comment.id);
    } finally {
      setBusy(false);
      setConfirmDelete(false);
    }
  };

  return (
    <div className={isReply ? 'ml-8 border-l border-border-soft pl-4' : ''}>
      <div className="rounded-lg border border-border bg-surface p-4">
        <div className="flex items-center gap-2 text-xs text-text-faint">
          <span className="font-medium text-text">{comment.memberNickname}</span>
          <span>·</span>
          <span>{formatDate(comment.createdAt)}</span>
        </div>

        {editing ? (
          <div className="mt-2 flex flex-col gap-2">
            <Textarea
              rows={2}
              value={editContent}
              onChange={(e) => setEditContent(e.target.value)}
            />
            <div className="flex justify-end gap-2">
              <Button variant="ghost" size="sm" onClick={() => setEditing(false)}>
                취소
              </Button>
              <Button size="sm" loading={busy} onClick={handleEditSubmit}>
                저장
              </Button>
            </div>
          </div>
        ) : (
          <p
            className={`mt-2 whitespace-pre-wrap text-sm leading-relaxed ${
              isDeleted ? 'italic text-text-faint' : 'text-text'
            }`}
          >
            {comment.content}
          </p>
        )}

        {!isDeleted && !editing && (
          <div className="mt-2 flex items-center gap-3 text-xs text-text-muted">
            {!isReply && (
              <button
                type="button"
                className="hover:text-primary"
                onClick={() => setReplying((v) => !v)}
              >
                답글
              </button>
            )}
            {isOwner && (
              <>
                <button type="button" className="hover:text-primary" onClick={() => setEditing(true)}>
                  수정
                </button>
                <button
                  type="button"
                  className="hover:text-danger"
                  onClick={() => setConfirmDelete(true)}
                >
                  삭제
                </button>
              </>
            )}
          </div>
        )}

        {!isDeleted && <RatingPanel commentId={comment.id} />}

        {replying && (
          <div className="mt-3">
            <CommentForm
              placeholder={`${comment.memberNickname}님에게 답글 남기기`}
              submitLabel="답글 등록"
              loading={busy}
              onSubmit={handleReplySubmit}
              onCancel={() => setReplying(false)}
            />
          </div>
        )}
      </div>

      <ConfirmDialog
        open={confirmDelete}
        title="댓글을 삭제할까요?"
        description="삭제한 댓글은 되돌릴 수 없습니다."
        danger
        loading={busy}
        onConfirm={handleDelete}
        onCancel={() => setConfirmDelete(false)}
      />
    </div>
  );
}
