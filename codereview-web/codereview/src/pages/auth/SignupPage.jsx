import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { memberApi } from '../../api/memberApi';
import { extractErrorMessage, useToast } from '../../context/ToastContext';
import Input from '../../component/common/Input';
import Button from '../../component/common/Button';
import {
  VALIDATION_HINTS,
  VALIDATION_MESSAGES,
  validateEmail,
  validateId,
  validateNickname,
  validatePassword,
} from '../../constants/validation';

const INITIAL = { id: '', password: '', passwordConfirm: '', nickname: '', email: '' };

// 규칙 판단은 constants/validation.js에 모여 있다 (서버 DTO와 쌍으로 관리)
function validate(form) {
  const errors = {};
  const checks = {
    id: validateId(form.id),
    password: validatePassword(form.password),
    passwordConfirm: form.passwordConfirm !== form.password ? VALIDATION_MESSAGES.passwordConfirm : null,
    nickname: validateNickname(form.nickname),
    email: validateEmail(form.email),
  };
  for (const [field, message] of Object.entries(checks)) {
    if (message) errors[field] = message;
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
      // 검증 실패 응답은 data에 {필드명: 메시지} Map이 담겨 온다 → 각 입력칸 아래 인라인으로 표시
      const fieldErrors = error?.response?.data?.data;
      if (fieldErrors && typeof fieldErrors === 'object' && Object.keys(fieldErrors).length > 0) {
        setErrors(fieldErrors);
      }
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
            hint={VALIDATION_HINTS.id}
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
            hint={VALIDATION_HINTS.email}
            required
          />
          <Input
            id="nickname"
            name="nickname"
            label="닉네임"
            value={form.nickname}
            onChange={handleChange}
            error={errors.nickname}
            hint={VALIDATION_HINTS.nickname}
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
            hint={VALIDATION_HINTS.password}
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
