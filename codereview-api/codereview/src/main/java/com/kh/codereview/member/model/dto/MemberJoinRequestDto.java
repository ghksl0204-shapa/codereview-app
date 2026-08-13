package com.kh.codereview.member.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 각 @Pattern은 빈 문자열을 통과시키는 * 수량자를 쓴다(비밀번호 제외).
 * 빈 값일 때 @NotBlank와 @Pattern이 동시에 실패하면 어느 메시지가 노출될지 보장되지 않으므로,
 * "비어 있음"은 @NotBlank가, "형식 위반"은 @Pattern이 각각 담당하게 분리한 것이다.
 */
@Getter
public class MemberJoinRequestDto {

    // 허용: 영문 대소문자, 숫자, 밑줄(_) / 차단: 공백, 한글, 그 외 특수문자
    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Size(min = 5, max = 12, message = "아이디는 5자 이상 12자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "아이디는 영문, 숫자, 밑줄(_)만 사용할 수 있습니다.")
    private String id;

    // 필수: 영문 1자 이상(첫 lookahead) + 숫자 1자 이상(두 번째 lookahead) / 차단: 공백(\S)
    // 그 외 특수문자는 종류를 제한하지 않는다 — 허용 문자를 좁히면 강한 비밀번호가 거부되어 보안상 나쁘다
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])\\S+$",
            message = "비밀번호는 영문과 숫자를 각각 1자 이상 포함하고, 공백 없이 입력해주세요.")
    private String password;

    // 차단: 공백만 / 문자 종류는 제한하지 않는다 — 한글 닉네임(동동동동 등)이 실제로 존재하므로 막으면 안 된다
    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    @Pattern(regexp = "^\\S*$", message = "닉네임에는 공백을 사용할 수 없습니다.")
    private String nickname;

    // 허용: 영문, 숫자, @ . _ - / 차단: 한글 등 비ASCII 문자와 공백
    // 이메일 구조(@ 위치, 도메인 형태) 검증은 @Email이 담당하므로 대체하지 않고 함께 사용한다
    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @Pattern(regexp = "^[a-zA-Z0-9@._-]*$", message = "이메일은 영문, 숫자와 @ . _ - 만 사용할 수 있습니다.")
    private String email;
}