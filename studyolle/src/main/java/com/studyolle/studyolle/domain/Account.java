package com.studyolle.studyolle.domain;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity // 이 어노테이션이 뭐하는거지?
@Getter @Setter // 롬복으로 게터세터 생성?
@EqualsAndHashCode(of="id") // EqualsAndHashCode 메소드 생성할때 아이디만 사용?? 연관관계가 복잡해질때 서로 다른 연관관계를 순환참조 하다가 스택오버플로우 발생 가능.
@Builder    // ??
@AllArgsConstructor // ??
@NoArgsConstructor // ??
public class Account {

    @Id @GeneratedValue  // ???
    private Long id;

    @Column(unique=true)    // 중복된 이메일이 있으면 안됨!
    private String email;

    @Column(unique=true)    // 중복된 닉네임이 있으면 안됨!
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime joinedAt;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location;

    @Lob    // 이 데이터가 대략 varchar(255) 보다 더 크다고 판단할때, @Lob, text 타입의 칼럼으로 맵핑됨., 패칭 타입??? 이건 전혀 모르겠다.
    // 유저를 로딩할때 종종 같이 쓸거기 때문에 패치 모드로 그때그때 가져온다??
    @Basic(fetch = FetchType.EAGER) // ???
    private String profileImage;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByEWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completedSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean isOkSendConfirmMail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }
}