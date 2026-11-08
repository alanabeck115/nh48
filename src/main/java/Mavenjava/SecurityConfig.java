
package Mavenjava;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/index.html",
                    "/mountains", "/users", "/hikes",
                    "/my-hikes",
                    "/my-hikes/edit",
                    "/my-hikes/update",
                    "/my-hikes/delete",
                    "/hike-entry", "/hike-entry/save",
                    "/css/**", "/js/**"
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.permitAll());

        return http.build();
    }
}
