package com.studyolle.studyolle.conifg;

import com.studyolle.studyolle.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity // 내가 시큐리티 설정을 직접 하겠다.
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // WebSecurityConfigurerAdapter 상속받으면 손쉽게 시큐리티 필터 설정 가능

    private final AccountService accountService;
    private final DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/", "/login", "/sign-up", "/check-email-token", "/email-login", "/check-email-login", "/login-link").permitAll() // 여기 해당되는 것들은 무조건 허용, get, post등등
                .mvcMatchers(HttpMethod.GET, "/profile/*").permitAll() // get 요청만 허용
                .anyRequest().authenticated();  // 그 나머지는 로그인 해야만 쓸 수 있다.

        http.formLogin()
                // .usernameParameter("aaa") 이런 형태로 유저네임, 패스워드 변수명을 바꿀 수 있다.  기본값은 username, password
                // .passwordParameter("bbb")
                .loginPage("/login") // 여기서 커스텀 로그인 페이지 안만들면 시큐리티가 기본 제공하는 로그인 페이지에서만 할 수 있다.
                .permitAll();       // 로그인 페이지 접근 권한 설정
                                    // 여기서 post로 /login 설정을 처리해준다. 패스워드도 플레인 텍스트를 인코딩 해줌. 우리가 등록한 인코더로. 따라서 post맵핑 /login을 따로 컨트롤러 안 만들어도 됨.
                                    // 그리고 우리가 등록한 PasswordEncoder, UserDetailsService 타입의 빈이 하나만 있으면 시큐리티에 아무것도 설정할 필요 없음.
                                    // 만약 패스워드 인코더가 여러개 있든지, UserDetailsService 타입의 빈이 하나 이상 있을때는 별도의 설정이 필요하다.
                                    // 별도의 설정이 뭘까?????
                                    // 원래는 어쎈티케이션매니저에 유저 디테일 서비스랑 패스워드 인코더를 설정해주지만, 기본 설정을 그대로 따른다면 따로 설정할 필요가 없음. 만약 인코더 여러개, 디테일 서비스 여러개 일때 별도의 설정 필요.

        http.logout()
                .logoutSuccessUrl("/"); // 로그아웃 성공시 이동할 url

        // 해쉬 기반의 가장 안전하지 않은 방식의 쿠키 제공
        // http.rememberMe().key("ddddd");

        // 가장 안전한 형태의 쿠키 사용법
        http.rememberMe()
                .userDetailsService(accountService)
                .tokenRepository(tokenRepository()); // db에서 토큰값을 읽어오거나 저장하는 인터페이스의 객체, 구현체를 주입해줘야 함.
    }

    // 토큰 리파지토리 구현체인, jdbc토큰 리파지토리, 너무 구체적인 타입 말고,
    // PersistentTokenRepository 인터페이스 타입으로 지정하고
    // 안에서 JdbcTokenRepositoryImpl 구체적인 타입을 쓴다 왜??
    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        // jdbc는 datasource 필요,
        // 우리는 jpa 쓰니까 당연히 빈으로 등록 되어있음. 위에서 생성자로 주입받을 수 있다.
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 정적 리소스에 대해 시큐리티 적용 안함.
        web.ignoring()
                .mvcMatchers("/node_modules/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }
}
