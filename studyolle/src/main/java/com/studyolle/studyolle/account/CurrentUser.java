package com.studyolle.studyolle.account;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 이게 뭐하는거지?? RUNTIME시 까지 유지가 되어야 한다??
@Target(ElementType.PARAMETER)  // 이건 또 뭐지?? 파라미터로 받을수 있게 한다??
// 스프링 시큐리티에서 제공하는 어노테이션!
// 이 어노테이션의 매개변수로 현재 인증된 principal을 참조할 수 있다!
// principal은  UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(account.getNicename(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER")));
// 첫번째 인자를 의미한다.
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? null : account")
public @interface CurrentUser {}
