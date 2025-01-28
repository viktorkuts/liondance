package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.logiclayer.User.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api/v1/clients")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class ClientController {
    private final UserService userService;

    public ClientController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('STAFF')")
    @GetMapping
    public Flux<UserResponseModel> getAllClients(@AuthenticationPrincipal JwtAuthenticationToken jwt) {
        return userService.getAllUsers(Role.CLIENT);
    }
}
