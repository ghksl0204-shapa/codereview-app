// 서버 DTO의 검증 규칙과 쌍으로 관리되는 파일이다.
//   - MemberJoinRequestDto (아이디/비밀번호/닉네임/이메일)
//   - MemberUpdateNicknameRequestDto (닉네임)
//   - MemberUpdatePasswordRequestDto (새 비밀번호)
// 한쪽만 수정하면 "프론트는 통과하는데 서버가 거부"하거나 그 반대인 불일치가 생기므로,
// 규칙을 바꿀 때는 반드시 서버 DTO의 @Pattern/@Size와 함께 수정할 것.

export const ID_LENGTH = { min: 5, max: 12 };
export const PASSWORD_LENGTH = { min: 8, max: 20 };
export const NICKNAME_LENGTH = { min: 2, max: 10 };

// 허용: 영문 대소문자, 숫자, 밑줄(_) / 차단: 공백, 한글, 그 외 특수문자
export const ID_PATTERN = /^[a-zA-Z0-9_]*$/;

// 필수: 영문 1자 이상 + 숫자 1자 이상 / 차단: 공백(\S)
// 그 외 특수문자는 종류를 제한하지 않는다 (패스워드 매니저가 만든 비밀번호를 거부하지 않기 위함)
export const PASSWORD_PATTERN = /^(?=.*[a-zA-Z])(?=.*[0-9])\S+$/;

// 차단: 공백만 / 문자 종류는 제한하지 않는다 (한글 닉네임 허용)
export const NICKNAME_PATTERN = /^\S*$/;

// 이메일 구조 검증 — 서버의 @Email에 대응
export const EMAIL_FORMAT_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

// 이메일 문자 종류 제한 — 서버의 @Pattern에 대응. 허용: 영문, 숫자, @ . _ -
export const EMAIL_CHAR_PATTERN = /^[a-zA-Z0-9@._-]*$/;

export const VALIDATION_MESSAGES = {
  idRequired: '아이디는 필수 입력값입니다.',
  idLength: `아이디는 ${ID_LENGTH.min}자 이상 ${ID_LENGTH.max}자 이하로 입력해주세요.`,
  idPattern: '아이디는 영문, 숫자, 밑줄(_)만 사용할 수 있습니다.',
  passwordConfirm: '비밀번호가 일치하지 않습니다.',
  nicknameRequired: '닉네임은 필수 입력값입니다.',
  nicknameLength: `닉네임은 ${NICKNAME_LENGTH.min}자 이상 ${NICKNAME_LENGTH.max}자 이하로 입력해주세요.`,
  nicknamePattern: '닉네임에는 공백을 사용할 수 없습니다.',
  emailRequired: '이메일은 필수 입력값입니다.',
  emailFormat: '올바른 이메일 형식이 아닙니다.',
  emailPattern: '이메일은 영문, 숫자와 @ . _ - 만 사용할 수 있습니다.',
};

// 화면에 표시할 입력 힌트 — 규칙이 바뀌면 이 문구도 함께 갱신할 것
export const VALIDATION_HINTS = {
  id: `${ID_LENGTH.min}~${ID_LENGTH.max}자, 영문·숫자·밑줄(_)`,
  password: `${PASSWORD_LENGTH.min}~${PASSWORD_LENGTH.max}자, 영문·숫자 각 1자 이상 포함(공백 불가)`,
  nickname: `${NICKNAME_LENGTH.min}~${NICKNAME_LENGTH.max}자, 공백 불가`,
  email: '영문·숫자와 @ . _ - 만 사용',
};

// 아래 검증 함수들은 회원가입 폼과 마이페이지가 함께 쓴다.
// 규칙 판단을 한 곳에 두어 두 화면이 어긋나지 않게 하기 위한 것이며,
// 위반 시 사용자에게 보여줄 메시지를 반환하고 문제가 없으면 null을 반환한다.

export function validateId(value) {
  if (!value) return VALIDATION_MESSAGES.idRequired;
  if (value.length < ID_LENGTH.min || value.length > ID_LENGTH.max) return VALIDATION_MESSAGES.idLength;
  if (!ID_PATTERN.test(value)) return VALIDATION_MESSAGES.idPattern;
  return null;
}

// label: '비밀번호' | '새 비밀번호' — 회원가입과 비밀번호 변경에서 문구만 달라진다
export function validatePassword(value, label = '비밀번호') {
  if (!value) return `${label}는 필수 입력값입니다.`;
  if (value.length < PASSWORD_LENGTH.min || value.length > PASSWORD_LENGTH.max) {
    return `${label}는 ${PASSWORD_LENGTH.min}자 이상 ${PASSWORD_LENGTH.max}자 이하로 입력해주세요.`;
  }
  if (!PASSWORD_PATTERN.test(value)) {
    return `${label}는 영문과 숫자를 각각 1자 이상 포함하고, 공백 없이 입력해주세요.`;
  }
  return null;
}

export function validateNickname(value) {
  if (!value) return VALIDATION_MESSAGES.nicknameRequired;
  if (value.length < NICKNAME_LENGTH.min || value.length > NICKNAME_LENGTH.max) {
    return VALIDATION_MESSAGES.nicknameLength;
  }
  if (!NICKNAME_PATTERN.test(value)) return VALIDATION_MESSAGES.nicknamePattern;
  return null;
}

export function validateEmail(value) {
  if (!value) return VALIDATION_MESSAGES.emailRequired;
  // 문자 종류를 먼저 본다 — 한글이 섞인 경우 "형식이 아니다"보다 구체적인 안내가 되기 때문
  if (!EMAIL_CHAR_PATTERN.test(value)) return VALIDATION_MESSAGES.emailPattern;
  if (!EMAIL_FORMAT_PATTERN.test(value)) return VALIDATION_MESSAGES.emailFormat;
  return null;
}
