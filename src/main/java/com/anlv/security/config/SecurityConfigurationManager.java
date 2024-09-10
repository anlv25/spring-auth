package com.anlv.security.config;

import com.anlv.security.rule.Rule;
import com.anlv.security.rule.RuleRedis;
import com.anlv.security.rule.RuleRedisRepository;
import com.anlv.security.rule.RuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityConfigurationManager {

    private final HttpSecurity http;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    private final RuleRepository ruleRepository;
    private final RuleRedisRepository ruleRedisRepository;

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

    public SecurityFilterChain updateSecurityConfiguration() throws Exception {


        List<RuleRedis> rules = new ArrayList<>();
        try {
            rules = (List<RuleRedis>) ruleRedisRepository.findAll();
        }catch (Exception e){
            log.error("REDIS: get data from redis", e);
        }

        if (rules.isEmpty()){
            rules = convertEntityToRedis(ruleRepository.findAll());
            ruleRedisRepository.saveAll(rules);
        }


        List<RuleRedis> finalRules = rules;
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->{
                            req.requestMatchers(WHITE_LIST_URL)
                                    .permitAll();
                            addRule(finalRules, req);
                        }

                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                )
        ;

        return http.build();
    }

    private void addRule(List<RuleRedis> finalRules, AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry req) {
        for (RuleRedis rule : finalRules) {
            if(rule.isCreateRequest()){
                req.requestMatchers(POST,rule.getExp_path()).hasRole(rule.getRole().getName());
            }
            if(rule.isUpdateRequest()){
                req.requestMatchers(PUT,rule.getExp_path()).hasRole(rule.getRole().getName());
            }
            if(rule.isReadRequest()){
                req.requestMatchers(GET,rule.getExp_path()).hasRole(rule.getRole().getName());
            }
            if(rule.isDeleteRequest()){
                req.requestMatchers(DELETE,rule.getExp_path()).hasRole(rule.getRole().getName());
            }
        }
        req.anyRequest().authenticated();
    }

    private  List<RuleRedis> convertEntityToRedis(List<Rule> rules) {
        List<RuleRedis> ruleRedises = new ArrayList<>();
        for (Rule rule : rules) {
            RuleRedis ruleRedis = new RuleRedis();
            ruleRedis.setId(rule.getId());
            ruleRedis.setExp_path(rule.getExp_path());
            ruleRedis.setExp_path_fe(rule.getExp_path_fe());
            ruleRedis.setRole(rule.getRole());
            ruleRedis.setCreateRequest(rule.isCreateRequest());
            ruleRedis.setUpdateRequest(rule.isUpdateRequest());
            ruleRedis.setReadRequest(rule.isReadRequest());
            ruleRedis.setDeleteRequest(rule.isDeleteRequest());
            ruleRedises.add(ruleRedis);
        }
        return ruleRedises;
    }
}
