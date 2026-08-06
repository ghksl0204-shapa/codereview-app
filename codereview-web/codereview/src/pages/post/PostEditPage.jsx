import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { postApi } from '../../api/postApi';
import { extractErrorMessage, useToast } from '../../context/ToastContext';
import { useAuth } from '../../context/AuthContext';
import PostForm from '../../component/post/PostForm';
import { PageSpinner } from '../../component/common/Spinner';

export default function PostEditPage() {
  const { postId } = useParams();
  const { user } = useAuth();
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [post, setPost] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    let ignore = false;
    async function fetchPost() {
      try {
        const { data } = await postApi.getDetail(postId);
        if (ignore) return;

        if (data.data.memberId !== user?.id) {
          showToast('본인이 작성한 게시글만 수정할 수 있습니다.', 'error');
          navigate(`/posts/${postId}`, { replace: true });
          return;
        }
        setPost(data.data);
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

  const handleSubmit = async (form) => {
    setSubmitting(true);
    try {
      await postApi.update(postId, form);
      showToast('게시글이 수정되었습니다.', 'success');
      navigate(`/posts/${postId}`, { replace: true });
    } catch (error) {
      showToast(extractErrorMessage(error, '게시글 수정에 실패했습니다.'), 'error');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <PageSpinner label="게시글 불러오는 중..." />;
  if (!post) return null;

  return (
    <div className="mx-auto max-w-3xl">
      <h1 className="font-display text-2xl font-semibold text-text">게시글 수정</h1>
      <div className="mt-8">
        <PostForm
          initialValues={{
            title: post.title,
            summary: post.summary ?? '',
            codeContent: post.codeContent,
            language: post.language,
            category: post.category,
            reviewFocus: post.reviewFocus ?? '',
          }}
          submitLabel="수정하기"
          loading={submitting}
          onSubmit={handleSubmit}
        />
      </div>
    </div>
  );
}
