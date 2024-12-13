package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.logiclayer.User.UserService;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Flux<UserResponseModel> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("{userId}")
    public Mono<UserResponseModel> getUserByUserId(@PathVariable String userId) {
        return userService.getUserByUserId(userId)
                .switchIfEmpty(Mono.error(new NotFoundException("User not found with id: " + userId)));
    }
    @PutMapping("{userId}")
    public Mono<UserResponseModel> updateUser(@PathVariable String userId, @RequestBody UserRequestModel userRequestModel) {
        return userService.updateUser(userId, userRequestModel);
    }

    @PostMapping()
    public Mono<UserResponseModel> addNewUser(@Valid @RequestBody Mono<UserRequestModel> userRequestModel, @RequestParam String role) {
        role.toUpperCase();
        return userService.addNewUser(role, userRequestModel);
    }
    @PatchMapping("{userId}/role")
    public Mono<UserResponseModel> updateUserRole(@PathVariable String userId, @RequestBody UserRolePatchRequestModel role) {
        return userService.updateUserRole(userId, role);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/current-user/roles")
    public Mono<EnumSet<Role>> getSessionRoles(@AuthenticationPrincipal JwtAuthenticationToken jwt){
        EnumSet<Role> roles = EnumSet.noneOf(Role.class);
        jwt.getAuthorities().forEach(role -> {
            roles.add(Role.valueOf(role.getAuthority()));
        });

        return Mono.just(roles);
    }
}
