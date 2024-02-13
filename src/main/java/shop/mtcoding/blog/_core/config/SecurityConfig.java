package shop.mtcoding.blog._core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;


@Configuration  //컴퍼넌트 스캔
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder(); //IoC 등록, 시큐리티가 로그인할때 어떤 해시로 비교해야하는지 알게됨
    }

    @Bean
    public WebSecurityCustomizer ignore() { //정적 자원만 security filter에서 제외하는 것!
        return w -> w.ignoring().requestMatchers("/static/**", "/h2-console/**");
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http.csrf(c -> c.disable());

        http.authorizeHttpRequests(a -> {

            a.requestMatchers(RegexRequestMatcher.regexMatcher("/board/\\d+")).permitAll()
                    .requestMatchers("/user/**", "/board/**").authenticated()
                    .anyRequest().permitAll();
        });


        http.formLogin(f -> {
            f.loginPage("/loginForm").loginProcessingUrl("/login").defaultSuccessUrl("/").failureUrl("/loginForm");
        });

        return http.build();

    }
}
