package com.hotel.filter;

import com.hotel.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器
 * 作用：拦截所有请求，解析并验证 JWT Token，将用户信息存入 Spring Security 上下文
 * 执行时机：在 Spring Security 的默认认证过滤器之前执行
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    
    /**
     * 核心过滤逻辑
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @param filterChain 过滤器链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
            FilterChain filterChain) throws ServletException, IOException {
        log.info("JWT 认证过滤器开始执行");
        // 1. 从请求头中获取 Authorization 字段
        String header = request.getHeader("Authorization");
        
        // 2. 检查 Token 是否存在且格式正确（以 "Bearer " 开头）
        if (header != null && header.startsWith("Bearer ")) {
            // 3. 提取 Token 字符串（去除 "Bearer " 前缀）
            String token = header.substring(7);
            
            // 4. 验证 Token 是否有效（未过期且签名正确）
            if (jwtUtil.isTokenValid(token)) {
                
                // 5. 从 Token 中解析用户信息
                Long userId = jwtUtil.getUserId(token);
                String username = jwtUtil.getUsername(token);
                Integer role = jwtUtil.getRole(token);
                
                // 6. 根据角色值生成权限标识（1 为管理员，其他为普通用户）
                // Spring Security 要求角色名必须以 "ROLE_" 开头
                String authority = role == 1 ? "ROLE_ADMIN" : "ROLE_USER";
                
                // 7. 构建认证令牌对象
                // 参数说明：(principal 主体信息, credentials 凭证, authorities 权限集合)
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userId, null, 
                        Collections.singletonList(new SimpleGrantedAuthority(authority)));
                
                // 8. 将认证信息存入安全上下文，供后续接口使用
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        // 9. 继续执行过滤器链中的下一个过滤器
        filterChain.doFilter(request, response);
    }
}
