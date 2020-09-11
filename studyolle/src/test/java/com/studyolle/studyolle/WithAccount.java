package com.studyolle.studyolle;

import lombok.With;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAccountSecurityContextFactory.class)
// 팩토리 지정해서 시큐리티 컨텍스트 홀더에 유저 정보 세팅
public @interface WithAccount {
    String value();
}
