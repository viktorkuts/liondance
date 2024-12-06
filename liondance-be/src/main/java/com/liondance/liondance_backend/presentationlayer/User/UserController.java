package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.logiclayer.User.UserService;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
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
}
