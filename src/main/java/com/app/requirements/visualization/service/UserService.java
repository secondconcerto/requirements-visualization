package com.app.requirements.visualization.service;

import com.app.requirements.visualization.model.User;
import com.app.requirements.visualization.web.dto.UserRegistrationDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    User save(UserRegistrationDto registrationDto);

}
