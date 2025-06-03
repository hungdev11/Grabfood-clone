package com.api.jwt;

import com.api.entity.Account;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.AccountRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtService {

    private final AccountRepository accountRepository;

    public static final String SECRET = "38wdW5saWgsZo7vBE/IXrx5dsl4iNjcjIXx93PcIIDg=";

    public String generateToken(String username) { // Use username
        Map<String, Object> claims = new HashMap<>();
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_PASSWORD_NOT_MATCH));
        claims.put("role", account.getRole().getRoleName());
        long accountId = account.getUser() == null ? 1L : account.getUser().getId();
        return accountId + "#" + createToken(claims, username);
    }

    /**
     * Extract pure JWT token from combined format: accountId#jwt
     * 
     * @param fullToken Combined token with format: accountId#jwt
     * @return Pure JWT token
     */
    public String extractJwtFromToken(String fullToken) {
        if (fullToken == null || fullToken.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_KEY, "Token không được để trống");
        }

        if (fullToken.contains("#")) {
            String[] parts = fullToken.split("#", 2);
            if (parts.length == 2) {
                return parts[1]; // Return JWT part
            }
        }

        // If no "#" found, assume it's already pure JWT
        return fullToken;
    }

    /**
     * Extract account ID from combined token format: accountId#jwt
     * 
     * @param fullToken Combined token with format: accountId#jwt
     * @return Account ID
     */
    public Long extractAccountIdFromToken(String fullToken) {
        if (fullToken == null || fullToken.trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_KEY, "Token không được để trống");
        }

        if (fullToken.contains("#")) {
            String[] parts = fullToken.split("#", 2);
            if (parts.length == 2) {
                try {
                    return Long.parseLong(parts[0]); // Return account ID part
                } catch (NumberFormatException e) {
                    throw new AppException(ErrorCode.INVALID_KEY, "Account ID trong token không hợp lệ");
                }
            }
        }

        throw new AppException(ErrorCode.INVALID_KEY, "Token format không đúng, cần có format: accountId#jwt");
    }

    private String createToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 3000))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String fullToken) {
        String jwtToken = extractJwtFromToken(fullToken);
        return extractClaim(jwtToken, Claims::getSubject);
    }

    public Date extractExpiration(String fullToken) {
        String jwtToken = extractJwtFromToken(fullToken);
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String fullToken, UserDetails userDetails) {
        String jwtToken = extractJwtFromToken(fullToken);
        final String username = extractClaim(jwtToken, Claims::getSubject);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(jwtToken));
    }

    /**
     * Validate token format and extract both account ID and JWT
     * 
     * @param fullToken Combined token
     * @return true if token format is valid
     */
    public Boolean isValidTokenFormat(String fullToken) {
        try {
            extractAccountIdFromToken(fullToken);
            extractJwtFromToken(fullToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
