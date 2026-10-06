package com.hope.escala.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.hope.escala.security.jwt.JwtAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						// Rotas Públicas / Swagger
						.requestMatchers("/auth/**").permitAll()
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
						.requestMatchers(HttpMethod.GET, "/empresas-publicas").permitAll()
						.requestMatchers(HttpMethod.GET, "/empresas", "/empresas/**").permitAll()
						.requestMatchers("/perfil/**").permitAll()
						.requestMatchers("/usuarios/meu-perfil").authenticated()
						.requestMatchers("/usuarios/**").permitAll()
						.requestMatchers("/auth/login", "/auth/solicitar-cadastro").permitAll()

						// Disponibilidades (Voluntários e Admins)
						.requestMatchers("/disponibilidades/**").authenticated()
						.requestMatchers(HttpMethod.GET, "/disponibilidades/**").hasAnyRole("ADMIN", "SUPER_ADMIN", "VOLUNTARIO", "USER", "MUSICO")
						.requestMatchers("/agenda-mensal/datas").authenticated()

						// 🟢 Liberação de Leitura (GET) para Escalas, Músicas e Playlists para qualquer membro autenticado
						.requestMatchers(HttpMethod.GET, "/escalas", "/escalas/**").authenticated()
						.requestMatchers(HttpMethod.GET, "/escala-musicos", "/escala-musicos/**").authenticated()
						.requestMatchers(HttpMethod.GET, "/playlists", "/playlists/**").authenticated()
						.requestMatchers(HttpMethod.GET, "/musicas", "/musicas/**").authenticated()

						.anyRequest().authenticated());

		http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		
		configuration.setAllowedOriginPatterns(Arrays.asList(
				"https://hope-escala-web.onrender.com",
				"https://hopeescalapro.com.br",
				"https://www.hopeescalapro.com.br",
				"https://api.hopeescalapro.com.br",
				"https://*.onrender.com",
				"http://localhost:8081",
				"http://localhost:19006",
				"http://localhost:5173",
				"http://localhost:3000",
				"http://localhost:8080",
				"http://172.18.73.28:8090",
				"http://172.18.73.28:5173"
		));
		
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With", "Origin"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
}
