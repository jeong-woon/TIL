package com.studyolle.studyolle.settings;

import com.studyolle.studyolle.WithAccount;
import com.studyolle.studyolle.account.AccountRepository;
import com.studyolle.studyolle.domain.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    // WithAccount 쓸때마다 User를 실제로 생성해서 db에 넣기 때문에
    // 매 테스트 후에 유저 정보를 지워주면 문제 없음.
    @AfterEach
    void afterEach(){
        accountRepository.deleteAll();
    }

    // @WithUserDetails(value="molt", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    // 디비에 있는 실제 user정보를 조회해서 쓴다.
    // 이거 버그라 제대로 동작 안함. before 전에 이게 먼저 실행되서 실패함.
    @WithAccount("molt") // 커스텀 어노테이션임
    @DisplayName("프로필 수정하기 화면")
    @Test
    void updateProfileForm() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(get("/settings/profile"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(flash().attributeExists("profile"));

        Account molt = accountRepository.findByNickname("molt");
        assertEquals(bio, molt.getBio());
    }

    @WithAccount("molt")
    @DisplayName("프로필 수정하기 - 입력값 정상")
    @Test
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우.";
        mockMvc.perform(post("/settings/profile")
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/settings/profile"))
                .andExpect(flash().attributeExists("message"));

        Account molt = accountRepository.findByNickname("molt");
        assertEquals(bio, molt.getBio());
    }

    @WithAccount("molt")
    @DisplayName("프로필 수정하기 - 입력값 오류")
    @Test
    void updateProfile_error() throws Exception {
        String bio = "길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우. 너무너무 길게 소개를 수정하는 경우. 길게 소개를 수정하는 경우.";
        mockMvc.perform(post("/settings/profile")
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("settings/profile"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors())
        ;

        Account molt = accountRepository.findByNickname("molt");
        assertNull(molt.getBio());
    }
}