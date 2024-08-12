package effective.mobile.com.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry ->
                authorizationManagerRequestMatcherRegistry
                    .requestMatchers("/auth/**").permitAll()
                    .requestMatchers(HttpMethod.POST, "/user").permitAll()
                    .requestMatchers("/actuator/**","/v2/**","/mypath/**","/swagger.yml","/anypath/").permitAll()
                    .requestMatchers("/user**").authenticated()
                    .requestMatchers("/task**").authenticated()
                    .requestMatchers("/comment**").authenticated().anyRequest().permitAll()
            )
            .httpBasic(Customizer.withDefaults())
            .sessionManagement(httpSecuritySessionManagementConfigurer ->
                httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}