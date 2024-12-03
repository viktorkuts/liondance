package com.liondance.liondance_backend.presentationlayer.User;

import com.liondance.liondance_backend.datalayer.User.RegistrationStatus;
import com.liondance.liondance_backend.logiclayer.User.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("/api/v1/staff")
public class StaffController {
    private final UserService userService;

    public StaffController(UserService userService) {
        this.userService = userService;
    }


}
