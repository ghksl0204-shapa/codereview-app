import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { memberApi } from '../../api/memberApi';
import { extractErrorMessage, useToast } from '../../context/ToastContext';
import Input from '../../component/common/Input';
import Button from '../../component/common/Button';

const INITIAL = { id: '', password: '', passwordConfirm: '', nickname: '', email: '' };

function validate(form) {
  const errors = {};
  if (form.id.length < 5 || form.id.length > 12) {
    errors.id = '아이디는 5자 이상 12자 이하로 입력해주세요.';
  }
  if (form.password.length < 8 || form.password.length > 20) {
    errors.password = '비밀번호는 8자 이상 20자 이하로 입력해주세요.';
  }
  if (form.passwordConfirm !== form.password) {
    errors.passwordConfirm = '비밀번호가 일치하지 않습니다.';
  }
  if (form.nickname.length < 2 || form.nickname.length > 10) {
    errors.nickname = '닉네임은 2자 이상 10자 이하로 입력해주세요.';
  }
  if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
    errors.email = '올바른 이메일 형식이 아닙니다.';
  }
  return errors;
}

export default function SignupPage() {
  const navigate = useNavigate();
  const { showToast } = useToast();

  const [form, setForm] = useState(INITIAL);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const clientErrors = validate(form);
    setErrors(clientErrors);
    if (Object.keys(clientErrors).length > 0) return;

    setLoading(true);
    try {
      await memberApi.join({
        id: form.id,
        password: form.password,
        nickname: form.nickname,
        email: form.email,
      });
      showToast('회원가입이 완료되었습니다. 로그인해주세요.', 'success');
      navigate('/login', { replace: true });
    } catch (error) {
      showToast(extractErrorMessage(error, '회원가입에 실패했습니다.'), 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex min-h-svh flex-col items-center justify-center px-6 py-16">
      <div className="w-full max-w-sm">
        <div className="mb-8 flex items-center gap-2">
          <span className="flex h-8 w-8 items-center justify-center rounded-md bg-primary font-mono text-sm font-bold text-[#0b0e14]">
            {'</>'}
          </span>
          <span className="font-display text-xl font-semibold text-text">CodeReview</span>
        </div>

        <h2 className="font-display text-2xl font-semibold text-text">회원가입</h2>
        <p className="mt-1.5 text-sm text-text-muted">
          코드 리뷰 커뮤니티에 참여할 계정을 만들어주세요.
        </p>

        <form onSubmit={handleSubmit} className="mt-8 flex flex-col gap-4">
          <Input
            id="id"
            name="id"
            label="아이디"
            value={form.id}
            onChange={handleChange}
            error={errors.id}
            hint="5~12자"
            required
          />
          <Input
            id="email"
            name="email"
            type="email"
            label="이메일"
            value={form.email}
            onChange={handleChange}
            error={errors.email}
            required
          />
          <Input
            id="nickname"
            name="nickname"
            label="닉네임"
            value={form.nickname}
            onChange={handleChange}
            error={errors.nickname}
            hint="2~10자"
            required
          />
          <Input
            id="password"
            name="password"
            type="password"
            label="비밀번호"
            value={form.password}
            onChange={handleChange}
            error={errors.password}
            hint="8~20자"
            required
          />
          <Input
            id="passwordConfirm"
            name="passwordConfirm"
            type="password"
            label="비밀번호 확인"
            value={form.passwordConfirm}
            onChange={handleChange}
            error={errors.passwordConfirm}
            required
          />

          <Button type="submit" size="lg" loading={loading} className="mt-2 w-full">
            가입하기
          </Button>
        </form>

        <p className="mt-8 text-center text-sm text-text-muted">
          이미 계정이 있으신가요?{' '}
          <Link to="/login" className="font-medium text-primary hover:text-primary-hover">
            로그인
          </Link>
        </p>
      </div>
    </div>
  );
}
