package com.hotel.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 工具类
 * 用于生成、解析和验证 JWT Token
 * 
 * JWT (JSON Web Token) 是一种无状态的身份认证机制，包含三部分：
 * - Header: 算法和类型
 * - Payload: 用户数据（userId, username, role等）
 * - Signature: 签名，防止篡改
 */
@Component
public class JwtUtil {
    
    /**
     * JWT 签名密钥
     * 从配置文件 application.yml 中读取 jwt.secret
     * 用于生成和验证 Token 签名，必须保密
     */
    @Value("${jwt.secret}")
    private String secret;
    
    /**
     * Token 过期时间（毫秒）
     * 从配置文件 application.yml 中读取 jwt.expiration
     * 例如：86400000 表示 24 小时
     */
    @Value("${jwt.expiration}")
    private Long expiration;
    
    /**
     * 获取签名密钥
     * 使用 HMAC-SHA256 算法将字符串密钥转换为 SecretKey 对象
     * 
     * @return SecretKey 签名密钥对象
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 生成 JWT Token
     * 
     * @param userId   用户ID
     * @param username 用户名
     * @param role     用户角色（0=普通用户，1=管理员）
     * @return JWT Token 字符串
     * 
     * Token 结构示例：
     * eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInVzZXJJZCI6MSwicm9sZSI6MX0.xxx
     * |---Header---| |--------Payload---------| |-Signature-|
     */
    public String generateToken(Long userId, String username, Integer role) {
        return Jwts.builder()
                .subject(username)                    // 设置主题（用户名）
                .claim("userId", userId)              // 添加自定义声明：用户ID
                .claim("role", role)                  // 添加自定义声明：角色
                .issuedAt(new Date())                 // 设置签发时间
                .expiration(new Date(System.currentTimeMillis() + expiration))  // 设置过期时间
                .signWith(getSigningKey())            // 使用密钥进行 HMAC-SHA256 签名
                .compact();                           // 生成紧凑的 JWT 字符串
    }
    
    /**
     * 解析 JWT Token
     * 验证签名并提取 Payload 中的声明信息
     * 
     * @param token JWT Token 字符串
     * @return Claims 对象，包含所有声明信息
     * @throws io.jsonwebtoken.ExpiredJwtException      Token 已过期
     * @throws io.jsonwebtoken.SignatureException       签名验证失败
     * @throws io.jsonwebtoken.MalformedJwtException    Token 格式错误
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())        // 设置验证密钥
                .build()
                .parseSignedClaims(token)           // 解析并验证签名
                .getPayload();                      // 获取 Payload 部分
    }
    
    /**
     * 从 Token 中获取用户ID
     * 
     * @param token JWT Token 字符串
     * @return 用户ID
     */
    public Long getUserId(String token) {
        return parseToken(token).get("userId", Long.class);
    }
    
    /**
     * 从 Token 中获取用户名
     * 
     * @param token JWT Token 字符串
     * @return 用户名（subject 字段）
     */
    public String getUsername(String token) {
        return parseToken(token).getSubject();
    }
    
    /**
     * 从 Token 中获取用户角色
     * 
     * @param token JWT Token 字符串
     * @return 用户角色（0=普通用户，1=管理员）
     */
    public Integer getRole(String token) {
        return parseToken(token).get("role", Integer.class);
    }
    
    /**
     * 验证 Token 是否有效
     * 检查 Token 是否过期以及格式是否正确
     * 
     * @param token JWT Token 字符串
     * @return true=有效，false=无效或已过期
     */
    public boolean isTokenValid(String token) {
        try {
            // 检查过期时间是否在当前时间之后
            return !parseToken(token).getExpiration().before(new Date());
        } catch (Exception e) {
            // 捕获所有异常（过期、签名错误、格式错误等）
            return false;
        }
    }
}
