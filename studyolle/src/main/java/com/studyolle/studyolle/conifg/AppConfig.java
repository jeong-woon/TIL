package com.studyolle.studyolle.conifg;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        // bcrypt 알고리즘 사용해서 비밀번호 해슁
        // 빈을 설정하고 가져다 쓰는것 학습이 필요하다. 이걸 어떻게 가져다 쓰는지 이해가 안됨.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
