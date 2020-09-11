package com.studyolle.studyolle;

import com.studyolle.studyolle.account.AccountService;
import com.studyolle.studyolle.account.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {
    // WithAccount는 어노테이션 클래스임.

    // 이 클래스는 빈으로 등록이 되기 때문에
    // 우리가 필요로 하는 빈들을 주입 받을수 있음
    // 이렇게.
    private final AccountService accountService;

    @Override
    public SecurityContext createSecurityContext(WithAccount withAccount) {
        // 커스텀 어노테이션에서 value 받아오기.
        String nickname = withAccount.value();

        // 유저정보 추가
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("jwkoo88@gmail.com");
        signUpForm.setNickname(nickname);
        signUpForm.setPassword("123456789");
        accountService.processNewAccount(signUpForm);

        // 시큐리티 컨텍스트에 user 정보 추가 원래는 이것만 하면 되지만,
        // 우리는 위처럼 유저 정보를 생성하는것도 할꺼임.
        UserDetails principal = accountService.loadUserByUsername(nickname);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        return context;
    }
}