package com.liondance.liondance_backend.logiclayer.User;

import com.liondance.liondance_backend.datalayer.User.UserRepository;
import com.liondance.liondance_backend.presentationlayer.User.UserResponseModel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Flux<UserResponseModel> getAllUsers() {
        return userRepository.findAll().map(UserResponseModel::from);
    }

}
