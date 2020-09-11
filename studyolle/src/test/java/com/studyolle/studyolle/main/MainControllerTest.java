package com.studyolle.studyolle.main;

import com.studyolle.studyolle.account.AccountRepository;
import com.studyolle.studyolle.account.AccountService;
import com.studyolle.studyolle.account.SignUpForm;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// @RunWith(...) , ExtendWith 이런서 junit5 부터 안써도 됨.
// ExtendWith 이미 스프링부트 테스트에 달려있음.
// junit5 애노테이션들은 메타애노테이션으로 사용될수 있다?? 이게 뭔말이지??
// 써드파티 junit5 지원하는? 이런거 쓸거면 ExtendWith 쓰겠지만, 스프링 부트 기반 테스트 에서는 필요 없음.
@SpringBootTest
@AutoConfigureMockMvc
//@RequiredArgsConstructor
class MainControllerTest {

//  private final MockMvc mockMvc;
//  junit5 가 di를 지원해준다. 타입이 정해져있는데,
//  위의 방식은 안된다.
// 스프링이 주입 못하고 junit이 다른 인스턴스를 넣으려고 시도하기 때문
// -> @Autowired 사용하도록 아래처럼.

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;


    // 사실 이렇게 모든 코드가 똑같은건 junit5가 제공하는 파라미터라이즈드 테스트(?)로 하면 좋은데, 일단 2개 정도는..

    @BeforeEach // 모든 테스트가 시작되기 전에 실핼되도록
    void beforeEach(){
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setEmail("jwkoo88@gmail.com");
        signUpForm.setNickname("molt");
        signUpForm.setPassword("123456789");
        accountService.processNewAccount(signUpForm);
    }

    @AfterEach  // 모든 테스트가 끝나고 나서.
    void afterEach(){
        accountRepository.deleteAll();
    }

    @DisplayName("이메일 로그인 성공")
    @Test
    void login_with_email() throws Exception {

        mockMvc.perform(post("/login")
                .param("username", "jwkoo88@gmail.com")
                .param("password", "123456789")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("molt"));
    }

    @DisplayName("닉네임 로그인 성공")
    @Test
    void login_with_nickname() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "molt")
                .param("password", "123456789")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("molt"));
    }

    @DisplayName("로그인 실패")
    @Test
    void login_fail() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "0000000000")
                .param("password", "126789")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @WithMockUser   // 실제 유저가 있는것 마냥, user, password
    @DisplayName("로그아웃")
    @Test
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }
}