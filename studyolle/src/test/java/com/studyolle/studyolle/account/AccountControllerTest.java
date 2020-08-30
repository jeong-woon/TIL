package com.studyolle.studyolle.account;

import com.studyolle.studyolle.domain.Account;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private JavaMailSender javaMailSender;

    // @Autowired랑 @MockBean의 차이는 뭘까???

    @DisplayName("회원가입 화면 노출 테스트")
    @Test
    void signUpForm() throws Exception {
        mockMvc.perform(get("/sign-up"))
                // html 렌더링도 확인 가능 타임리프 이기때문에 가능, 서블릿 컨테이너가 렌더링 하지 않음.
                // @SpringBootTest(WebEnvironment = 랜덤포트 또는 디파인 포트) 하면 실제 서블릿 컨테이너가 뜨는데, 이때 목mvc나 웹 테스트 클라이언트, 웹 클라이언트 띄워서 테스트하면 됨.
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
                .andExpect(model().attributeExists("signUpForm"))
                .andExpect(unauthenticated()) // 로그인 되어있는지
                // 스프링 시큐리티가 있는 mockMvc와 없는 mockMvc는 다르다 그 이유는 스프링 부트가 설정으로 여러가지를 해준다. ex) csrf같은 기능
        ;
    }

    @DisplayName("회원가입 처리 - 입력값 오류")
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {
        mockMvc.perform(post("/sign-up")
            .param("nickname", "jeongwoon")
            .param("email", "email...")
            .param("password", "12345")
            .with(csrf()))
                // 200이 나올거 같은데?? 이 요청이 403? 왜 나오는거지?? csrf(시큐리티에서 설명), 타 사이트에서 내 사이트를 대상으로 폼 데이터를 보내는것을 방지하는 기술
                // 타임리프, 스프링 시큐리티, 스프링mvc 조합으로 csrf 토큰 기능을 지원해준다. 이미 화면에 히든 값으로 csrf 토큰이 들어있다. 이 토큰을 보고 서버에서 내가 만든 데이터인지 판단한다
                // 만약에 csrf 토큰이 제공 안되거나 다르면 시큐리티에서 퍼밋 올 해줬어도 안전하지 않은 요청이라고 판단해서 튕겨낸다.
                // 인증하지 않고 사용해도 되지만, 안전하지 않은 요청까지 받아들이지 않음. 시큐리티
                // 테스트시 폼을 보내는 테스트시 csrf 토큰 넣어주려면 .with(csrf()) 해주자.
            .andExpect(status().isOk())
            .andExpect(view().name("account/sign-up"))
            .andExpect(unauthenticated()) // 로그인 되어있는지
        ;
    }

    @DisplayName("회원가입 처리 - 입력값 정상")
    @Test
    void signUpSubmit_with_correct_input() throws Exception {
        mockMvc.perform(post("/sign-up")
                    .param("nickname", "jeongwoon")
                    .param("email", "jwkoo88@gmail.com")
                    .param("password", "123456789")
                    .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(authenticated());

        Account account = accountRepository.findByEmail("jwkoo88@gmail.com");
        assertNotNull(account);
        assertNotEquals(account.getPassword(), "123456789");
        assertNotNull(account.getEmailCheckToken());
        // assertTrue가 뭐지??
        assertTrue(accountRepository.existsByEmail("jwkoo88@gmail.com"));

        // then, should send???
        // SimpleMailMessage 타입의 아무 인스턴스나 가지고 send 라는 메소드가 호출 됐는지 검사하는거라고 함...
        // 사실 메일 센더 같은건 개발자가 인터페이스만 관리하고, 실제 발송하는건 외부 서비스다 이거까지 테스트 하기에는...
        // 그리고 테스트를 그렇게 깊게 짜두면, 나중에 코드를 수정하기가 힘들다.
        then(javaMailSender).should().send(any(SimpleMailMessage.class));
    }

    @DisplayName("인증메일 확인 - 입력값 오류")
    @Test
    void checkEmailToken_with_wrong_input() throws Exception {
        mockMvc.perform(get("/check-email-token")
                .param("token", "asdfasdfadsddd")
                .param("email", "jwkoo88@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("account/checked-email"))
        .andExpect(unauthenticated());
    }

    @DisplayName("인증메일 확인 - 입력값 정상")
    @Test
    void checkEmailToken_with_correct_input() throws Exception {
        Account account = Account.builder()
                .email("jwkoo88@gmail.com")
                .nickname("molt")
                .password("123456789")
                .build();
        Account newAccount = accountRepository.save(account);
        newAccount.generateEmailCheckToken();

        mockMvc.perform(get("/check-email-token")
                .param("token", newAccount.getEmailCheckToken())
                .param("email", newAccount.getEmail()))
                .andExpect(status().isOk())
                .andExpect(model().attributeDoesNotExist("error"))
                .andExpect(model().attributeExists("nickname"))
                .andExpect(model().attributeExists("numberOfUser"))
                .andExpect(view().name("account/checked-email"))
        .andExpect(authenticated().withUsername("molt"));
        // 위 처럼 인증정보의 검증도 가능함.
    }
}