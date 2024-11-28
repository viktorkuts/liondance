package com.liondance.liondance_backend.logiclayer.User;

import com.liondance.liondance_backend.presentationlayer.User.UserResponseModel;
import reactor.core.publisher.Flux;

public interface UserService {
    Flux<UserResponseModel> getAllUsers();
}
