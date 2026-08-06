import { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { extractErrorMessage, useToast } from '../../context/ToastContext';
import Input from '../../component/common/Input';
import Button from '../../component/common/Button';

export default function LoginPage() {
  const { login } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();
  const location = useLocation();

  const [form, setForm] = useState({ id: '', password: '' });
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await login(form.id, form.password);
      const redirectTo = location.state?.from?.pathname ?? '/posts';
      navigate(redirectTo, { replace: true });
    } catch (error) {
      showToast(extractErrorMessage(error, '아이디 또는 비밀번호를 확인해주세요.'), 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="grid min-h-svh grid-cols-1 lg:grid-cols-[1.1fr_1fr]">
      {/* 브랜딩 패널 */}
      <div className="relative hidden flex-col justify-between overflow-hidden border-r border-border-soft bg-surface px-14 py-12 lg:flex">
        <div className="flex items-center gap-2">
          <span className="flex h-8 w-8 items-center justify-center rounded-md bg-primary font-mono text-sm font-bold text-[#0b0e14]">
            {'</>'}
          </span>
          <span className="font-display text-xl font-semibold text-text">CodeReview</span>
        </div>

        <div className="max-w-md">
          <h1 className="font-display text-4xl font-semibold leading-[1.15] text-text">
            코드를 올리면,
            <br />
            AI와 동료가 함께 리뷰합니다.
          </h1>
          <p className="mt-4 text-sm leading-relaxed text-text-muted">
            AI 자동 리뷰로 먼저 점검받고, 댓글과 평점으로 동료 개발자들과 함께
            더 나은 코드를 만들어가는 학습 커뮤니티입니다.
          </p>
        </div>

        {/* 코드 diff 느낌의 시그니처 장식 */}
        <div className="monaco-shell bg-bg font-mono text-[13px] leading-7">
          <div className="border-b border-border bg-surface-2 px-4 py-2 text-xs text-text-faint">
            review.diff
          </div>
          <div className="px-4 py-3">
            <p className="text-text-faint">function calculateTotal(items) {'{'}</p>
            <p className="bg-danger-soft px-1 text-red-300">
              - &nbsp;return items.reduce((a, b) =&gt; a + b.price)
            </p>
            <p className="bg-mint-soft px-1 text-emerald-300">
              + &nbsp;return items.reduce((a, b) =&gt; a + b.price, 0)
            </p>
            <p className="text-text-faint">{'}'}</p>
            <p className="mt-2 text-amber-300">
              💡 초기값 0을 지정하지 않으면 빈 배열일 때 오류가 발생해요.
            </p>
          </div>
        </div>
      </div>

      {/* 로그인 폼 */}
      <div className="flex flex-col items-center justify-center px-6 py-16">
        <div className="w-full max-w-sm">
          <div className="mb-8 flex items-center gap-2 lg:hidden">
            <span className="flex h-8 w-8 items-center justify-center rounded-md bg-primary font-mono text-sm font-bold text-[#0b0e14]">
              {'</>'}
            </span>
            <span className="font-display text-xl font-semibold text-text">CodeReview</span>
          </div>

          <h2 className="font-display text-2xl font-semibold text-text">로그인</h2>
          <p className="mt-1.5 text-sm text-text-muted">
            아이디와 비밀번호로 계속 진행하세요.
          </p>

          <form onSubmit={handleSubmit} className="mt-8 flex flex-col gap-4">
            <Input
              id="id"
              name="id"
              label="아이디"
              autoComplete="username"
              value={form.id}
              onChange={handleChange}
              placeholder="5~12자"
              required
            />
            <Input
              id="password"
              name="password"
              type="password"
              label="비밀번호"
              autoComplete="current-password"
              value={form.password}
              onChange={handleChange}
              placeholder="8~20자"
              required
            />
            <Button type="submit" size="lg" loading={loading} className="mt-2 w-full">
              로그인
            </Button>
          </form>

          <div className="my-6 flex items-center gap-3 text-xs text-text-faint">
            <span className="h-px flex-1 bg-border" />
            소셜 로그인 (예정)
            <span className="h-px flex-1 bg-border" />
          </div>

          <div className="flex flex-col gap-2">
            <Button variant="ghost" size="lg" className="w-full" disabled title="추후 지원 예정">
              Google로 계속하기
            </Button>
            <Button variant="ghost" size="lg" className="w-full" disabled title="추후 지원 예정">
              GitHub로 계속하기
            </Button>
          </div>

          <p className="mt-8 text-center text-sm text-text-muted">
            아직 계정이 없으신가요?{' '}
            <Link to="/signup" className="font-medium text-primary hover:text-primary-hover">
              회원가입
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
