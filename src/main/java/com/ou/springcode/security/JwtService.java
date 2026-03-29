package com.ou.springcode.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.ou.springcode.config.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
    private final JwtProperties jwtProperties;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    /**
     * Tạo khóa ký HMAC SHA từ secret trong JwtProperties.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Tạo access token cho user với thời gian hết hạn từ JwtProperties.
     */
    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(
            userDetails.getUsername(),
            jwtProperties.getAccessTokenExpirationMs()
        );
    }

    /**
     * Tạo refresh token cho user với thời gian hết hạn từ JwtProperties.
     */
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(
            userDetails.getUsername(),
            jwtProperties.getRefreshTokenExpirationMs()
        );
    }

    /**
     * Xây dựng JWT với subject, thời gian phát hành, thời gian hết hạn và ký bằng khóa bí mật.
     */
    private String buildToken(String subject, long expirationMs) {
        return Jwts.builder()
        .subject(subject)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expirationMs))
        .signWith(getSigningKey())
        .compact();
    }

    /**
     * Trích xuất username (subject) từ JWT token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Trích xuất thời gian hết hạn từ JWT token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Trích xuất một claim cụ thể từ JWT token bằng cách sử dụng hàm resolver.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResoler) {
        Claims claims = extractAllClaims(token);
        return claimsResoler.apply(claims);
    }

    /**
     * Trích xuất tất cả claims từ JWT token sau khi verify chữ ký.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
    }

    /**
     * Kiểm tra xem JWT token có hết hạn hay không.
     */
    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate JWT token với UserDetails: kiểm tra username khớp và token chưa hết hạn.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
