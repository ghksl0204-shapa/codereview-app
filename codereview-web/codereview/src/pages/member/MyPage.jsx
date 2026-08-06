import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { memberApi } from '../../api/memberApi';
import { extractErrorMessage, useToast } from '../../context/ToastContext';
import { useAuth } from '../../context/AuthContext';
import Input from '../../component/common/Input';
import Button from '../../component/common/Button';
import ConfirmDialog from '../../component/common/ConfirmDialog';

export default function MyPage() {
  const { user, updateNickname, logout } = useAuth();
  const { showToast } = useToast();
  const navigate = useNavigate();

  const [nickname, setNickname] = useState(user?.nickname ?? '');
  const [nicknameLoading, setNicknameLoading] = useState(false);

  const [passwordForm, setPasswordForm] = useState({ currentPassword: '', newPassword: '' });
  const [passwordLoading, setPasswordLoading] = useState(false);

  const [withdrawPassword, setWithdrawPassword] = useState('');
  const [confirmWithdraw, setConfirmWithdraw] = useState(false);
  const [withdrawLoading, setWithdrawLoading] = useState(false);

  const handleNicknameSubmit = async (e) => {
    e.preventDefault();
    if (nickname.length < 2 || nickname.length > 10) {
      showToast('닉네임은 2자 이상 10자 이하로 입력해주세요.', 'error');
      return;
    }
    setNicknameLoading(true);
    try {
      await memberApi.updateNickname(nickname);
      updateNickname(nickname);
      showToast('닉네임이 변경되었습니다.', 'success');
    } catch (error) {
      showToast(extractErrorMessage(error, '닉네임 변경에 실패했습니다.'), 'error');
    } finally {
      setNicknameLoading(false);
    }
  };

  const handlePasswordSubmit = async (e) => {
    e.preventDefault();
    if (passwordForm.newPassword.length < 8 || passwordForm.newPassword.length > 20) {
      showToast('새 비밀번호는 8자 이상 20자 이하로 입력해주세요.', 'error');
      return;
    }
    setPasswordLoading(true);
    try {
      await memberApi.updatePassword(passwordForm.currentPassword, passwordForm.newPassword);
      showToast('비밀번호가 변경되었습니다.', 'success');
      setPasswordForm({ currentPassword: '', newPassword: '' });
    } catch (error) {
      showToast(extractErrorMessage(error, '비밀번호 변경에 실패했습니다.'), 'error');
    } finally {
      setPasswordLoading(false);
    }
  };

  const handleWithdraw = async () => {
    setWithdrawLoading(true);
    try {
      await memberApi.withdraw(withdrawPassword);
      showToast('회원 탈퇴가 완료되었습니다.', 'success');
      await logout();
      navigate('/login', { replace: true });
    } catch (error) {
      showToast(extractErrorMessage(error, '회원 탈퇴에 실패했습니다.'), 'error');
      setWithdrawLoading(false);
      setConfirmWithdraw(false);
    }
  };

  return (
    <div className="mx-auto flex max-w-lg flex-col gap-10">
      <div>
        <h1 className="font-display text-2xl font-semibold text-text">마이페이지</h1>
        <p className="mt-1 text-sm text-text-muted">
          {user?.id} · {user?.role === 'ADMIN' ? '관리자' : '일반회원'}
        </p>
      </div>

      <form onSubmit={handleNicknameSubmit} className="flex flex-col gap-3 rounded-xl border border-border bg-surface p-5">
        <h2 className="font-display text-base font-medium text-text">닉네임 변경</h2>
        <Input value={nickname} onChange={(e) => setNickname(e.target.value)} hint="2~10자" />
        <Button type="submit" size="sm" loading={nicknameLoading} className="self-start">
          변경하기
        </Button>
      </form>

      <form onSubmit={handlePasswordSubmit} className="flex flex-col gap-3 rounded-xl border border-border bg-surface p-5">
        <h2 className="font-display text-base font-medium text-text">비밀번호 변경</h2>
        <Input
          type="password"
          label="현재 비밀번호"
          value={passwordForm.currentPassword}
          onChange={(e) => setPasswordForm((p) => ({ ...p, currentPassword: e.target.value }))}
        />
        <Input
          type="password"
          label="새 비밀번호"
          value={passwordForm.newPassword}
          onChange={(e) => setPasswordForm((p) => ({ ...p, newPassword: e.target.value }))}
          hint="8~20자"
        />
        <Button type="submit" size="sm" loading={passwordLoading} className="self-start">
          변경하기
        </Button>
      </form>

      <div className="flex flex-col gap-3 rounded-xl border border-danger/30 bg-surface p-5">
        <h2 className="font-display text-base font-medium text-text">회원 탈퇴</h2>
        <p className="text-xs text-text-muted">탈퇴 시 계정 정보는 복구할 수 없습니다.</p>
        <Input
          type="password"
          label="현재 비밀번호"
          value={withdrawPassword}
          onChange={(e) => setWithdrawPassword(e.target.value)}
        />
        <Button
          variant="danger"
          size="sm"
          className="self-start"
          disabled={!withdrawPassword}
          onClick={() => setConfirmWithdraw(true)}
        >
          회원 탈퇴
        </Button>
      </div>

      <ConfirmDialog
        open={confirmWithdraw}
        title="정말 탈퇴하시겠어요?"
        description="이 작업은 되돌릴 수 없습니다."
        danger
        loading={withdrawLoading}
        onConfirm={handleWithdraw}
        onCancel={() => setConfirmWithdraw(false)}
      />
    </div>
  );
}
