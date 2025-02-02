package com.liondance.liondance_backend.utils;

import com.liondance.liondance_backend.datalayer.User.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClientConfigurer;

import java.util.stream.Collectors;

public class WebTestAuthConfig {
    public static WebTestClientConfigurer csrfConfig = SecurityMockServerConfigurers.csrf();

    public static WebTestClientConfigurer getAuthFor(User user){
        return SecurityMockServerConfigurers.mockJwt()
                .authorities(user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toSet()))
                .jwt(j -> j.claims(c -> c.put("sub", user.getAssociatedId())));
    }
}
