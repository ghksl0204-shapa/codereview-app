import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { postApi } from '../../api/postApi';
import { extractErrorMessage, useToast } from '../../context/ToastContext';
import PostFilterBar from '../../component/post/PostFilterBar';
import PostCard from '../../component/post/PostCard';
import Pagination from '../../component/common/Pagination';
import EmptyState from '../../component/common/EmptyState';
import { PageSpinner } from '../../component/common/Spinner';
import Button from '../../component/common/Button';

const LIMIT = 9;

export default function PostListPage() {
  const { showToast } = useToast();

  const [filters, setFilters] = useState({});
  const [offset, setOffset] = useState(0);
  const [page, setPage] = useState({ items: [], totalCount: 0, limit: LIMIT });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    let ignore = false;

    async function fetchPosts() {
      setLoading(true);
      try {
        const { data } = await postApi.getList({ offset, limit: LIMIT, ...filters });
        if (!ignore) setPage(data.data);
      } catch (error) {
        if (!ignore) showToast(extractErrorMessage(error, '게시글을 불러오지 못했습니다.'), 'error');
      } finally {
        if (!ignore) setLoading(false);
      }
    }

    fetchPosts();
    return () => {
      ignore = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [offset, filters]);

  const handleApplyFilters = (nextFilters) => {
    setFilters(nextFilters);
    setOffset(0);
  };

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-end justify-between">
        <div>
          <h1 className="font-display text-2xl font-semibold text-text">게시글</h1>
          <p className="mt-1 text-sm text-text-muted">
            코드를 올리고 AI 리뷰와 동료 피드백을 받아보세요.
          </p>
        </div>
        <Button as={Link} to="/posts/new" size="md">
          + 코드 업로드
        </Button>
      </div>

      <PostFilterBar filters={filters} onApply={handleApplyFilters} />

      {loading ? (
        <PageSpinner label="게시글 불러오는 중..." />
      ) : page.items.length === 0 ? (
        <EmptyState
          title="조건에 맞는 게시글이 없습니다"
          description="필터를 변경하거나 첫 코드 리뷰 요청을 올려보세요."
          action={
            <Button as={Link} to="/posts/new" size="md">
              코드 업로드하기
            </Button>
          }
        />
      ) : (
        <>
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {page.items.map((post) => (
              <PostCard key={post.id} post={post} />
            ))}
          </div>
          <Pagination
            offset={page.offset}
            limit={page.limit}
            totalCount={page.totalCount}
            onChangeOffset={setOffset}
          />
        </>
      )}
    </div>
  );
}
