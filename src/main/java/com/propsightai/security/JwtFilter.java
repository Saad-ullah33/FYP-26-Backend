package com.propsightai.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    // Fixed the duplicate constructor parameter to ensure flawless Spring Autowiring
    public JwtFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
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

                    // ── ROLE PREFIX FIX ──
                    // If your authorities list looks like ["ADMIN"], this maps it to ["ROLE_ADMIN"] dynamically
                    // so that your @PreAuthorize("hasRole('ADMIN')") annotations authorize successfully.
                    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities().stream()
                            .map(auth -> {
                                String role = auth.getAuthority();
                                if (!role.startsWith("ROLE_")) {
                                    return new SimpleGrantedAuthority("ROLE_" + role);
                                }
                                return auth;
                            })
                            .collect(Collectors.toList());

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    authorities // Pass the corrected authority set here
                            );

                    // Bind request metadata context into token container
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    System.out.println("Propsight Auth Status: User " + email + " successfully authenticated with authorities: " + authorities);
                } else {
                    System.out.println("Propsight Auth Warning: Token string is syntactically invalid for user: " + email);
                }
            }

        } catch (ExpiredJwtException e) {
            logger.warn("Propsight Filter Catch: JWT has expired.");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT Token has expired. Please re-login.");
            return;

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