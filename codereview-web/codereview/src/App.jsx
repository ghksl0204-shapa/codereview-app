import { Navigate, Route, Routes } from 'react-router-dom';

import PublicOnlyRoute from './component/layout/PublicOnlyRoute';
import PrivateRoute from './component/layout/PrivateRoute';
import Layout from './component/layout/Layout';

import LoginPage from './pages/auth/LoginPage';
import SignupPage from './pages/auth/SignupPage';
import PostListPage from './pages/post/PostListPage';
import PostDetailPage from './pages/post/PostDetailPage';
import PostCreatePage from './pages/post/PostCreatePage';
import PostEditPage from './pages/post/PostEditPage';
import MyPage from './pages/member/MyPage';

function App() {
  return (
    <Routes>
      {/* 첫 화면: 로그인 여부에 따라 로그인 또는 게시글 목록으로 */}
      <Route path="/" element={<Navigate to="/login" replace />} />

      <Route element={<PublicOnlyRoute />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />
      </Route>

      <Route element={<PrivateRoute />}>
        <Route element={<Layout />}>
          <Route path="/posts" element={<PostListPage />} />
          <Route path="/posts/new" element={<PostCreatePage />} />
          <Route path="/posts/:postId" element={<PostDetailPage />} />
          <Route path="/posts/:postId/edit" element={<PostEditPage />} />
          <Route path="/mypage" element={<MyPage />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;
