package org.example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {

//    public final static long TOKEN_VALIDATION_SECOND = 1000L  * 60 * 60;  //60 minute
    public final static long TOKEN_VALIDATION_SECOND = 1000L * 60 * 60;
    public final static long REFRESH_TOKEN_VALIDATION_SECOND = 1000L * 60 * 60 * 8; //8 hour

    final static public String ACCESS_TOKEN_NAME = "accessToken";
    final static public String REFRESH_TOKEN_NAME = "refreshToken";

    @Value("${secret.jwt.key}")
    private String SECRET_KEY;

    private ObjectMapper objectMapper;

    public JwtUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    public String doGenerateToken(Claims claims, long expireTime){
        String jwt = Jwts.builder()
                        .setClaims(claims)
                        .setIssuedAt(new Date(System.currentTimeMillis()))
                        .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                        .signWith(getSigningKey(SECRET_KEY), SignatureAlgorithm.HS256)
                        .compact();

        return jwt;
    }

    /**
     * JWT token 내 payload(body) 영역을 복호화 한다.
     * @param token
     * @return
     * @throws ExpiredJwtException
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * JWT token 내 payload(body) 영역 중 id를 return 한다.
     * @param token
     * @return
     */
    public String getUniqueidInToken(String token) {
        String _id = extractAllClaims(token).get("id", String.class);
        return _id;
    }

    /**
     * token 만료일이 유효한지 return 한다.
     * @param token
     * @return true: expired, false: not expired
     */
    public Boolean isTokenExpired(String token) {
        Date expiration = null;
        boolean returnVal = true;
        try {
            expiration = extractAllClaims(token).getExpiration();
            returnVal = expiration.before(new Date());
        } catch (Exception e) {
            returnVal = true;
        }
        return returnVal;
    }

    public Boolean isTokenExpiredDate(String token) {
        final Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    /**
     * token의 상태를 boolean으로 return한다. JWT내 Payload의 id와 만료일이 유효한지 확인한다.
     *
     * @param token
     * @param userMap
     * @return
     */
    public Boolean invaildToken(String token, Map userMap) {
        Boolean result = false;
        final String _id = getUniqueidInToken(token);
        if (_id.equals(userMap.get("_id")) && !isTokenExpired(token)) {
            result = true;
        }
        return result;
    }

    /**
     * token의 상태를 boolean으로 return한다. JWT내 Payload의 id와 만료일이 유효한지 확인한다.
     *
     * @param token
     * @return
     */
    public Boolean invaildTokenCheck(String token, String requestId) {
        Boolean result = false;
        final String response_id = getUniqueidInToken(token);
        if (response_id.equals(requestId) && !isTokenExpired(token)) {
            result = true;
        }
        return result;
    }
}