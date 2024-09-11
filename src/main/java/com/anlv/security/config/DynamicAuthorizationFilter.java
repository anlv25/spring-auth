package com.anlv.security.config;

import com.anlv.security.rule.RuleRedis;
import com.anlv.security.rule.RuleRedisRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;
import java.util.Arrays;

public class DynamicAuthorizationFilter extends OncePerRequestFilter {

    private final RuleRedisRepository ruleRedisRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final String[] WHITE_LIST_URL = {"/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html",
            "/actuator/**",
            "/rules"};

    public DynamicAuthorizationFilter(RuleRedisRepository ruleRedisRepository) {
        this.ruleRedisRepository = ruleRedisRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();

        if (isWhitelisted(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        String method = request.getMethod();

        List<RuleRedis> rules = (List<RuleRedis>) ruleRedisRepository.findAll();

        boolean isAuthorized = checkAuthorization(rules, requestPath, method);

        if (!isAuthorized) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("Access Denied");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isWhitelisted(String requestPath) {
        return Arrays.stream(WHITE_LIST_URL)
                     .anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }

    private boolean checkAuthorization(List<RuleRedis> rules, String requestPath, String method) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        for (RuleRedis rule : rules) {
            if (pathMatcher.match(rule.getExp_path(), requestPath)) {
                switch (method) {
                    case "GET":
                        return rule.isReadRequest() && authentication.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_" + rule.getRole().getName()));
                    case "POST":
                        return rule.isCreateRequest() && authentication.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_" + rule.getRole().getName()));
                    case "PUT":
                        return rule.isUpdateRequest() && authentication.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_" + rule.getRole().getName()));
                    case "DELETE":
                        return rule.isDeleteRequest() && authentication.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals("ROLE_" + rule.getRole().getName()));
                }
            }
        }

        return false;
    }
}
