package com.mall.util;

import io.jsonwebtoken.Claims;
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
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.equals("/api/login") || path.equals("/api/register") ||
            path.startsWith("/api/stats") || path.startsWith("/api/ai") ||
            path.startsWith("/h2-console")) {
            chain.doFilter(request, response);
            return;
        }

        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            try {
                Claims claims = jwtUtil.parse(auth.substring(7));
                var authToken = new UsernamePasswordAuthenticationToken(
                    claims, null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + claims.get("role").toString().toUpperCase())));
                authToken.setDetails(claims);
                SecurityContextHolder.getContext().setAuthentication(authToken);
                request.setAttribute("user", claims);
            } catch (Exception ignored) {}
        }
        chain.doFilter(request, response);
    }
}
