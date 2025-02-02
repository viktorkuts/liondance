package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.User.Role;
import com.liondance.liondance_backend.logiclayer.Event.EventService;
import com.liondance.liondance_backend.logiclayer.User.UserService;
import com.liondance.liondance_backend.presentationlayer.Event.EventResponseModel;
import com.liondance.liondance_backend.utils.exceptions.EmailInUse;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import com.liondance.liondance_backend.utils.exceptions.Unauthorized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/clients")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class ClientController {
    private final UserService userService;
    private final EventService eventService;

    public ClientController(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    @PreAuthorize("hasAuthority('STAFF')")
    @GetMapping
    public Flux<UserResponseModel> getAllClients(@AuthenticationPrincipal JwtAuthenticationToken jwt) {
        return userService.getAllUsers(Role.CLIENT);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public Mono<UserResponseModel> addClient(@AuthenticationPrincipal JwtAuthenticationToken jwt, @RequestBody Mono<UserRequestModel> userRequestModel) {
        return userService.validate(jwt.getName())
                .flatMap(user -> Mono.just(user).map(UserResponseModel::from))
                .onErrorResume(NotFoundException.class, ex -> Mono.empty())
                .switchIfEmpty(userService.addNewUser(Role.CLIENT.toString(), userRequestModel, jwt));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/current-client/events")
    public Flux<EventResponseModel> getEventsForUser(@AuthenticationPrincipal JwtAuthenticationToken jwt) {
        return userService.validate(jwt.getName())
                .flatMap(user -> {
                    if(!user.getRoles().contains(Role.CLIENT)) {
                        return Mono.error(new Unauthorized("User does not have the right roles to access this resource"));
                    }

                    return Mono.just(user.getUserId());
                })
                .flatMapMany(eventService::getEventsByClientId);
    }
}
