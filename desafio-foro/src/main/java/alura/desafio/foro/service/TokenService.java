package alura.desafio.foro.service;

import alura.desafio.foro.domain.usuario.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {

    @Value("${jwt.secret:12345678}")
    private String secretOrKey;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    public String generarToken(Usuario usuario) {
        try {

            SecretKey key = Keys.hmacShaKeyFor(secretOrKey.getBytes());

            Instant expira = generarFechaExpiracion();

            String token = Jwts.builder()
                    .setIssuer("foro_hub")
                    .setSubject(usuario.getEmail())
                    .claim("id", usuario.getId())
                    .setIssuedAt(new Date())
                    .setExpiration(Date.from(expira))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();

            return token;
        } catch (Exception exception) {

            throw new RuntimeException("Error al generar el token JWT", exception);
        }
    }

    public String getSubject(String token) {
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Token JWT inválido o no proporcionado");
        }
        try {
            SecretKey key = Keys.hmacShaKeyFor(secretOrKey.getBytes());

            Jws<Claims> jws = Jwts.parserBuilder()
                    .requireIssuer("foro_hub")
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            String subject = jws.getBody().getSubject();

            if (subject == null) {
                throw new RuntimeException("Token JWT inválido: no se encontró el 'subject'");
            }
            return subject;

        } catch (Exception exception) {
            throw new RuntimeException("Token JWT inválido o expirado", exception);
        }
    }
    private Instant generarFechaExpiracion() {

        return LocalDateTime.now().plusSeconds(expiration / 1000)
                .atOffset(ZoneOffset.UTC).toInstant();
    }
}