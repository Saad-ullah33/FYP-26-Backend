//package com.propsightai.security;
//
//import io.jsonwebtoken.ExpiredJwtException;
//import io.jsonwebtoken.MalformedJwtException;
//import io.jsonwebtoken.security.SignatureException;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class JwtFilter extends OncePerRequestFilter {
//
//    private final JwtService jwtService;
//    private final CustomUserDetailsService userDetailsService;
//
//    public JwtFilter(
//            JwtService jwtService,
//            CustomUserDetailsService userDetailsService
//    ) {
//        this.jwtService = jwtService;
//        this.userDetailsService = userDetailsService;
//    }
//
//    @Override
//    protected void doFilterInternal(
//            HttpServletRequest request,
//            HttpServletResponse response,
//            FilterChain filterChain
//    ) throws ServletException, IOException {
//
//        String header = request.getHeader("Authorization");
//
//        if (header == null || !header.startsWith("Bearer ")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        String token = header.substring(7);
//
//        try {
//
//            String email = jwtService.extractEmail(token);
//
//            if (email != null &&
//                    SecurityContextHolder.getContext().getAuthentication() == null) {
//
//
//                UserDetails userDetails =
//                        userDetailsService.loadUserByUsername(email);
//
//
//                if (jwtService.isValid(token, userDetails)) {
//
//
//                    UsernamePasswordAuthenticationToken authentication =
//                            new UsernamePasswordAuthenticationToken(
//                                    userDetails,
//                                    null,
//                                    userDetails.getAuthorities()
//                            );
//
//
//                    SecurityContextHolder
//                            .getContext()
//                            .setAuthentication(authentication);
//                }
//            }
//
//        } catch (ExpiredJwtException e) {
//
//            logger.warn("JWT expired");
//
//        }
//        catch (MalformedJwtException e) {
//
//            logger.warn("Malformed JWT");
//
//        }
//        catch (SignatureException e) {
//
//            logger.warn("Invalid signature");
//
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}

package com.propsightai.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtFilter(
            JwtService jwtService, JwtService jwtService1,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService1;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Bypass checks for preflight OPTIONS requests immediately
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            String email = jwtService.extractEmail(token);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    // CRITICAL FIX: Bind the request metadata context into the token container
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    System.out.println("Propsight Auth Status: User " + email + " successfully authenticated.");
                } else {
                    System.out.println("Propsight Auth Warning: Token string is syntactically invalid for user: " + email);
                }
            }

        } catch (ExpiredJwtException e) {
            logger.warn("Propsight Filter Catch: JWT has expired.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token has expired. Please re-login.");
            return; // Terminate filter pipeline early to return a clear 401 instead of a confusing 403

        } catch (MalformedJwtException e) {
            logger.warn("Propsight Filter Catch: Malformed JWT structure.");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Malformed authentication token signature.");
            return;

        } catch (SignatureException e) {
            logger.warn("Propsight Filter Catch: Invalid cryptographic signature.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Cryptographic signature validation failure.");
            return;

        } catch (Exception e) {
            logger.error("Propsight Filter Catch: Unexpected error parsing token stream: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}