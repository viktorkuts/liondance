package com.liondance.liondance_backend.utils.security;

import com.liondance.liondance_backend.logiclayer.User.UserService;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
    @Value("${spring.security.oauth2.client.provider.okta.issuer-uri}")
    private String issuer;
    @Value("${liondance.frontend.url}")
    private String frontend;
    @Value("${spring.security.oauth2.client.registration.okta.client-id}")
    private String clientId;
    @Autowired
    private Environment env;
    private final UserService userService;
    @Autowired
    private ReactiveClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(ex -> ex.anyExchange()
                        .permitAll())
                .cors(cors -> cors.configurationSource(corsConfig()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .oauth2ResourceServer(server -> {
                    server.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()));
                })
                .oidcLogout(logout -> logout.backChannel(Customizer.withDefaults()))
                .logout(logout -> logout.logoutSuccessHandler(logoutHandler()));
        return http.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {
        ReactiveJwtAuthenticationConverter converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
        return converter;
    }

    private Converter<Jwt, Flux<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
        JwtGrantedAuthoritiesConverter delegate = new JwtGrantedAuthoritiesConverter();
        return (jwt) -> getUserInfo(jwt.getTokenValue())
                .flatMapMany((userInfo) -> {
                    return userService.validate(jwt.getClaim("sub"))
                            .onErrorResume(NotFoundException.class, e -> Mono.empty())
                            .flatMapMany(user -> Flux.fromStream(user.getRoles().stream()
                                    .map(role -> new SimpleGrantedAuthority(role.name()))));
                });
    }

    private Mono<Map<String, String>> getUserInfo(String accessToken) {
        return Mono.just(new HashMap<>());
    }

    private ServerLogoutSuccessHandler logoutHandler(){
        OidcClientInitiatedServerLogoutSuccessHandler oidcLogoutSuccessHandler =
                new OidcClientInitiatedServerLogoutSuccessHandler(this.clientRegistrationRepository);

        // Sets the location that the End-User's User Agent will be redirected to
        // after the logout has been performed at the Provider
        oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}");

        return oidcLogoutSuccessHandler;
    }

    @Bean
    public CorsWebFilter corsWebFilter(){
        return new CorsWebFilter(corsConfig());
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfig(){
        String deploymentBranch = env.getProperty("COOLIFY_BRANCH");
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin(frontend);
        config.addAllowedOrigin(issuer.substring(0, issuer.length() - 1));
        if(deploymentBranch != null && deploymentBranch.split("/").length > 1 && deploymentBranch.split("/")[1].matches("\\d+")){
            config.addAllowedOrigin(frontend.replace("://", "://" + deploymentBranch.split("/")[1] + "."));
        }
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", config);
        return src;
    }
}