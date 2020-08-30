package com.studyolle.studyolle.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

// jpa는 엔티티 정보를 보고 테이블을 만들어준다.
// 따라서 토큰리파지토리에서 사용하는 엔티티 클래스 작성 필요.
@Table(name="persistent_logins")
@Entity
@Getter @Setter
public class PersistentLogins {

    @Id
    @Column(length = 64)
    private String series;

    @Column(nullable = false, length=64)
    private String username;

    @Column(nullable = false, length=64)
    private String token;

    @Column(name="last_used", nullable = false, length=64)
    private LocalDateTime lastUsed;
}