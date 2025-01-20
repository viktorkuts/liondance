//package com.liondance.liondance_backend;
//
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//
//@TestConfiguration
//@Profile("test")
//public class TestSecurityConfig {
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        http
//                .authorizeExchange(ex -> {
//                    ex.anyExchange().permitAll();
//                })
//                .csrf(ServerHttpSecurity.CsrfSpec::disable);
//        return http.build();
//    }
//}
