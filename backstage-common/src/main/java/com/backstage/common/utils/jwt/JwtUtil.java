package com.backstage.common.utils.jwt;

import com.backstage.common.constant.OshUserConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/30
 * Time: 13:33
 */
@Component
public class JwtUtils {
    // 令牌秘钥

    private static String secret;
    @Value("${token.secret}")
    public void setSecret(String secret) {
        JwtUtils.secret = secret;
    }

    public static String createToken(Map<String, Object> claims)
    {
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    public static Claims parseToken(String token) {
        try {
            // 使用相同的secret解析token
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        } catch (SignatureException e) {
            // 签名验证失败
            throw new RuntimeException("Token签名验证失败", e);
        } catch (Exception e) {
            // token过期或其他解析错误
            throw new RuntimeException("Token解析失败", e);
        }
    }

    public static Long getUserIdByToken(String token) {
        Claims claims = parseToken(token);
        return claims.get(OshUserConstants.USER_ID, Long.class);
    }
}
