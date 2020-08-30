package com.studyolle.studyolle.account;

import com.studyolle.studyolle.domain.Account;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserAccount extends User {
    // User는 스프링 시큐리티의 User임. 그 유저를 확장해서 UserAccount를 만들었다.

    // 인증된 객체 안에는 UserAccount 타입의 객체가 있는데, 그걸 그냥 쓸려면 쓰고,
    // account 도메인 객체로 얻어올려면 이렇게 프로퍼티로 getter를 이용해 꺼내줘야 한다.
    // 만약 여기서 프로퍼티 이름을 바꾸면 CurrentUser에서 얻어오는 프로퍼티 명도 바꿔줘야 함.
    private Account account;

    public UserAccount(Account account) {
        super(account.getNickname(), account.getPassword(), List.of(new SimpleGrantedAuthority("ROLE_USER"))); // 초기화
        this.account = account; // 값 세팅
    }
}