package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.User.RegistrationStatus;
import com.liondance.liondance_backend.logiclayer.User.UserService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("/api/v1/staff")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8080"})
public class StaffController {
    private final UserService userService;

    public StaffController(UserService userService) {
        this.userService = userService;
    }


}
