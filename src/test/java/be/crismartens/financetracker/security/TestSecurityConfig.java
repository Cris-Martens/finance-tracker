package be.crismartens.financetracker.security;

import be.crismartens.financetracker.auth.MyUserDetailsService;
import be.crismartens.financetracker.auth.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Profile("test")
public class TestSecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final MyUserDetailsService myUserDetailsService;

    @Autowired
    public TestSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                              MyUserDetailsService myUserDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Bean
    @Primary
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(CsrfConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden")))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(
                                "/", "/home",
                                "/api/v1/register", "/register",
                                "/auth/login", "/auth/**", "/login"
                            ).permitAll()
                        .requestMatchers(
                                "/api/v1/user/**",
                                        "/user/**",
                                        "/api/v1/budget/",
                                        "/api/v1/budget/**",
                                        "/budget",
                                        "/api/v1/dashboard/**",
                                        "/api/v1/accountinfo",
                                        "/api/v1/accountinfo/**",
                                        "/Api/v1/delete")
                                .hasAuthority("ROLE_USER")
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

    @Bean
    @Primary
    PasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return myUserDetailsService;
    }

    @Bean
    @Primary
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(testPasswordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    @Primary
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
