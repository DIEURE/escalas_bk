package com.hope.escala.security.jwt.JwtService;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hope.escala.entity.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	// private static final String SECRET_KEY =
	// "HopeEscalaSistemaJWT2026MinhaChaveSuperSecretaComMaisDe32Caracteres";

	// private static final long EXPIRATION = 1000 * 60 * 60 * 24;

	@Value("${jwt.secret}")
	private String secret;

	@Value("${jwt.expiration}")
	private Long expiration;

	private Key getSignKey() {
		return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public String gerarToken(Usuario usuario) {

	    return Jwts.builder()
	            .setSubject(usuario.getEmail())

	            .claim("id", usuario.getId())
	            .claim("nome", usuario.getNome())
	            .claim("perfil", usuario.getPerfil().name())

	            .setIssuedAt(new Date())

	            .setExpiration(
	                    new Date(System.currentTimeMillis() + expiration)
	            )

	            .signWith(getSignKey(), SignatureAlgorithm.HS256)

	            .compact();
	}

	public String extrairEmail(String token) {

		return extrairClaims(token).getSubject();
	}

	public boolean tokenValido(String token) {

		try {

			extrairClaims(token);

			return true;

		} catch (Exception e) {

			return false;
		}
	}

	private Claims extrairClaims(String token) {

		return Jwts.parserBuilder()

				.setSigningKey(getSignKey())

				.build()

				.parseClaimsJws(token)

				.getBody();
	}
}
