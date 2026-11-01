//package com.jaswanth.fooddeliveryplatform.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.Collections;
//import java.util.List;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//
//    public JwtAuthenticationFilter(JwtService jwtService){
//        this.jwtService = jwtService;
//    }
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    )throws ServletException, IOException {
//
//        String authorizationHeader =
//                request.getHeader("Authorization");
//
//        System.out.println(
//                "Authorization Header: "
//                        + authorizationHeader
//        );
//
//        if (authorizationHeader == null ||
//                !authorizationHeader.startsWith("Bearer ")) {
//
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token =
//                authorizationHeader.substring(7);
//
//
//        if (!jwtService.isTokenValid(token)) {
//            System.out.println("JWT INVALID");
//
//            filterChain.doFilter(request, response);
//            return;
//        }
//        String userId =
//                jwtService.extractUserId(token);
//
//        System.out.println(
//                "JWT VALID - User ID: " + userId
//        );
//
//        List<String> roles = jwtService.extractRoles(token);
//        System.out.println(
//                "Role: " + roles
//        );
//        List<SimpleGrantedAuthority> authorities =
//                roles.stream()
//                        .map(role ->
//                                new SimpleGrantedAuthority(
//                                        "ROLE_" + role
//                                )
//                        )
//                        .toList();
//
//        UsernamePasswordAuthenticationToken authentication =
//                new UsernamePasswordAuthenticationToken(
//                        userId,
//                        null,
//                        authorities
//                );
//        SecurityContextHolder
//                .getContext()
//                .setAuthentication(authentication);
//
//        System.out.println(
//                "Is authenticated: " +
//                        SecurityContextHolder.getContext()
//                                .getAuthentication()
//                                .isAuthenticated()
//        );
//
//        filterChain.doFilter(request, response);
//
//
//
//    }
//
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request) {
//        String path = request.getServletPath();
//        return path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui") || path.equals("/swagger-ui.html");
//    }
//}

package com.jaswanth.fooddeliveryplatform.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/v1/auth/")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        try {
            if (!jwtService.isTokenValid(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String userId = jwtService.extractUserId(token);
            List<String> roles = jwtService.extractRoles(token);

            List<SimpleGrantedAuthority> authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception ex) {
            // Guard against malformed or expired JWT parsing crashes
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}