package com.studyolle.studyolle.account;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class SignUpForm {
    // 컨트롤러에서 @Valid 어노테이션 활용할려면 여기서 JSR303 어노테이션 추가해야함.
    // @NotBlank, @Length, @Pattern, @Email 등등의 어노테이션

    @NotBlank
    @Length(min=3, max=20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$")
    private String nickname;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Length(min=8, max=50)
    private String password;

}
