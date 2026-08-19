package com.hope.escala.security.jwt.JwtAuthenticationFilter;


import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hope.escala.security.jwt.JwtService.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
    		
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = recuperarToken(request);

        System.out.println(">>> JwtAuthenticationFilter");
        
        if (token != null && jwtService.tokenValido(token)) {

            String email = jwtService.extrairEmail(token);

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(email);
            
            System.out.println("Email: " + email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
            
            System.out.println("Authorities: " + userDetails.getAuthorities());

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request));

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);
        }
        
        System.out.println("Usuário autenticado com sucesso");

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {

        String bearer = request.getHeader("Authorization");

        if (StringUtils.hasText(bearer)
                && bearer.startsWith("Bearer ")) {

            return bearer.substring(7);
        }

        return null;
    }
}

