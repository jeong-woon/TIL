package com.studyolle.studyolle.account;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor // 롬복이 private final로 선언된 빈을 자동으로 생성자를 만들어주고, 빈을 주입 받을수 있다. @Autowired 안써도 됨.
public class SignUpFormValidator implements Validator {

    // 이게 빈인데, 빈을 주입 받을려면 이 클래스 자체가 빈이 되어야 함.
    private final AccountRepository accountRepository;

    // 어떤 빈이 생성자가 하나만 있고, 그 생성자가 받는 파라미터들이 빈으로 등록되어있다면 스프링4.2 이후부터는 자동으로 빈을 주입해준다.
    // 따라서 오토와이어드나 인젝트 같은 어노테이션을 쓰지 않아도 의존성 주입이 된다.

//    public SignUpFormValidator(AccountRepository accountRepository) {
//        this.accountRepository = accountRepository;
//    }

    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object object, Errors errors) {
        // TODO : 이메일, 닉네임 중복검사, DB 조회!
        SignUpForm signUpForm = (SignUpForm)object;
        if(accountRepository.existsByEmail(signUpForm.getEmail())){
            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "이미 사용중인 이메일 입니다.");
        }

        if(accountRepository.existsByNickname(signUpForm.getNickname())){
            errors.rejectValue("nickname", "invalid.nickname", new Object[]{signUpForm.getNickname()}, "이미 사용중인 닉네임 입니다.");
        }
    }
}
