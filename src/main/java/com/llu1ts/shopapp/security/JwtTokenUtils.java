package com.llu1ts.shopapp.security;


import com.llu1ts.shopapp.entity.User;
import com.llu1ts.shopapp.exception.AuthorizationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtils {

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.secret-key}")
    private String secretKey;


    public String generateToken(User user) throws AuthorizationException {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", user.getUsername());
        claims.put("phoneNumber", user.getPhoneNumber());
        claims.put("password", user.getPassword());
        claims.put("role", user.getRole().getName());

        try {
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getPhoneNumber())
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                    .signWith(getSignKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new AuthorizationException("Bad credentials");
        }
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //check het han

    public boolean isTokenExpired(String token) {
        try {
            Date expirationDate = extractClaims(token, Claims::getExpiration);
            return expirationDate.before(new Date());
        } catch (Exception e) {
            // Token không lấy ra được, không dịch được,... Thì tính là hết hạn
            return true;
        }
    }


    // lay ra thoi gian het han token
    public Date getExpirationDate(String token) {
        return extractClaims(token, Claims::getExpiration);
    }

    //lay ra username(phone number)
    public String getUsername(String token) {
        return extractClaims(token, Claims::getSubject);
    }

    //lay ra username(phone number)
    public String getUserId(String token) {
        return extractClaims(token, claims -> claims.get("id")).toString();
    }

    // Check token có thuộc về user nào không
    public boolean validateToken(String token, UserDetails userDetails) {
        String username = getUsername(token);
        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }

    //check admin
    public Boolean isAdmin(String token) {
        String role = getRole(token);
        return role.toLowerCase().equals("admin");
    }

    //get Role
    public String getRole(String token) {
        return extractClaims(token, claims -> claims.get("role")).toString();
    }
}
