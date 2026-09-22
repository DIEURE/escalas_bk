package com.hope.escala.security.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = recuperarToken(request);

        if (token != null && jwtService.tokenValido(token)) {
            String email = jwtService.extrairEmail(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

         // Dentro de doFilterInternal, após carregar o userDetails:
            List<GrantedAuthority> authorities = new ArrayList<>();

            if (userDetails != null && userDetails.getAuthorities() != null) {
                for (GrantedAuthority auth : userDetails.getAuthorities()) {
                    String role = auth.getAuthority();
                    if (role != null && !role.isBlank()) {
                        if (role.startsWith("ROLE_")) {
                            authorities.add(new SimpleGrantedAuthority(role));                  // Mantém ROLE_ADMIN
                            authorities.add(new SimpleGrantedAuthority(role.substring(5)));     // Adiciona ADMIN limpo
                        } else {
                            authorities.add(new SimpleGrantedAuthority(role));                  // Mantém ADMIN
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));        // Adiciona ROLE_ADMIN
                        }
                    }
                }
            }

            // Imprime para conferência:
            System.out.println(">>> Autenticado: " + email + " | Authorities finais: " + authorities);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    authorities // 👈 Aqui agora vai conter [ROLE_ADMIN, ADMIN]
            );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String recuperarToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
