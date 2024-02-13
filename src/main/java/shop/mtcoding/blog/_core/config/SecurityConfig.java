package shop.mtcoding.blog._core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  //컴퍼넌트 스캔
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer ignore() {
        return w -> w.ignoring().requestMatchers("/board/*", "/static/**", "/h2-console/**");
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http.csrf(c -> c.disable());

        http.authorizeHttpRequests(a -> {
            //이 페이지는 인증이 필요해! 인증 없으면 못 들어감!
            a.requestMatchers("/user/updateForm", "/board/**").authenticated()
                    .anyRequest().permitAll();
        });

        http.formLogin(f -> {
            f.loginPage("/loginForm").loginProcessingUrl("/login").defaultSuccessUrl("/").failureUrl("/loginForm");
        });

        return http.build();

    }
}
