package com.rslakra.automobile.service.security.config;

import com.rslakra.automobile.service.security.PasswordAuthenticationFilter;
import com.rslakra.automobile.service.security.UserDetailsAuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class HttpSecurityConfigurer extends AbstractHttpConfigurer<HttpSecurityConfigurer, HttpSecurity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSecurityConfigurer.class);

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsService userDetailsService;

    public HttpSecurityConfigurer() {
        LOGGER.debug("HttpSecurityConfigurer()");
    }

    /**
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        LOGGER.debug("+configure({})", http);
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        http.addFilterBefore(authenticationFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class);
        LOGGER.debug("-configure()");
    }

    /**
     * @return
     */
    public static HttpSecurityConfigurer httpSecurityConfigurer() {
        return new HttpSecurityConfigurer();
    }

    /**
     * Completely ignore H2 console from Spring Security
     * 
     * @return
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        LOGGER.debug("webSecurityCustomizer()");
        return (web) -> web.ignoring().requestMatchers("/h2/**");
    }

    /**
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        LOGGER.debug("filterChain({})", http);
        /* Allow frames only with the same origin, which is much more safe. */
        http
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
            /* Ignore only h2 csrf, spring-security 6. */
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2/**"))
            /* Authorization */
            .authorizeHttpRequests(auth -> auth
                /* H2 Console - must be first */
                .requestMatchers("/h2/**")
                .permitAll()
                /* Testing API - remove it in production */
                .requestMatchers("/auth/**", "/rest/**")
                .permitAll()
                /* Static resources */
                .requestMatchers("/css/**", "/js/**", "/*.html", "/resources/**")
                .permitAll()
                /* Public pages */
                .requestMatchers("/", "/home", "/services/**", "/schedules/**")
                .permitAll()
                /* Login and Register pages */
                .requestMatchers("/login", "/login/**", "/register", "/register/**")
                .permitAll()
                /* Hidden Admin Registration */
                .requestMatchers("/admin/register")
                .permitAll()
                /* Error page */
                .requestMatchers("/error")
                .permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("userName")
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }

    /**
     * @param authenticationManager
     * @return
     * @throws Exception
     */
    public PasswordAuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager) {
        LOGGER.debug("+authenticationFilter({})", authenticationManager);
        PasswordAuthenticationFilter passwordAuthFilter = new PasswordAuthenticationFilter(passwordEncoder);
        passwordAuthFilter.setAuthenticationManager(authenticationManager);
        passwordAuthFilter.setAuthenticationFailureHandler(failureHandler());
        // Set the login URL so the filter only processes login requests
        passwordAuthFilter.setFilterProcessesUrl("/login");
        LOGGER.debug("-authenticationFilter(), passwordAuthFilter: {}", passwordAuthFilter);
        return passwordAuthFilter;
    }

    /**
     * @param auth
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        LOGGER.debug("configureGlobal({})", auth);
        auth.authenticationProvider(authProvider());
    }

    /**
     * @return
     */
    public AuthenticationProvider authProvider() {
        LOGGER.debug("authProvider()");
        AuthenticationProvider
            authProvider =
            new UserDetailsAuthenticationProvider(passwordEncoder, userDetailsService);

        return authProvider;
    }

    /**
     * @return
     */
    public SimpleUrlAuthenticationFailureHandler failureHandler() {
        LOGGER.debug("failureHandler()");
        return new SimpleUrlAuthenticationFailureHandler("/login?error=true");
    }

}
