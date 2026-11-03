//package com.jaswanth.fooddeliveryplatform.security;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//public class SecurityConfig {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    public SecurityConfig(
//            JwtAuthenticationFilter jwtAuthenticationFilter
//    ) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http)
//            throws Exception {
//
//        http
//                .csrf(csrf -> csrf.disable())
//                .sessionManagement(session->
//                        session.sessionCreationPolicy(
//                                SessionCreationPolicy.STATELESS
//                        ))
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/api/v1/auth/register",
//                                "/api/v1/auth/login",
//                                "/api/v1/users/**",
//                                "/v3/api-docs/**",
//                                "/swagger-ui/**",
//                                "/swagger-ui.html"
//                        ).permitAll()
//
//                        .requestMatchers("/api/v1/admin/**")
//                        .hasRole("ADMIN")
//
//                        .requestMatchers("/api/v1/delivery/**")
//                        .hasRole("DELIVERY_PARTNER")
//
//                        .requestMatchers("/api/v1/customer/**")
//                        .hasRole("CUSTOMER")
//
//                        .requestMatchers(HttpMethod.GET, "/api/v1/restaurants/**").permitAll()
//
//                        .requestMatchers(HttpMethod.POST, "/api/v1/payments/webhook").permitAll()
//
//                        .anyRequest().authenticated()
//                )
//                .addFilterBefore(
//                        jwtAuthenticationFilter,
//                        UsernamePasswordAuthenticationFilter.class
//
//                );
//
//        return http.build();
//    }
//
//}

package com.jaswanth.fooddeliveryplatform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity // Enables @PreAuthorize("hasRole(...)") across controllers
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        // 1. Whitelist ALL auth endpoints (register, login, verify-email)
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // 2. Swagger / OpenAPI documentation
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/ws/**",
                                "/api/v1/deliveries/tracking/**",
                                "/api/v1/payments/webhook"
                        ).permitAll()

                        // 3. Actuator probes
                        .requestMatchers("/actuator/**").permitAll()

                        // 4. Public read-only restaurant routes & payment webhooks
                        .requestMatchers(HttpMethod.GET, "/api/v1/restaurants/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/restaurants/{restaurantId}/orders/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/payments/webhook").permitAll()

                        // 5. Role-based routes
                        .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/delivery/**").hasRole("DELIVERY_PARTNER")
                        .requestMatchers("/api/v1/orders/**").hasRole("CUSTOMER")
                        // 6. User endpoints and all others require authentication
                        .requestMatchers("/api/v1/users/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}