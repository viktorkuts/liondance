package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.logiclayer.User.UserService;
import com.liondance.liondance_backend.utils.exceptions.NotFoundException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
}
