import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { postApi } from '../../api/postApi';
import { extractErrorMessage, useToast } from '../../context/ToastContext';
import PostForm from '../../component/post/PostForm';

export default function PostCreatePage() {
  const navigate = useNavigate();
  const { showToast } = useToast();
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (form) => {
    setLoading(true);
    try {
      const { data } = await postApi.create(form);
      showToast('게시글이 등록되었습니다.', 'success');
      navigate(`/posts/${data.data.id}`, { replace: true });
    } catch (error) {
      showToast(extractErrorMessage(error, '게시글 등록에 실패했습니다.'), 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="mx-auto max-w-3xl">
      <h1 className="font-display text-2xl font-semibold text-text">코드 업로드</h1>
      <p className="mt-1 text-sm text-text-muted">
        코드를 등록하면 AI 리뷰가 자동으로 생성되고, 동료들의 댓글과 평점을 받을 수 있습니다.
      </p>
      <div className="mt-8">
        <PostForm submitLabel="등록하기" loading={loading} onSubmit={handleSubmit} />
      </div>
    </div>
  );
}
