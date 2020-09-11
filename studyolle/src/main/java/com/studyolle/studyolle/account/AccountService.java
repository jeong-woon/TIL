package com.studyolle.studyolle.account;

import com.studyolle.studyolle.domain.Account;
import com.studyolle.studyolle.settings.Profile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    // 이렇게 받으려면 콘솔 메일 센더는 왜 만든거지??
    private final JavaMailSender javaMailSender;
    // 이걸 어떻게 여기 이 이름으로 가져다 쓰는거지???
    private final PasswordEncoder passwordEncoder;
    // 이 빈은 스프링 시큐리티에서 등록돼 있는데 이걸 이렇게 주입 받으려면 설정을 바꿔야함.
//    private final AuthenticationManager AuthenticationManager;

    // 중요한 문제가 있다.
    // saveNewAccount 에서 save 안에서는 트랜잭션이 적용이 되고, 퍼시스트 상태로 적용되는데,
    // save가 끝나면 processNewAccount에서 newAccount가 트랜잭션을 벗어나 detached 상태가 되기 때문에 db에 싱크가 안됨.
    // processNewAccount에 @Transactional을 붙여놔야 계속 트랜잭션 안에 있기 때문에 퍼시스트 상태 유지. 퍼시스트 상태 객체는 트랜잭션이 종료될때 상태를 db에 싱크함.
    //@Transactional
    public Account processNewAccount(SignUpForm signUpForm) {
        Account newAccount = saveNewAccount(signUpForm);
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount(@Valid SignUpForm signUpForm) {

        Account account = Account.builder()
                .email(signUpForm.getEmail())
                .nickname(signUpForm.getNickname())
                .password(passwordEncoder.encode(signUpForm.getPassword()))
                .studyCreatedByWeb(true)
                .studyEnrollmentResultByEWeb(true)
                .studyUpdatedByWeb(true)
                .build();

        // 회원가입
        return accountRepository.save(account);
    }

    public void sendSignUpConfirmEmail(Account newAccount) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(newAccount.getEmail());
        mailMessage.setSubject("스터디 올레, 회원가입 인증");
        mailMessage.setText("/check-email-token?token="+newAccount.getEmailCheckToken()+"&email=" + newAccount.getEmail());
        javaMailSender.send(mailMessage);
    }

    public void autoLogin(Account account) {
        // 스프링 시큐리티에서는 시큐리티 컨텍스트 홀더가 있음. 얘가 컨텍스트를 들고 있는데 그 안에 setAuthentication() 해주면 로그인됨.
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account)                                        // principal, new UserAccount(account)로 넣으면 인증된 객체로 UserAccount 타입을 받아올수 있다.
                , account.getPassword()                                         // password
                , List.of(new SimpleGrantedAuthority("ROLE_USER"))        // 계정이 가진 권한.
        );
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);

        // 사실 위의 방법은 정석은 아님. UsernamePasswordAuthenticationToken 주석을 읽어보면 사실 AuthenticationManager 안에서 쓰라고 만들어 둔 생성자임.
        // 원래 폼 인증에서 쓰는 방법은 사용자가 보낸 id, pw를 가지고 AuthenticationManager를 통해 인증을 거친 토큰을 넣어줘야 함. 아래 처럼
        // 근데 우리는 평문 패스워드를 받아올 방법이 없음. 저장도 안하니까. 위의 방법으로 함.
        // AuthenticationManager는 스프링 시큐리티에 빈으로 등록이 되어있음. 근데 노출이 안되서 주입을 못받아 온다? 이게 무슨 의미지??
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(account.getNickname(), account.getPassword());
//        Authentication authentication = AuthenticationManager.authenticate(token);
//        SecurityContext context = SecurityContextHolder.getContext();
//        context.setAuthentication(authentication);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String emailOrNickname) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(emailOrNickname);
        if(account == null) {
            account = accountRepository.findByNickname(emailOrNickname);
        }

        if(account == null) {
            throw new UsernameNotFoundException(emailOrNickname);
        }

        return new UserAccount(account);
    }

    public void completedSignUp(Account account) {
        // 여기서 어카운트는 퍼시스트 상태임 영속성 컨텍스트가 이미 만들어진 상태에서 트랜잭션에서 들어감.
        account.completedSignUp();
        autoLogin(account);
    }

    public void updateProfile(Account account, Profile profile) {
        // 여기 어카운트는 세션에 넣어뒀던 값을 받아서 온것임.
        // 트랜잭션이 끝난지 오래임. detached
        account.setUrl(profile.getUrl());
        account.setBio(profile.getBio());
        account.setOccupation(profile.getOccupation());
        account.setLocation(profile.getLocation());
        account.setProfileImage(profile.getProfileImage());

        // .save를 호출하면 save 구현체 안에서 id 값이 있는지 확인하고 있으면 머지한다.
        // 지금 내용으로. 업데이트 발생
        accountRepository.save(account);
    }
}
